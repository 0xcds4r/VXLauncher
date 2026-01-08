//////////////////////////////////////////////////////
//// @File Main.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher;

import org.vxlauncher.ui.LauncherWindow;
import javax.swing.*;
import java.util.Locale;

public class Main
{
    public static void main(String[] args)
    {
        Locale.setDefault(Locale.forLanguageTag("ru-RU"));
        System.setProperty("file.encoding", "UTF-8");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LauncherWindow window = new LauncherWindow();
            window.setVisible(true);
        });
    }
}