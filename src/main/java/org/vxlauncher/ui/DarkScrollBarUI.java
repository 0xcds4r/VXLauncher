//////////////////////////////////////////////////////
//// @File ui/DarkScrollBarUI.java
//// @Author 0xcds4r
//// @Date 08 Jan. 2026
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class DarkScrollBarUI extends BasicScrollBarUI
{
    private static final Color BG_SECONDARY = new Color(28, 28, 30);
    private static final Color BG_TERTIARY = new Color(38, 38, 40);
    private static final Color THUMB_COLOR = new Color(58, 58, 60);

    @Override
    protected void configureScrollBarColors()
    {
        this.thumbColor = THUMB_COLOR;
        this.trackColor = BG_SECONDARY;
        this.thumbLightShadowColor = THUMB_COLOR;
        this.thumbHighlightColor = THUMB_COLOR;
    }

    @Override
    protected JButton createDecreaseButton(int orientation)
    {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation)
    {
        return createZeroButton();
    }

    private JButton createZeroButton()
    {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {
        g.setColor(BG_SECONDARY);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        g.setColor(THUMB_COLOR);
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);

        g.setColor(new Color(68, 68, 70));
        g.drawRect(thumbBounds.x, thumbBounds.y, thumbBounds.width - 1, thumbBounds.height - 1);
    }
}
