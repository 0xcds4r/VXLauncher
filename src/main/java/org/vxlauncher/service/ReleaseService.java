//////////////////////////////////////////////////////
//// @File service/ReleaseService.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.vxlauncher.model.OSType;
import org.vxlauncher.model.ReleaseInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ReleaseService
{
    private static final String GITHUB_API = "https://api.github.com/repos/MihailRis/voxelcore/releases";
    private static final String BASE_URL = "https://github.com/MihailRis/voxelcore/releases/download/";

    private final OSType osType;

    public ReleaseService(OSType osType) {
        this.osType = osType;
    }

    public Map<String, ReleaseInfo> loadReleasesFromGitHub()
    {
        Map<String, ReleaseInfo> releases = new LinkedHashMap<>();

        try {
            System.out.println("--> Запрос к GitHub API: " + GITHUB_API);
            String json = fetchGitHubReleases();
            System.out.println("--> Получено JSON длиной: " + json.length() + " символов");

            releases = parseGitHubReleases(json);
            System.out.println("--> Распарсено релизов: " + releases.size());

            if (releases.isEmpty())
            {
                System.out.println("--> Релизы не найдены, используем fallback");
                releases = loadKnownReleases();
            }
        } catch (Exception e)
        {
            System.err.println("--> Ошибка загрузки релизов из GitHub: " + e.getMessage());
            e.printStackTrace();
            releases = loadKnownReleases();
        }

        return releases;
    }

    private String fetchGitHubReleases()
            throws Exception
    {
        URL url = new URL(GITHUB_API);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
        conn.setRequestProperty("User-Agent", "VXLauncher");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("--> GitHub API вернул код: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        in.close();

        return response.toString();
    }

    private Map<String, ReleaseInfo> parseGitHubReleases(String json)
    {
        Map<String, ReleaseInfo> releases = new LinkedHashMap<>();

        try {
            JsonArray releasesArray = JsonParser.parseString(json).getAsJsonArray();
            System.out.println("--> Найдено релизов в JSON: " + releasesArray.size());

            for (JsonElement releaseElement : releasesArray)
            {
                JsonObject release = releaseElement.getAsJsonObject();

                if (release.has("draft") && release.get("draft").getAsBoolean()) {
                    continue;
                }

                String tagName = release.get("tag_name").getAsString();
                String version = tagName.startsWith("v") ? tagName.substring(1) : tagName;

                System.out.println("--> Обработка релиза: " + version);

                JsonArray assets = release.getAsJsonArray("assets");
                if (assets == null || assets.isEmpty()) {
                    System.out.println("--> Нет assets для релиза " + version);
                    continue;
                }

                for (JsonElement assetElement : assets)
                {
                    JsonObject asset = assetElement.getAsJsonObject();
                    String fileName = asset.get("name").getAsString();

                    System.out.println("--> Проверяем asset: " + fileName);

                    boolean matches = switch (osType) {
                        case WINDOWS -> fileName.contains("win64") && fileName.endsWith(".zip");
                        case MACOS -> fileName.contains("macos") && fileName.endsWith(".dmg");
                        case LINUX -> fileName.endsWith(".AppImage");
                    };

                    if (matches)
                    {
                        String downloadUrl = asset.get("browser_download_url").getAsString();
                        long size = asset.get("size").getAsLong();

                        System.out.println("--> Найден подходящий файл: " + fileName);

                        ReleaseInfo info = new ReleaseInfo(version, downloadUrl, "", size, fileName);
                        releases.put(version, info);
                        break;
                    }
                }
            }

        } catch (Exception e)
        {
            System.err.println("Error parse JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return releases;
    }

    /////////////////////////////////////////////////////////
    //// @Fallback known releases
    /////////////////////////////////////////////////////////
    public Map<String, ReleaseInfo> loadKnownReleases()
    {
        Map<String, ReleaseInfo> releases = new LinkedHashMap<>();

        ///////////////////////
        //// 0.29.3 based! ///
        /////////////////////
        switch (osType)
        {
            case WINDOWS:
                releases.put("0.29.3", new ReleaseInfo(
                        "0.29.3",
                        BASE_URL + "v0.29.3/voxelcore.0.29.3_win64.zip",
                        "dc6325e950bcb15878cd6be80f21693b36886064313ad40ac4800832fffefef6",
                        6490112,
                        "voxelcore.0.29.3_win64.zip"
                ));
                break;
            case MACOS:
                releases.put("0.29.3", new ReleaseInfo(
                        "0.29.3",
                        BASE_URL + "v0.29.3/voxelcore.0.29.3_macos.dmg",
                        "06b2bdc3cf25db58de9349471efa473412c2bbb997b352995301866cf1ab371d",
                        4536320,
                        "voxelcore.0.29.3_macos.dmg"
                ));
                break;
            case LINUX:
                releases.put("0.29.3", new ReleaseInfo(
                        "0.29.3",
                        BASE_URL + "v0.29.3/voxelcore.0.29.3_x86-64.AppImage",
                        "d17c90d482695c1fbb6e07298e8e157a4c061ff2d9cfde9abdbf057d63ff9e9e",
                        67842867,
                        "voxelcore.0.29.3_x86-64.AppImage"
                ));
                break;
        }

        return releases;
    }
}