//////////////////////////////////////////////////////
//// @File Main.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher;

import org.vxlauncher.ui.LauncherWindow;
import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
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