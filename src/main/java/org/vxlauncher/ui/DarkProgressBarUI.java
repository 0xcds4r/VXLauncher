//////////////////////////////////////////////////////
//// @File ui/DarkProgressBarUI.java
//// @Author 0xcds4r
//// @Date 08 Jan. 2026
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class DarkProgressBarUI extends BasicProgressBarUI
{
    private static final Color BG_TRACK = new Color(38, 38, 40);
    private static final Color PROGRESS_FILL = new Color(10, 132, 255);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);

    @Override
    public void paint(Graphics g, JComponent c)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        JProgressBar bar = (JProgressBar) c;
        Insets insets = bar.getInsets();

        int width = bar.getWidth() - insets.left - insets.right;
        int height = bar.getHeight() - insets.top - insets.bottom;

        g2d.setColor(BG_TRACK);
        g2d.fillRoundRect(insets.left, insets.top, width, height, 8, 8);

        if (bar.getValue() > 0)
        {
            int progressWidth = (int) ((width / (double) bar.getMaximum()) * bar.getValue());
            g2d.setColor(PROGRESS_FILL);
            g2d.fillRoundRect(insets.left, insets.top, progressWidth, height, 8, 8);
        }

        if (bar.isStringPainted())
        {
            String text = bar.getString();
            if (text != null && !text.isEmpty())
            {
                FontMetrics fm = g2d.getFontMetrics(bar.getFont());
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();

                int x = insets.left + (width - textWidth) / 2;
                int y = insets.top + ((height - fm.getHeight()) / 2) + fm.getAscent();

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(bar.getFont());
                g2d.drawString(text, x, y);
            }
        }
    }
}
