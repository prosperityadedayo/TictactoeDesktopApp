import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class AvatarManager {
    private static final AvatarManager INSTANCE = new AvatarManager();
    private static final String PREF_KEY_P1 = "xando.avatar.player1";
    private static final String PREF_KEY_P2 = "xando.avatar.player2";
    private final Preferences prefs = Preferences.userNodeForPackage(AvatarManager.class);

    // List of emoji options (expand as you like)
    private final String[] emojiOptions = {
            "X", "O", "😎", "🤖", "🐱", "🐶", "🔥", "💎", "🍀", "⚡"
    };

    private AvatarManager() {}

    public static AvatarManager get() { return INSTANCE; }

    public String getPlayer1Avatar() {
        return prefs.get(PREF_KEY_P1, "X"); // Default = X
    }

    public String getPlayer2Avatar() {
        return prefs.get(PREF_KEY_P2, "O"); // Default = O
    }

    public void setPlayer1Avatar(String emoji) {
        prefs.put(PREF_KEY_P1, emoji);
    }

    public void setPlayer2Avatar(String emoji) {
        prefs.put(PREF_KEY_P2, emoji);
    }

    // Show dialog for emoji selection
    public void showAvatarSelection(Component parent) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        JComboBox<String> p1Box = new JComboBox<>(emojiOptions);
        p1Box.setSelectedItem(getPlayer1Avatar());
        panel.add(new JLabel("Player 1 Avatar:"));
        panel.add(p1Box);

        JComboBox<String> p2Box = new JComboBox<>(emojiOptions);
        p2Box.setSelectedItem(getPlayer2Avatar());
        panel.add(new JLabel("Player 2 Avatar:"));
        panel.add(p2Box);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Choose Avatars (optional)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            setPlayer1Avatar((String) p1Box.getSelectedItem());
            setPlayer2Avatar((String) p2Box.getSelectedItem());
        }
    }
}
