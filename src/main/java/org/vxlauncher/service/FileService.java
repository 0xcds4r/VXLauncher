//////////////////////////////////////////////////////
//// @File service/FileService.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.service;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileService
{
    public void createDirectories(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    public boolean directoryExists(String path)
    {
        File dir = new File(path);

        return dir.exists() && dir.isDirectory();
    }

    public boolean isDirectoryEmpty(String path)
    {
        File dir = new File(path);
        String[] contents = dir.list();

        return contents == null || contents.length == 0;
    }

    public void unzip(Path zipFile, Path destDir) throws IOException
    {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile())))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                Path newPath = destDir.resolve(entry.getName());

                if (entry.isDirectory())
                {
                    Files.createDirectories(newPath);
                }
                else
                {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }

    public String calculateSHA256(Path file) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream fis = Files.newInputStream(file))
        {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash)
        {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1)
                hexString.append('0');

            hexString.append(hex);
        }

        return hexString.toString();
    }

    public File findExecutable(String dir, String extension)
    {
        File directory = new File(dir);

        File[] allFiles = directory.listFiles();
        if (allFiles != null)
        {
            for (File file : allFiles)
            {
                if (file.isFile() && file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                    if (!file.getName().equalsIgnoreCase("vctest.exe")) {
                        return file;
                    }
                }
            }
        }

        if (allFiles != null)
        {
            for (File subdir : allFiles)
            {
                if (subdir.isDirectory())
                {
                    File found = findExecutable(subdir.getAbsolutePath(), extension);

                    if (found != null) {
                        return found;
                    }
                }
            }
        }

        return null;
    }

    public boolean setExecutable(Path file)
    {
        return file.toFile().setExecutable(true);
    }

    public void deleteFile(Path file)
            throws IOException
    {
        Files.delete(file);
    }
}