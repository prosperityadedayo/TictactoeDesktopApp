import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.prefs.Preferences;


public class ThemeManager {
    public enum Theme {LIGHT, DARK}

    static final class Colors {
        final Color windowBg, boardBg, buttonBg, buttonBorder, buttonHoverBg, text, xColor, oColor, overlayLine, accent;
        Colors(Color windowBg, Color boardBg, Color buttonBg, Color buttonBorder, Color buttonHoverBg,
               Color text, Color xColor, Color oColor, Color overlayLine, Color accent) {
            this.windowBg = windowBg; this.boardBg = boardBg; this.buttonBg = buttonBg; this.buttonBorder = buttonBorder;
            this.buttonHoverBg = buttonHoverBg; this.text = text; this.xColor = xColor; this.oColor = oColor;
            this.overlayLine = overlayLine; this.accent = accent;
        }
    }

    private static final ThemeManager INSTANCE = new ThemeManager();
    private static final String PREF_KEY = "xando.theme";
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private Theme theme;

    private ThemeManager() {
        String saved = prefs.get(PREF_KEY, "LIGHT");
        try { theme = Theme.valueOf(saved); } catch (Exception e) { theme = Theme.LIGHT; }
    }

    static ThemeManager get() { return INSTANCE; }
    Theme getTheme() { return theme; }

    void setTheme(Theme t) { theme = t; prefs.put(PREF_KEY, t.name()); applyDialogTheme(); }

    Colors colors() {
        if (theme == Theme.DARK) {
            return new Colors(
                    new Color(0x12,0x12,0x12),
                    new Color(0x1A,0x1A,0x1A),
                    new Color(0x33,0x33,0x33),
                    new Color(0x20,0x20,0x20),
                    new Color(0x2A,0x2A,0x2A),
                    new Color(0xEE,0xEE,0xEE),
                    new Color(0xE7,0x4C,0x3C),
                    new Color(0x2E,0xCC,0x71),
                    new Color(0xFF,0xD7,0x00),
                    new Color(0x3B,0x82,0xF6)
            );
        } else {
            return new Colors(
                    new Color(0xFA,0xFA,0xFA), // windowBg
                    new Color(0xFF,0xFF,0xFF), // boardBg
                    new Color(0xF2,0xF2,0xF2), // buttonBg
                    new Color(0xCC,0xCC,0xCC), // buttonBorder
                    new Color(0xE9,0xE9,0xE9), // buttonHoverBg
                    new Color(0x11,0x11,0x11), // text
                    new Color(0x00,0x66,0xCC), // X (blue)
                    new Color(0xCC,0x00,0x33), // O (crimson)
                    new Color(0x00,0x66,0xCC), // overlay line
                    new Color(0x00,0x7B,0xFF)  // accent
            );
        }
    }

    void applyToGame(XandO game) {
        Colors c = colors();

        if (game.windows.getJMenuBar() != null) styleMenuBar(game.windows.getJMenuBar());

        game.windows.getContentPane().setBackground(c.windowBg);
        game.myFrame.setBackground(c.boardBg);

        JButton[] buttons = game.getButtons();
        for (JButton b : buttons) {
            styleBoardButton(b);
            String txt = b.getText();
            if ("X".equals(txt)) b.setForeground(c.xColor);
            else if ("O".equals(txt)) b.setForeground(c.oColor);
            else b.setForeground(c.text);
        }

        if (game.nameWindow != null) {
            styleComponentTree(game.nameWindow.getContentPane(), c);
            game.nameWindow.repaint();
        }
        styleComponentTree(game.windows.getContentPane(), c);

        if (game.overlay != null) game.overlay.repaint();

        applyDialogTheme();
        game.windows.repaint();
    }

