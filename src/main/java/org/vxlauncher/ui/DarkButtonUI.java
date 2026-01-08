//////////////////////////////////////////////////////
//// @File ui/DarkButtonUI.java
//// @Author 0xcds4r
//// @Date 08 Jan. 2026
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class DarkButtonUI extends BasicButtonUI
{
    private Color buttonColor;
    private Color hoverColor;
    private Color textColor;
    private int width;
    private int height;
    private boolean isHovered = false;

    public DarkButtonUI(Color buttonColor) {
        this(buttonColor, buttonColor.brighter(), new Color(255, 255, 255), 0, 48);
    }

    public DarkButtonUI(Color buttonColor, Color hoverColor, Color textColor, int width, int height) {
        this.buttonColor = buttonColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.width = width;
        this.height = height;
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    isHovered = true;
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                button.repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize(JComponent c)
    {
        if (width == 0) {
            return super.getPreferredSize(c);
        }
        return new Dimension(width, height);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b)
    {
        g.setColor(buttonColor.darker());
        g.fillRect(0, 0, b.getWidth(), b.getHeight());
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
    {

    }

    @Override
    public void paint(Graphics g, JComponent c)
    {
        AbstractButton button = (AbstractButton) c;

        Color currentColor = buttonColor;

        if (button.getModel().isPressed()) {
            currentColor = buttonColor.darker();
        }
        else if (isHovered && button.isEnabled()) {
            currentColor = hoverColor;
        }

        g.setColor(currentColor);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(textColor);
        g2.setFont(button.getFont());

        String text = button.getText();
        if (text != null && !text.isEmpty()) {
            FontMetrics fm = g2.getFontMetrics(button.getFont());
            int textX = (c.getWidth() - fm.stringWidth(text)) / 2;
            int textY = ((c.getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(text, textX, textY);
        }
    }
}