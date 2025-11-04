//////////////////////////////////////////////////////
//// @File model/ReleaseInfo.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.model;

public class ReleaseInfo
{
    private final String version;
    private final String downloadUrl;
    private final String sha256;
    private final long size;
    private final String fileName;

    public ReleaseInfo(String version, String downloadUrl, String sha256, long size, String fileName)
    {
        this.version = version;
        this.downloadUrl = downloadUrl;
        this.sha256 = sha256;
        this.size = size;
        this.fileName = fileName;
    }

    public String getVersion() { return version; }
    public String getDownloadUrl() { return downloadUrl; }
    public String getSha256() { return sha256; }
    public long getSize() { return size; }
    public String getFileName() { return fileName; }

    @Override
    public String toString() {
        return version;
    }
}