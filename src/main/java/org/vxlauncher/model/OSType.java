//////////////////////////////////////////////////////
//// @File model/OSType.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.model;

public enum OSType
{
    WINDOWS("windows", ".exe"),
    MACOS("macos", ".app"),
    LINUX("linux", ".AppImage");

    private final String name;
    private final String executableExtension;

    OSType(String name, String executableExtension) {
        this.name = name;
        this.executableExtension = executableExtension;
    }

    public String getName() { return name; }
    public String getExecutableExtension() { return executableExtension; }

    public static OSType detectCurrent()
    {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return WINDOWS;
        if (os.contains("mac")) return MACOS;
        return LINUX;
    }
}