//////////////////////////////////////////////////////
//// @File ui/FontManager.java
//// @Author 0xcds4r
//// @Date 08 Jan. 2026
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager
{
    private static final Map<String, Font> fontCache = new HashMap<>();
    private static final int DEFAULT_SIZE = 12;

    public static void loadFonts() {
        try {
            loadFont("segoe-ui-regular", "Segoe UI.ttf");
            loadFont("segoe-ui-bold", "Segoe UI Bold.ttf");
            loadFont("segoe-ui-italic", "Segoe UI Italic.ttf");
            loadFont("segoe-ui-bold-italic", "Segoe UI Bold Italic.ttf");

            loadFont("jetbrains-mono-regular", "JetBrainsMono-Regular.ttf");
            loadFont("jetbrains-mono-bold", "JetBrainsMono-Bold.ttf");
            loadFont("jetbrains-mono-italic", "JetBrainsMono-Italic.ttf");
            loadFont("jetbrains-mono-bold-italic", "JetBrainsMono-BoldItalic.ttf");
            loadFont("jetbrains-mono-light", "JetBrainsMono-Light.ttf");
            loadFont("jetbrains-mono-light-italic", "JetBrainsMono-LightItalic.ttf");
            loadFont("jetbrains-mono-medium", "JetBrainsMono-Medium.ttf");
            loadFont("jetbrains-mono-medium-italic", "JetBrainsMono-MediumItalic.ttf");
            loadFont("jetbrains-mono-semibold", "JetBrainsMono-SemiBold.ttf");
            loadFont("jetbrains-mono-semibold-italic", "JetBrainsMono-SemiBoldItalic.ttf");
            loadFont("jetbrains-mono-extrabold", "JetBrainsMono-ExtraBold.ttf");
            loadFont("jetbrains-mono-extrabold-italic", "JetBrainsMono-ExtraBoldItalic.ttf");
            loadFont("jetbrains-mono-extralight", "JetBrainsMono-ExtraLight.ttf");
            loadFont("jetbrains-mono-extralight-italic", "JetBrainsMono-ExtraLightItalic.ttf");
            loadFont("jetbrains-mono-thin", "JetBrainsMono-Thin.ttf");
            loadFont("jetbrains-mono-thin-italic", "JetBrainsMono-ThinItalic.ttf");

            loadFont("jetbrains-mono-nl-regular", "JetBrainsMonoNL-Regular.ttf");
            loadFont("jetbrains-mono-nl-bold", "JetBrainsMonoNL-Bold.ttf");
            loadFont("jetbrains-mono-nl-italic", "JetBrainsMonoNL-Italic.ttf");
            loadFont("jetbrains-mono-nl-bold-italic", "JetBrainsMonoNL-BoldItalic.ttf");
            loadFont("jetbrains-mono-nl-light", "JetBrainsMonoNL-Light.ttf");
            loadFont("jetbrains-mono-nl-light-italic", "JetBrainsMonoNL-LightItalic.ttf");
            loadFont("jetbrains-mono-nl-medium", "JetBrainsMonoNL-Medium.ttf");
            loadFont("jetbrains-mono-nl-medium-italic", "JetBrainsMonoNL-MediumItalic.ttf");
            loadFont("jetbrains-mono-nl-semibold", "JetBrainsMonoNL-SemiBold.ttf");
            loadFont("jetbrains-mono-nl-semibold-italic", "JetBrainsMonoNL-SemiBoldItalic.ttf");
            loadFont("jetbrains-mono-nl-extrabold", "JetBrainsMonoNL-ExtraBold.ttf");
            loadFont("jetbrains-mono-nl-extrabold-italic", "JetBrainsMonoNL-ExtraBoldItalic.ttf");
            loadFont("jetbrains-mono-nl-extralight", "JetBrainsMonoNL-ExtraLight.ttf");
            loadFont("jetbrains-mono-nl-extralight-italic", "JetBrainsMonoNL-ExtraLightItalic.ttf");
            loadFont("jetbrains-mono-nl-thin", "JetBrainsMonoNL-Thin.ttf");
            loadFont("jetbrains-mono-nl-thin-italic", "JetBrainsMonoNL-ThinItalic.ttf");

            System.out.println("✓ Шрифты загружены: " + fontCache.size() + " шт.");

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки шрифтов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadFont(String key, String filename) throws IOException, FontFormatException
    {
        String resourcePath = "/fonts/" + filename;

        try (InputStream is = FontManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("⚠ Шрифт не найден в ресурсах: " + resourcePath);
                return;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            fontCache.put(key.toLowerCase(), font);
            System.out.println("✓ Загружен шрифт: " + key);
        }
    }

    public static Font getFont(String fontName, int size) {
        Font font = fontCache.get(fontName.toLowerCase());
        if (font == null) {
            System.err.println("⚠ Шрифт '" + fontName + "' не найден, используется системный шрифт");
            return new Font("Dialog", Font.PLAIN, size);
        }
        return font.deriveFont((float) size);
    }

    public static Font getFont(String fontName, int style, int size) {
        Font font = getFont(fontName, size);
        return font.deriveFont(style, (float) size);
    }

    public static Font getFont(String fontName) {
        return getFont(fontName, DEFAULT_SIZE);
    }

    public static final String SEGOE_UI_REGULAR = "segoe-ui-regular";
    public static final String SEGOE_UI_BOLD = "segoe-ui-bold";
    public static final String SEGOE_UI_ITALIC = "segoe-ui-italic";
    public static final String SEGOE_UI_BOLD_ITALIC = "segoe-ui-bold-italic";

    public static final String JETBRAINS_MONO_REGULAR = "jetbrains-mono-regular";
    public static final String JETBRAINS_MONO_BOLD = "jetbrains-mono-bold";
    public static final String JETBRAINS_MONO_ITALIC = "jetbrains-mono-italic";
    public static final String JETBRAINS_MONO_BOLD_ITALIC = "jetbrains-mono-bold-italic";
    public static final String JETBRAINS_MONO_LIGHT = "jetbrains-mono-light";
    public static final String JETBRAINS_MONO_LIGHT_ITALIC = "jetbrains-mono-light-italic";
    public static final String JETBRAINS_MONO_MEDIUM = "jetbrains-mono-medium";
    public static final String JETBRAINS_MONO_MEDIUM_ITALIC = "jetbrains-mono-medium-italic";
    public static final String JETBRAINS_MONO_SEMIBOLD = "jetbrains-mono-semibold";
    public static final String JETBRAINS_MONO_SEMIBOLD_ITALIC = "jetbrains-mono-semibold-italic";
    public static final String JETBRAINS_MONO_EXTRABOLD = "jetbrains-mono-extrabold";
    public static final String JETBRAINS_MONO_EXTRABOLD_ITALIC = "jetbrains-mono-extrabold-italic";
    public static final String JETBRAINS_MONO_EXTRALIGHT = "jetbrains-mono-extralight";
    public static final String JETBRAINS_MONO_EXTRALIGHT_ITALIC = "jetbrains-mono-extralight-italic";
    public static final String JETBRAINS_MONO_THIN = "jetbrains-mono-thin";
    public static final String JETBRAINS_MONO_THIN_ITALIC = "jetbrains-mono-thin-italic";

    public static final String JETBRAINS_MONO_NL_REGULAR = "jetbrains-mono-nl-regular";
    public static final String JETBRAINS_MONO_NL_BOLD = "jetbrains-mono-nl-bold";
    public static final String JETBRAINS_MONO_NL_ITALIC = "jetbrains-mono-nl-italic";
    public static final String JETBRAINS_MONO_NL_BOLD_ITALIC = "jetbrains-mono-nl-bold-italic";
    public static final String JETBRAINS_MONO_NL_LIGHT = "jetbrains-mono-nl-light";
    public static final String JETBRAINS_MONO_NL_LIGHT_ITALIC = "jetbrains-mono-nl-light-italic";
    public static final String JETBRAINS_MONO_NL_MEDIUM = "jetbrains-mono-nl-medium";
    public static final String JETBRAINS_MONO_NL_MEDIUM_ITALIC = "jetbrains-mono-nl-medium-italic";
    public static final String JETBRAINS_MONO_NL_SEMIBOLD = "jetbrains-mono-nl-semibold";
    public static final String JETBRAINS_MONO_NL_SEMIBOLD_ITALIC = "jetbrains-mono-nl-semibold-italic";
    public static final String JETBRAINS_MONO_NL_EXTRABOLD = "jetbrains-mono-nl-extrabold";
    public static final String JETBRAINS_MONO_NL_EXTRABOLD_ITALIC = "jetbrains-mono-nl-extrabold-italic";
    public static final String JETBRAINS_MONO_NL_EXTRALIGHT = "jetbrains-mono-nl-extralight";
    public static final String JETBRAINS_MONO_NL_EXTRALIGHT_ITALIC = "jetbrains-mono-nl-extralight-italic";
    public static final String JETBRAINS_MONO_NL_THIN = "jetbrains-mono-nl-thin";
    public static final String JETBRAINS_MONO_NL_THIN_ITALIC = "jetbrains-mono-nl-thin-italic";

    public static final int SIZE_TITLE = 36;
    public static final int SIZE_SUBTITLE = 14;
    public static final int SIZE_LABEL = 14;
    public static final int SIZE_NORMAL = 13;
    public static final int SIZE_SMALL = 12;
    public static final int SIZE_TINY = 11;
}