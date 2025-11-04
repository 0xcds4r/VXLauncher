//////////////////////////////////////////////////////
//// @File service/InstallationService.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.service;

import org.vxlauncher.model.OSType;
import org.vxlauncher.model.ReleaseInfo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class InstallationService
{
    private final FileService fileService;
    private final DownloadService downloadService;
    private final OSType osType;
    private final String versionsDir;

    public InstallationService(FileService fileService, DownloadService downloadService, OSType osType, String versionsDir)
    {
        this.fileService = fileService;
        this.downloadService = downloadService;
        this.osType = osType;
        this.versionsDir = versionsDir;
    }

    public void install(ReleaseInfo release, Consumer<String> logger, Consumer<Integer> progressCallback)
            throws Exception
    {
        String versionDir = versionsDir + java.io.File.separator + release.getVersion();
        fileService.createDirectories(versionDir);

        Path downloadPath = Paths.get(versionDir, release.getFileName());

        logger.accept("Загрузка: " + release.getDownloadUrl());
        downloadService.downloadFile(release.getDownloadUrl(), downloadPath, progressCallback);

        if (!release.getSha256().isEmpty())
        {
            logger.accept("Проверка контрольной суммы...");
            String calculatedHash = fileService.calculateSHA256(downloadPath);

            if (!calculatedHash.equalsIgnoreCase(release.getSha256())) {
                throw new Exception("Контрольная сумма не совпадает!");
            }

            logger.accept("Контрольная сумма проверена ✓");
        }

        if (osType == OSType.WINDOWS && release.getFileName().endsWith(".zip"))
        {
            logger.accept("Распаковка архива...");

            fileService.unzip(downloadPath, Paths.get(versionDir));
            fileService.deleteFile(downloadPath);
        }
        else if (osType == OSType.LINUX && release.getFileName().endsWith(".AppImage"))
        {
            logger.accept("Установка прав на исполнение...");

            if(fileService.setExecutable(downloadPath))
            {
                logger.accept("Установка прав на исполнение - успех");
            }
            else
            {
                logger.accept("Установка прав на исполнение - провал");
            }
        }
    }

    public boolean isVersionInstalled(String version)
    {
        String versionDir = versionsDir + java.io.File.separator + version;
        return fileService.directoryExists(versionDir) && !fileService.isDirectoryEmpty(versionDir);
    }
}