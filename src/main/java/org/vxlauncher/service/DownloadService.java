//////////////////////////////////////////////////////
//// @File service/DownloadService.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DownloadService
{
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .executor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
            .build();

    private static final int BUFFER_SIZE = 256 * 1024; // 256 KB
    private static final long CHUNK_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int MAX_RETRIES = 5;

    public CompletableFuture<Void> downloadAsync(
            String urlStr,
            Path destination,
            Consumer<Integer> progressCallback)
    {
        return CompletableFuture
                .supplyAsync(() -> fetchMetadata(urlStr))
                .thenCompose(meta ->
                {
                    if (meta.supportsRanges && meta.size > CHUNK_SIZE * 2)
                    {
                        return downloadMultiThreaded(urlStr, destination, meta.size, progressCallback);
                    }
                    else
                    {
                        return downloadSingleThreaded(urlStr, destination, meta.size, progressCallback);
                    }
                })
                .exceptionally(e -> {
                    throw new RuntimeException("Download failed: " + e.getMessage(), e);
                });
    }

    private CompletableFuture<Void> downloadSingleThreaded(
            String urlStr,
            Path destination,
            long totalSize,
            Consumer<Integer> progressCallback)
    {
        return retryAsync(() ->
        {
            HttpRequest req = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlStr))
                    .header("User-Agent", "VXLauncher/1.2")
                    .timeout(Duration.ofMinutes(10))
                    .build();

            return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofInputStream())
                    .thenAccept(resp ->
                    {
                        if (resp.statusCode() != 200) {
                            throw new RuntimeException("HTTP " + resp.statusCode());
                        }

                        long size = totalSize > 0 ? totalSize :
                                resp.headers().firstValue("content-length")
                                        .map(Long::parseLong).orElse(-1L);

                        downloadWithNIO(resp.body(), destination, size, progressCallback);
                    });
        });
    }

    private CompletableFuture<Void> downloadMultiThreaded(
            String urlStr,
            Path destination,
            long totalSize,
            Consumer<Integer> progressCallback) {

        int numThreads = Math.min(8, (int) ((totalSize + CHUNK_SIZE - 1) / CHUNK_SIZE));
        long chunkSize = totalSize / numThreads;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicLong downloaded = new AtomicLong(0);

        try {
            Files.createDirectories(destination.getParent());
            try (FileChannel fc = FileChannel.open(destination,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                fc.position(totalSize - 1);
                fc.write(ByteBuffer.wrap(new byte[]{0}));
            }
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }

        for (int i = 0; i < numThreads; i++)
        {
            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? totalSize - 1 : start + chunkSize - 1;

            CompletableFuture<Void> future = downloadChunk(urlStr, destination, start, end, downloaded, totalSize, progressCallback);
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> downloadChunk(
            String urlStr,
            Path destination,
            long start,
            long end,
            AtomicLong downloaded,
            long totalSize,
            Consumer<Integer> progressCallback)
    {
        return retryAsync(() -> {
            HttpRequest req = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlStr))
                    .header("User-Agent", "VXLauncher/1.2")
                    .header("Range", "bytes=" + start + "-" + end)
                    .timeout(Duration.ofMinutes(10))
                    .build();

            return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofInputStream())
                    .thenAccept(resp ->
                    {
                        if (resp.statusCode() != 206 && resp.statusCode() != 200)
                        {
                            throw new RuntimeException("HTTP " + resp.statusCode());
                        }

                        try (InputStream in = resp.body();
                             ReadableByteChannel rbc = Channels.newChannel(in);
                             FileChannel out = FileChannel.open(destination, StandardOpenOption.WRITE)) {

                            out.position(start);
                            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

                            int read;
                            while ((read = rbc.read(buffer)) != -1)
                            {
                                buffer.flip();

                                while (buffer.hasRemaining()) {
                                    out.write(buffer);
                                }

                                buffer.clear();

                                if (progressCallback != null)
                                {
                                    long current = downloaded.addAndGet(read);
                                    int pct = (int) (current * 100 / totalSize);
                                    progressCallback.accept(Math.min(pct, 100));
                                }
                            }
                        } catch (IOException e)
                        {
                            throw new RuntimeException("I/O error in chunk download", e);
                        }
                    });
        });
    }

    private void downloadWithNIO(
            InputStream in,
            Path destination,
            long totalSize,
            Consumer<Integer> progressCallback)
    {
        try (ReadableByteChannel rbc = Channels.newChannel(in);
             FileChannel out = FileChannel.open(destination,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.WRITE,
                     StandardOpenOption.TRUNCATE_EXISTING))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            long downloaded = 0;
            int read;

            while ((read = rbc.read(buffer)) != -1)
            {
                buffer.flip();

                while (buffer.hasRemaining()) {
                    out.write(buffer);
                }

                buffer.clear();
                downloaded += read;

                if (totalSize > 0 && progressCallback != null)
                {
                    int pct = (int) (downloaded * 100 / totalSize);
                    progressCallback.accept(pct);
                }
            }

            if (progressCallback != null) progressCallback.accept(100);

        } catch (IOException e)
        {
            throw new RuntimeException("I/O error", e);
        }
    }

    private <T> CompletableFuture<T> retryAsync(
            Supplier<CompletableFuture<T>> supplier)
    {
        CompletableFuture<T> result = supplier.get();

        for (int i = 0; i < DownloadService.MAX_RETRIES; i++)
        {
            final int attempt = i;

            result = result.exceptionallyCompose(ex ->
            {
                if (attempt < DownloadService.MAX_RETRIES - 1)
                {
                    try
                    {
                        Thread.sleep((long) Math.pow(2, attempt) * 1000);
                    } catch (InterruptedException ie)
                    {
                        Thread.currentThread().interrupt();
                    }

                    return supplier.get();
                }

                return CompletableFuture.failedFuture(ex);
            });
        }

        return result;
    }

    public void downloadFile(String url, Path dest, Consumer<Integer> cb) {
        downloadAsync(url, dest, cb).join();
    }

    private DownloadMetadata fetchMetadata(String urlStr)
    {
        try
        {
            HttpRequest head = HttpRequest.newBuilder()
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(urlStr))
                    .header("User-Agent", "VXLauncher/1.2")
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<Void> resp = CLIENT.send(head, HttpResponse.BodyHandlers.discarding());

            long size = resp.headers()
                    .firstValue("content-length")
                    .map(Long::parseLong)
                    .orElse(-1L);

            boolean supportsRanges = resp.headers()
                    .firstValue("accept-ranges")
                    .map(v -> v.equalsIgnoreCase("bytes"))
                    .orElse(false);

            return new DownloadMetadata(size, supportsRanges);
        } catch (Exception e)
        {
            return new DownloadMetadata(-1L, false);
        }
    }

    private record DownloadMetadata(long size, boolean supportsRanges) {}
}