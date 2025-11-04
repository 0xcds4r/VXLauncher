//////////////////////////////////////////////////////
//// @File ui/UIHelper.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import javax.swing.*;
import java.awt.*;

public final class UIHelper
{
    private UIHelper() {
        throw new UnsupportedOperationException("UIHelper is a ui class and cannot be instantiated");
    }

    public static Icon folderIcon()
    {
        Icon i = UIManager.getIcon("FileView.directoryIcon");
        if (i != null) return i;
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new Color(255, 215, 0));
                g.fillRoundRect(x + 2, y + 6, 20, 14, 4, 4);
                g.fillRect(x + 4, y + 4, 6, 3);
                g.setColor(new Color(200, 160, 0));
                g.drawRoundRect(x + 2, y + 6, 20, 14, 4, 4);
            }
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
        };
    }

    public static Icon packIcon()
    {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new Color(100, 180, 255));
                g.fillRoundRect(x+4, y+4, 16, 16, 4, 4);
                g.setColor(new Color(0, 120, 220));
                g.drawRoundRect(x+4, y+4, 16, 16, 4, 4);
                g.fillRect(x+8, y+10, 8, 3);
            }
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
        };
    }

    public static Icon worldIcon()
    {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new Color(100, 200, 100));
                g.fillOval(x+4, y+4, 16, 16);
                g.setColor(new Color(0, 140, 0));
                g.drawOval(x+4, y+4, 16, 16);
                g.fillRect(x+10, y+10, 4, 4);
            }
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
        };
    }
}