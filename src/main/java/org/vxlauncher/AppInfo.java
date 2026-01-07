//////////////////////////////////////////////////////
//// @File AppInfo.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher;

import java.io.File;

public final class AppInfo
{
    private static final String APP_NAME = "VX Launcher";
    private static final String GAME_NAME = "voxelcore";

    private static final String APP_VERSION = "1.2";

    private static final int APP_WIDTH = 700;
    private static final int APP_HEIGHT = 600;

    private static final String APP_DIR = System.getProperty("user.home") + File.separator + APP_NAME.replace(" ", "");
    private static final String VERSIONS_DIR = APP_DIR + File.separator + "versions";

    private static final String CONTENT_DIR = System.getProperty("user.home") + "/.config/" + GAME_NAME + "/content";
    private static final String WORLDS_DIR  = System.getProperty("user.home") + "/.config/" + GAME_NAME + "/worlds";

    private AppInfo() {
        throw new UnsupportedOperationException("AppInfo is a utility class and cannot be instantiated");
    }

    public static String getAppName() {
        return APP_NAME;
    }

    public static int getWidth() {
        return APP_WIDTH;
    }

    public static int getHeight() {
        return APP_HEIGHT;
    }

    public static String getAppDir() {
        return APP_DIR;
    }

    public static String getVersionsDir() {
        return VERSIONS_DIR;
    }

    public static String getContentsDir() {
        return CONTENT_DIR;
    }

    public static String getWorldsDir() {
        return WORLDS_DIR;
    }

    public static String getGameName() {
        return GAME_NAME;
    }

    public static String getWindowTitle() {
        return APP_NAME + " - v" + APP_VERSION;
    }

    public static String getAbout() {
        return "Лаунчер для VoxelCore | by 0xcds4r";
    }
}