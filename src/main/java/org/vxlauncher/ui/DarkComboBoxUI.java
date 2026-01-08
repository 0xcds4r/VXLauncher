//////////////////////////////////////////////////////
//// @File ui/DarkComboBoxUI.java
//// @Author 0xcds4r
//// @Date 08 Jan. 2026
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public class DarkComboBoxUI extends BasicComboBoxUI
{
    private static final Color BG_TERTIARY = new Color(38, 38, 40);
    private static final Color BG_SECONDARY = new Color(50, 50, 52);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color SCROLLBAR_TRACK = new Color(38, 38, 40);
    private static final Color SCROLLBAR_THUMB = new Color(100, 100, 102);
    private static final Color SCROLLBAR_THUMB_HOVER = new Color(120, 120, 122);

    @Override
    protected JButton createArrowButton()
    {
        JButton btn = new JButton() {
            @Override
            public int getWidth() { return 0; }
        };
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setVisible(false);
        return btn;
    }

    @Override
    protected Rectangle rectangleForCurrentValue()
    {
        Rectangle rect = super.rectangleForCurrentValue();
        rect.width = comboBox.getWidth();
        return rect;
    }

    @Override
    public void configureEditor()
    {
        super.configureEditor();
        if (editor != null) {
            editor.setBackground(BG_TERTIARY);
            editor.setForeground(TEXT_PRIMARY);
        }
    }

    @Override
    protected ComboPopup createPopup()
    {
        return new BasicComboPopup(comboBox) {
            @Override
            protected void configureScroller()
            {
                super.configureScroller();
                configureScrollBar();
            }

            private void configureScrollBar()
            {
                JScrollBar sb = scroller.getVerticalScrollBar();
                sb.setUI(new DarkScrollBarUI());
                sb.setBackground(SCROLLBAR_TRACK);

                sb.setPreferredSize(new Dimension(10, 0));
            }

            @Override
            public void show()
            {
                super.show();
                list.setBackground(BG_SECONDARY);
                list.setForeground(TEXT_PRIMARY);
                list.setSelectionBackground(new Color(80, 80, 85));
                list.setSelectionForeground(TEXT_PRIMARY);
            }
        };
    }

    private static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI
    {
        @Override
        protected void configureScrollBarColors()
        {
            this.thumbColor = SCROLLBAR_THUMB;
            this.thumbDarkShadowColor = SCROLLBAR_THUMB;
            this.thumbHighlightColor = SCROLLBAR_THUMB;
            this.thumbLightShadowColor = SCROLLBAR_THUMB;
            this.trackColor = SCROLLBAR_TRACK;
            this.trackHighlightColor = SCROLLBAR_TRACK;
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
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            if (thumbBounds.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(SCROLLBAR_THUMB);
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4,
                    thumbBounds.height, 4, 4);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(SCROLLBAR_TRACK);
            g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }
}