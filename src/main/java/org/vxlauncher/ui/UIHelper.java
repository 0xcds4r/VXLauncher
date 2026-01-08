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
    private static final Color ICON_COLOR = new Color(10, 132, 255);
    private static final Color ICON_SECONDARY = new Color(48, 209, 88);

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

    public static Icon worldIcon()
    {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ICON_COLOR);
                g2.fillOval(x + 2, y + 2, 12, 12);

                g2.setColor(ICON_SECONDARY);
                g2.fillRect(x + 4, y + 5, 3, 2);
                g2.fillRect(x + 9, y + 7, 3, 2);
                g2.fillRect(x + 5, y + 10, 4, 1);

                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(174, 174, 178));
                g2.drawOval(x + 2, y + 2, 12, 12);
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
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
}