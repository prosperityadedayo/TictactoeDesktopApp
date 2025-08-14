import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;

public class XandO {
    int playerOneWins = 0;
    int playerTwoWins = 0;
    int draws = 0;
    boolean computerMode = false;

    ArrayList<Integer> playerOne = new ArrayList<>();
    ArrayList<Integer> playertwo = new ArrayList<>();


    int flag = 0;

    JFrame windows = new JFrame("X and O game");
    JPanel myFrame = new JPanel((new GridLayout(3, 3, 8, 8)));
    JPanel overlay;

    JFrame nameWindow = new JFrame("Enter Player Names");
    JTextField playerOneNameField = new JTextField(15);
    JTextField playerTwoNameField = new JTextField(15);
    JButton continueButton = new JButton("Start Game");

    String playerOneName = "Player One";
    String playerTwoName = "Player Two";

    // FIX: Keep reference to computer's timer so we can cancel it


    JButton btn1 = new JButton();
    JButton btn2 = new JButton();
    JButton btn3 = new JButton();
    JButton btn4 = new JButton();
    JButton btn5 = new JButton();
    JButton btn6 = new JButton();
    JButton btn7 = new JButton();
    JButton btn8 = new JButton();
    JButton btn9 = new JButton();

    int[] winningLine = null;

    Timer computerMoveTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Set theme/look and feel first
            ThemeManager.get().setTheme(ThemeManager.Theme.LIGHT);

            // 2. Install font AFTER LAF is set
            ThemeManager.installGlobalUIFont("Segoe UI", 13);