    void applyDialogTheme() {
        Colors c = colors();
        UIManager.put("OptionPane.background", c.windowBg);
        UIManager.put("Panel.background", c.windowBg);
        UIManager.put("OptionPane.messageForeground", c.text);
        UIManager.put("Button.background", c.buttonBg);
        UIManager.put("Button.foreground", c.text);
        UIManager.put("Menu.background", c.windowBg);
        UIManager.put("Menu.foreground", c.text);
        UIManager.put("MenuItem.background", c.windowBg);
        UIManager.put("MenuItem.foreground", c.text);
        UIManager.put("CheckBoxMenuItem.background", c.windowBg);
        UIManager.put("CheckBoxMenuItem.foreground", c.text);
        UIManager.put("Separator.foreground", c.buttonBorder);
    }

    void styleComponentTree(Component comp, Colors c) {
        if (comp == null) return;
        if (comp instanceof JComponent jc && !(jc instanceof JButton)) {
            jc.setBackground(c.windowBg);
            jc.setForeground(c.text);
        }
        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                styleComponentTree(child, c);
            }
        }
    }

    void styleMenuBar(JMenuBar bar) {
        Colors c = colors();
        bar.setBackground(c.windowBg);
        bar.setForeground(c.text);
        for (MenuElement me : bar.getSubElements()) {
            if (me.getComponent() != null) {
                me.getComponent().setBackground(c.windowBg);
                me.getComponent().setForeground(c.text);
            }
        }
    }

    void styleBoardButton(JButton b) {
        Colors c = colors();
        b.setBackground(c.buttonBg);
        b.setForeground(c.text);
        b.setBorder(new LineBorder(c.buttonBorder, 2, true));
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Remove previous listeners (in case of theme switch)
        for (MouseListener ml : b.getMouseListeners()) {
            if (ml.getClass().getName().contains("ThemeHover")) b.removeMouseListener(ml);
        }
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) b.setBackground(c.buttonHoverBg);
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(c.buttonBg);
            }
            @Override public void mousePressed(MouseEvent e) {
                if (b.isEnabled()) b.setBorder(new LineBorder(c.accent, 2, true));
            }
            @Override public void mouseReleased(MouseEvent e) {
                b.setBorder(new LineBorder(c.buttonBorder, 2, true));
            }
        });
    }
    void stylePrimaryButton(JButton b) {

        Font baseFont = b.getFont();
        if (baseFont == null) {
            baseFont = UIManager.getFont("Button.font");
            if (baseFont == null) {
                baseFont = new Font("Segoe UI Emoji", Font.PLAIN, 13); // final fallback
            }
            b.setFont(baseFont);
        }

        Colors c = colors();
        b.setBackground(c.accent);
        b.setForeground(Color.WHITE);
        b.setBorder(new LineBorder(c.accent.darker(), 2, true));
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, b.getFont().getSize2D()));

        for (MouseListener ml : b.getMouseListeners()) {
            if (ml.getClass().getName().contains("ThemeHover")) b.removeMouseListener(ml);
        }
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(c.accent.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(c.accent);
            }
            @Override public void mousePressed(MouseEvent e) {
                b.setBorder(new LineBorder(c.accent.darker().darker(), 2, true));
            }
            @Override public void mouseReleased(MouseEvent e) {
                b.setBorder(new LineBorder(c.accent.darker(), 2, true));
            }
        });
    }

    static void showThemedMessage(Component parent, String message, String title) {
        get().applyDialogTheme();
        JOptionPane.showMessageDialog(parent, new JLabel(message), title, JOptionPane.INFORMATION_MESSAGE);
    }

    static int showThemedConfirm(Component parent, String message, String title) {
        get().applyDialogTheme();
        return JOptionPane.showConfirmDialog(parent, new JLabel(message), title, JOptionPane.YES_NO_OPTION);
    }

    static void showThemedComponent(Component parent, JComponent comp, String title, int messageType) {
        get().applyDialogTheme();
        JOptionPane.showMessageDialog(parent, comp, title, messageType);
    }

    static String toHex(Color col) {
        return String.format("%02X%02X%02X", col.getRed(), col.getGreen(), col.getBlue());
    }

    static void installGlobalUIFont(String fontName, int size) {
        Font font = new Font(fontName, Font.PLAIN, size);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

}
