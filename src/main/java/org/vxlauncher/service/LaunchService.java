//////////////////////////////////////////////////////
//// @File service/LaunchService.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.service;

import org.vxlauncher.AppInfo;
import org.vxlauncher.model.OSType;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

public class LaunchService
{
    private final FileService fileService;
    private final OSType osType;
    private final String versionsDir;

    public LaunchService(FileService fileService, OSType osType, String versionsDir)
    {
        this.fileService = fileService;
        this.osType = osType;
        this.versionsDir = versionsDir;
    }

    public void launch(String version, Consumer<String> logger)
            throws Exception
    {
        logger.accept("Запуск " + AppInfo.getGameName() + " версии " + version);

        String versionDir = versionsDir + File.separator + version;

        ProcessBuilder pb;

        switch (osType)
        {
            case WINDOWS: {
                File exeFile = fileService.findExecutable(versionDir, ".exe");
                if (exeFile == null) throw new Exception("Исполняемый файл не найден");
                pb = new ProcessBuilder(exeFile.getAbsolutePath());
                break;
            }

            case MACOS: {
                logger.accept("Для macOS требуется ручная установка .dmg файла");
                return;
            }

            case LINUX: {
                logger.accept("Поиск AppImage в: " + versionDir);
                File appImage = fileService.findExecutable(versionDir, ".AppImage");

                if (appImage == null) {
                    logger.accept("Содержимое директории: " + Arrays.toString(new File(versionDir).list()));
                    throw new Exception("AppImage не найден в " + versionDir);
                }

                logger.accept("Найден AppImage: " + appImage.getAbsolutePath());
                fileService.setExecutable(appImage.toPath());
                pb = new ProcessBuilder(appImage.getAbsolutePath());
                break;
            }

            default: {
                throw new Exception("Неподдерживаемая ОС");
            }
        }

        pb.directory(new File(versionDir));
        Process process = pb.start();
        logger.accept("Игра запущена (PID: " + process.pid() + ")");
    }
}