            // 3. Now create and show the UI
            XandO xando = new XandO();
            xando.showHomepage();
        });
    }


    // --- Homepage Method ---
    void showHomepage() {
        JFrame homepageFrame = new JFrame("X and O");
        homepageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homepageFrame.setResizable(false);
        homepageFrame.setSize(520, 300);
        homepageFrame.setLocationRelativeTo(null);

        ThemeManager.Colors c = ThemeManager.get().colors();

        JPanel homepagePanel = new JPanel(new BorderLayout());
        homepagePanel.setBorder(new EmptyBorder(36, 24, 24, 24));
        homepagePanel.setBackground(c.windowBg);

        JLabel titleLabel = new JLabel(
                "<html><span style='color:#" + ThemeManager.toHex(c.xColor) + "'>X</span>" +
                        "<span style='opacity:.6'> | </span>" +
                        "<span style='color:#" + ThemeManager.toHex(c.oColor) + "'>O</span></html>",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 112));
        titleLabel.setForeground(c.text);

        JLabel subtitle = new JLabel("Welcome to Tic-Tac-Toe", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(c.text);

        homepagePanel.add(titleLabel, BorderLayout.CENTER);
        homepageFrame.add(subtitle, BorderLayout.SOUTH);
        homepageFrame.add(homepagePanel);
        homepageFrame.setVisible(true);

        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homepageFrame.dispose();
                getPlayerNames();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }


    void getPlayerNames() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        ThemeManager.get().styleComponentTree(root, ThemeManager.get().colors());

        JLabel header = new JLabel("Who’s playing?");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 8));
        JLabel p1 = new JLabel("Player One Name");
        JLabel p2 = new JLabel("Player Two Name");
        JLabel hint = new JLabel("<html><i>Type <b>Computer</b> as Player Two to play vs AI.</i></html>");
        Font baseFont = hint.getFont();
        if (baseFont == null) {
            baseFont = UIManager.getFont("Label.font");
            if (baseFont == null) {
                baseFont = new Font("Segoe UI", Font.ITALIC, 12); // final hard fallback
            }
        }
        hint.setFont(baseFont.deriveFont(Font.ITALIC, 12f));

        //hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 12f));

        continueButton.setFocusPainted(false);
        ThemeManager.get().stylePrimaryButton(continueButton);

        form.add(p1);
        form.add(playerOneNameField);
        form.add(p2);
        form.add(playerTwoNameField);
        form.add(hint);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(continueButton);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        nameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameWindow.setSize(420, 350);
        nameWindow.setMinimumSize(new Dimension(380, 240));
        nameWindow.setLocationRelativeTo(null);
        nameWindow.setContentPane(root);
        nameWindow.setVisible(true);

        continueButton.addActionListener(e -> {
            playerOneName = playerOneNameField.getText().trim();
            playerTwoName = playerTwoNameField.getText().trim();
            if (playerTwoName.equalsIgnoreCase("computer") || playerTwoName.isEmpty()) {
                playerTwoName = "Computer";
                computerMode = true;
            }
            if (playerOneName.isEmpty()) playerOneName = "Player One";

            nameWindow.dispose();
            drawgame();
        });
    }

    void drawgame() {
        JButton[] buttons = getButtons();
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 34));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
            ThemeManager.get().styleBoardButton(btn);
            // Background/border/text will be set by ThemeManager below
        }

        myFrame.setBorder(new EmptyBorder(12, 12, 12, 12));
        myFrame.setBackground(ThemeManager.get().colors().boardBg);

        myFrame.add(btn1);
        myFrame.add(btn2);
        myFrame.add(btn3);
        myFrame.add(btn4);
        myFrame.add(btn5);
        myFrame.add(btn6);
        myFrame.add(btn7);
        myFrame.add(btn8);
        myFrame.add(btn9);

        btn1.addActionListener(e -> playerMove(btn1, 1));
        btn2.addActionListener(e -> playerMove(btn2, 2));
        btn3.addActionListener(e -> playerMove(btn3, 3));
        btn4.addActionListener(e -> playerMove(btn4, 4));
        btn5.addActionListener(e -> playerMove(btn5, 5));
        btn6.addActionListener(e -> playerMove(btn6, 6));
        btn7.addActionListener(e -> playerMove(btn7, 7));
        btn8.addActionListener(e -> playerMove(btn8, 8));
        btn9.addActionListener(e -> playerMove(btn9, 9));

        overlay = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (winningLine != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.get().colors().overlayLine); // THEMED color
                    g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    Point p1 = getButtonCenter(winningLine[0], this);
                    Point p2 = getButtonCenter(winningLine[2], this);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g2.dispose();
                }
            }

            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        overlay.setOpaque(false);

        windows.setGlassPane(overlay);
        overlay.setVisible(false);

        windows.getContentPane().removeAll();

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 12, 8, 12));
        header.setBackground(ThemeManager.get().colors().windowBg);
        JLabel names = new JLabel(playerOneName + " (X)  vs  " + playerTwoName + " (O)");
        names.setFont(new Font("Segoe UI", Font.BOLD, 16));
        names.setForeground(ThemeManager.get().colors().text);
        header.add(names, BorderLayout.WEST);

        windows.add(header, BorderLayout.NORTH);
        windows.add(myFrame, BorderLayout.CENTER);
        windows.setJMenuBar(createMenuBar());

        windows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windows.setSize(460, 560);
        windows.setMinimumSize(new Dimension(380, 480));
        windows.setLocationRelativeTo(null);
        windows.setVisible(true);
        windows.setResizable(true);

        ThemeManager.get().applyToGame(this);
    }


    void playerMove(JButton btn, int position) {
        // DEBUG: log entry and current state
        System.out.println("[DEBUG] playerMove called: pos=" + position + " flag=" + flag +
                " playerOne=" + playerOne + " playertwo=" + playertwo);

        if (!btn.isEnabled()) {
            System.out.println("[DEBUG] button already disabled for pos=" + position + ", ignoring.");
            return;
        }

        if (flag == 0) {
            playerOne.add(position);
            btn.setText("X");
            btn.setForeground(ThemeManager.get().colors().xColor);
            btn.setEnabled(false);
            flag = 1;
            checkWin();

            if (computerMode && !isGameOver()) {
                if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
                    computerMoveTimer.stop();
                }
                computerMoveTimer = new Timer(250, e -> {
                    ((Timer) e.getSource()).stop();
                    makeComputerMove();
                });
                computerMoveTimer.setRepeats(false);
                computerMoveTimer.start();
            }
        } else {
            playertwo.add(position);
            btn.setText("O");
            btn.setForeground(ThemeManager.get().colors().oColor);
            btn.setEnabled(false);
            flag = 0;
            checkWin();
        }

        // DEBUG: log exit state
        System.out.println("[DEBUG] playerMove exit: flag=" + flag +
                " playerOne=" + playerOne + " playertwo=" + playertwo);
    }

    boolean isGameOver() {
        return (checkWinConditionOnly(playerOne) ||
                checkWinConditionOnly(playertwo) ||
                (playerOne.size() + playertwo.size()) == 9);
    }


    void checkWin() {
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            computerMoveTimer.stop();
        }

        if (checkWinConditionOnly(playerOne)) {
            playerOneWins++;
            winningLine = getWinningCombination(playerOne);
            if (overlay != null) {
                overlay.setVisible(true);
                overlay.repaint();
            }
            SwingUtilities.invokeLater(() -> {
                ThemeManager.showThemedMessage(windows, playerOneName + " Wins!", "Winner");
                disableAllButtons();
                askToPlayAgain();
            });
            return;
        }
        if (checkWinConditionOnly(playertwo)) {
            playerTwoWins++;
            winningLine = getWinningCombination(playertwo);
            if (overlay != null) {
                overlay.setVisible(true);
                overlay.repaint();
            }
            SwingUtilities.invokeLater(() -> {
                ThemeManager.showThemedMessage(windows, playerTwoName + " Wins!", "Winner");
                disableAllButtons();
                askToPlayAgain();
            });
            return;
        }
        if ((playerOne.size() + playertwo.size()) == 9) {
            draws++;
            SwingUtilities.invokeLater(() -> {
                ThemeManager.showThemedMessage(windows, "It's a Draw!", "Draw");
                askToPlayAgain();
            });
        }
    }

    boolean checkWinConditionOnly(ArrayList<Integer> playerMoves) {
        int[][] winCombinations = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
                {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
                {1, 5, 9}, {3, 5, 7}
        };
        for (int[] combination : winCombinations) {
            if (playerMoves.contains(combination[0]) &&
                    playerMoves.contains(combination[1]) &&
                    playerMoves.contains(combination[2]))
                return true;
        }
        return false;
    }

    int[] getWinningCombination(ArrayList<Integer> moves) {
        int[][] winCombinations = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
                {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
                {1, 5, 9}, {3, 5, 7}
        };
        for (int[] combo : winCombinations) {
            if (moves.contains(combo[0]) && moves.contains(combo[1]) && moves.contains(combo[2])) return combo;
        }
        return null;
    }

    void askToPlayAgain() {
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            computerMoveTimer.stop();
        }
        int choice = ThemeManager.showThemedConfirm(windows, "Do you want to play again?", "Play Again?");
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            showScoreboard();
            ThemeManager.showThemedMessage(windows, "Thanks for playing!", "Goodbye");
            System.exit(0);
        }
    }

    void resetGame() {
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            computerMoveTimer.stop();
        }
        JButton[] buttons = getButtons();
        for (JButton btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setForeground(ThemeManager.get().colors().text);
        }
        playerOne.clear();
        playertwo.clear();
        winningLine = null;
        if (overlay != null) {
            overlay.setVisible(false);
            overlay.repaint();
        }
        flag = 0;

        ThemeManager.get().applyToGame(this);
    }

    void disableAllButtons() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
        btn5.setEnabled(false);
        btn6.setEnabled(false);
        btn7.setEnabled(false);
        btn8.setEnabled(false);
        btn9.setEnabled(false);
    }

    void showScoreboard() {
        Color text = ThemeManager.get().colors().text;
        String scoreboardHtml =
                "<html><body style='color:#" + ThemeManager.toHex(text) + ";font-family:Segoe UI;'>" +
                        "<h2 style='margin:0 0 8px 0;'>Final Scoreboard</h2>" +
                        "<div style='line-height:1.6'>" +
                        "<b>" + playerOneName + " (X)</b><br/>Wins: " + playerOneWins + "<br/><br/>" +
                        "<b>" + playerTwoName + " (O)</b><br/>Wins: " + playerTwoWins + "<br/><br/>" +
                        "<b>Draws:</b> " + draws + "</div></body></html>";
        JLabel label = new JLabel(scoreboardHtml);
        ThemeManager.showThemedComponent(windows, label, "Scoreboard", JOptionPane.INFORMATION_MESSAGE);
    }


    boolean isAvailable(int position) {
        return !(playerOne.contains(position) || playertwo.contains(position));
    }

    void makeComputerMove() {
        int move = getWinningMove(playertwo);
        if (move == -1) move = getWinningMove(playerOne);
        if (move == -1 && isAvailable(5)) move = 5;
        if (move == -1) {
            for (int i : new int[]{1, 3, 7, 9}) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }
        if (move == -1) {
            for (int i : new int[]{2, 4, 6, 8}) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }
        if (move != -1) {
            JButton targetButton = getButton(move);
            if (targetButton != null && targetButton.isEnabled()) {
                playerMove(targetButton, move);
            }
        }
    }

    int getWinningMove(ArrayList<Integer> playerMoves) {
        int[][] wins = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
                {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
                {1, 5, 9}, {3, 5, 7}
        };
        for (int[] line : wins) {
            int count = 0, empty = -1;
            for (int pos : line) {
                if (playerMoves.contains(pos)) count++;
                else if (isAvailable(pos)) empty = pos;
            }
            if (count == 2 && empty != -1) return empty;
        }
        return -1;
    }

    JButton getButton(int position) {
        switch (position) {
            case 1:
                return btn1;
            case 2:
                return btn2;
            case 3:
                return btn3;
            case 4:
                return btn4;
            case 5:
                return btn5;
            case 6:
                return btn6;
            case 7:
                return btn7;
            case 8:
                return btn8;
            case 9:
                return btn9;
        }
        return null;
    }

    Point getButtonCenter(int position, Component relativeTo) {
        JButton btn = getButton(position);
        if (btn == null) return new Point(0, 0);
        return SwingUtilities.convertPoint(btn, btn.getWidth() / 2, btn.getHeight() / 2, relativeTo);
    }

    JButton[] getButtons() {
        return new JButton[]{btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};
    }

    JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        ThemeManager.get().styleMenuBar(mb);

        JMenu game = new JMenu("Game");
        JMenu view = new JMenu("View");

        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem scoreboard = new JMenuItem("Scoreboard");
        JMenuItem exit = new JMenuItem("Exit");

        JCheckBoxMenuItem darkToggle = new JCheckBoxMenuItem("Dark mode");
        darkToggle.setSelected(ThemeManager.get().getTheme() == ThemeManager.Theme.DARK);

        newGame.addActionListener(e -> resetGame());
        scoreboard.addActionListener(e -> showScoreboard());
        exit.addActionListener(e -> System.exit(0));

        darkToggle.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    ThemeManager.get().setTheme(
                            darkToggle.isSelected() ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT
                    );
                    ThemeManager.get().applyToGame(this);
                })
        );

        game.add(newGame);
        game.add(scoreboard);
        game.addSeparator();
        game.add(exit);

        view.add(darkToggle);

        mb.add(game);
        mb.add(view);
        return mb;
    }
}
