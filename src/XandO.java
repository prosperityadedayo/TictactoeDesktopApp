import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.border.LineBorder; // Import for LineBorder

public class XandO {
    int playerOneWins = 0;
    int playerTwoWins = 0;
    int draws = 0;
    boolean computerMode = false;

    ArrayList<Integer> playerOne = new ArrayList<>();
    ArrayList<Integer> playertwo = new ArrayList<>();

    JFrame windows = new JFrame("X and O game");
    int flag = 0;

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

    JPanel myFrame = new JPanel((new GridLayout(3, 3)));

    JPanel overlay;

    JFrame nameWindow = new JFrame("Enter Player Names");
    JTextField playerOneNameField = new JTextField(15);
    JTextField playerTwoNameField = new JTextField(15);
    JButton continueButton = new JButton("Continue");

    String playerOneName = "Player One";
    String playerTwoName = "Player Two";

    // FIX: Keep reference to computer's timer so we can cancel it
    Timer computerMoveTimer;

    // --- Homepage Method ---
    void showHomepage() {
        JFrame homepageFrame = new JFrame("X and O");
        homepageFrame.setSize(400, 300);
        homepageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homepageFrame.setResizable(false);
        homepageFrame.setLocationRelativeTo(null);

        JPanel homepagePanel = new JPanel();
        homepagePanel.setBackground(Color.BLACK);
        homepagePanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 100));
        String htmlText = "<html><font color='green'>X</font><font color='white'>|</font><font color='red'>O</font></html>";
        titleLabel.setText(htmlText);

        homepagePanel.add(titleLabel);
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
        JPanel namePanel = new JPanel(new GridLayout(4, 2));
        namePanel.add(new JLabel("Player One Name:"));
        namePanel.add(playerOneNameField);
        namePanel.add(new JLabel("Player Two Name:"));
        namePanel.add(playerTwoNameField);

        JLabel instructionLabel = new JLabel(
                "<html>Type '<b>Computer</b>' in Player Two's name field to play against the computer.</html>");
        namePanel.add(instructionLabel);
        namePanel.add(new JLabel());

        namePanel.add(new JLabel(""));
        namePanel.add(continueButton);

        nameWindow.add(namePanel);
        nameWindow.setSize(400, 180);
        nameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameWindow.setVisible(true);
        nameWindow.setLocationRelativeTo(null);

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerOneName = playerOneNameField.getText().trim();
                playerTwoName = playerTwoNameField.getText().trim();
                if (playerTwoName.equalsIgnoreCase("computer") || playerTwoName.isEmpty()) {
                    playerTwoName = "Computer";
                    computerMode = true;
                }
                if (playerOneName.isEmpty())
                    playerOneName = "Player One";

                nameWindow.dispose();
                drawgame();
            }
        });
    }

    void drawgame() {
        JButton[] buttons = { btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9 };
        for (JButton btn : buttons) {
            btn.setBackground(Color.BLACK);
            btn.setBorder(new LineBorder(Color.WHITE, 2));
            btn.setFont(new Font("Arial", Font.BOLD, 40));
            btn.setFocusPainted(false);
        }

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
                    g2.setColor(Color.YELLOW);
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
        windows.add(myFrame);
        windows.setSize(300, 300);
        windows.setVisible(true);
        windows.setLocationRelativeTo(null);
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
            btn.setForeground(Color.GREEN);
            btn.setEnabled(false);
            flag = 1;
            checkWin();

            // Only schedule computer move if still not game over
            if (computerMode && !isGameOver()) {
                // FIX: stop previous timer before starting new
                if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
                    System.out.println("[DEBUG] stopping previous computerMoveTimer before starting new one");
                    computerMoveTimer.stop();
                }
                System.out.println("[DEBUG] scheduling computerMoveTimer (after player move)");
                computerMoveTimer = new Timer(250, e -> {
                    ((Timer) e.getSource()).stop();
                    System.out.println("[DEBUG] computerMoveTimer fired");
                    makeComputerMove();
                });
                computerMoveTimer.setRepeats(false);
                computerMoveTimer.start();
            }
        } else {
            playertwo.add(position);
            btn.setText("O");
            btn.setForeground(Color.RED);
            btn.setEnabled(false);
            flag = 0;
            checkWin();
        }

        // DEBUG: log exit state
        System.out.println("[DEBUG] playerMove exit: flag=" + flag +
                " playerOne=" + playerOne + " playertwo=" + playertwo);
    }

    boolean isGameOver() {
        return (checkWinConditionOnly(playerOne) || checkWinConditionOnly(playertwo) ||
                (playerOne.size() + playertwo.size()) == 9);
    }

    public static void main(String[] args) {
        XandO xando = new XandO();
        xando.showHomepage();
    }

    void checkWin() {
        // FIX: stop any pending computer move immediately when a decisive condition
        // occurs
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            System.out.println("[DEBUG] checkWin(): stopping computerMoveTimer because we are checking win/draw");
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
                JOptionPane.showMessageDialog(null, playerOneName + " Wins!");
                disableAllButtons();
                askToPlayAgain();
            });
            return;
        }
        if (checkWinConditionOnly(playertwo)) {
            playerTwoWins++;
            winningLine = getWinningCombination(playertwo); // keep correct moves
            if (overlay != null) {
                overlay.setVisible(true);
                overlay.repaint();
            }
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, playerTwoName + " Wins!"); // show correct winner
                disableAllButtons();
                askToPlayAgain();
            });
            return;
        }
        if ((playerOne.size() + playertwo.size()) == 9) {
            draws++;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "It's a Draw!");
                askToPlayAgain();
            });
            return;
        }
    }

    boolean checkWinConditionOnly(ArrayList<Integer> playerMoves) {
        int[][] winCombinations = {
                { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 },
                { 1, 4, 7 }, { 2, 5, 8 }, { 3, 6, 9 },
                { 1, 5, 9 }, { 3, 5, 7 }
        };
        for (int[] combination : winCombinations) {
            if (playerMoves.contains(combination[0]) &&
                    playerMoves.contains(combination[1]) &&
                    playerMoves.contains(combination[2])) {
                return true;
            }
        }
        return false;
    }

    int[] getWinningCombination(ArrayList<Integer> moves) {
        int[][] winCombinations = {
                { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 },
                { 1, 4, 7 }, { 2, 5, 8 }, { 3, 6, 9 },
                { 1, 5, 9 }, { 3, 5, 7 }
        };
        for (int[] combo : winCombinations) {
            if (moves.contains(combo[0]) &&
                    moves.contains(combo[1]) &&
                    moves.contains(combo[2])) {
                return combo;
            }
        }
        return null;
    }

    void askToPlayAgain() {
        // FIX: stop pending timer before asking (extra safeguard)
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            System.out.println("[DEBUG] askToPlayAgain(): stopping computerMoveTimer as safeguard");
            computerMoveTimer.stop();
        }

        int choice = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Play Again?",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            showScoreboard();
            JOptionPane.showMessageDialog(null, "Thanks for playing!");
            System.exit(0);
        }
    }

    void resetGame() {
        // FIX: stop pending computer move timer
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            System.out.println("[DEBUG] resetGame(): stopping running computerMoveTimer");
            computerMoveTimer.stop();
        } else {
            System.out.println("[DEBUG] resetGame(): no running computerMoveTimer to stop");
        }

        JButton[] buttons = { btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9 };
        for (JButton btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setForeground(null);
        }
        playerOne.clear();
        playertwo.clear();
        winningLine = null;
        if (overlay != null) {
            overlay.setVisible(false);
            overlay.repaint();
        }
        flag = 0;

        System.out.println(
                "[DEBUG] resetGame completed: flag=" + flag + " playerOne=" + playerOne + " playertwo=" + playertwo);
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
        String scoreboard = playerOneName + "'s Record:\n" +
                "Wins: " + playerOneWins + "\n\n" +
                playerTwoName + "'s Record:\n" +
                "Wins: " + playerTwoWins + "\n\n" +
                "Draws: " + draws;
        JOptionPane.showMessageDialog(null, scoreboard, "Final Scoreboard", JOptionPane.INFORMATION_MESSAGE);
    }

    boolean isAvailable(int position) {
        return !(playerOne.contains(position) || playertwo.contains(position));
    }

    void makeComputerMove() {
        System.out.println("[DEBUG] makeComputerMove() called. Current state playerOne=" + playerOne + " playertwo="
                + playertwo + " flag=" + flag);

        int move = getWinningMove(playertwo);
        if (move == -1)
            move = getWinningMove(playerOne);
        if (move == -1 && isAvailable(5))
            move = 5;
        if (move == -1) {
            for (int i : new int[] { 1, 3, 7, 9 }) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }
        if (move == -1) {
            for (int i : new int[] { 2, 4, 6, 8 }) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }

        if (move != -1) {
            System.out.println("[DEBUG] makeComputerMove selected move=" + move);
            JButton targetButton = getButton(move);
            // guard: ensure target still enabled
            if (targetButton != null && targetButton.isEnabled()) {
                playerMove(targetButton, move);
            } else {
                System.out.println("[DEBUG] targetButton was null or already disabled for move=" + move);
            }
        } else {
            System.out.println("[DEBUG] makeComputerMove found no available move (shouldn't happen)");
        }
    }

    int getWinningMove(ArrayList<Integer> playerMoves) {
        int[][] wins = {
                { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 },
                { 1, 4, 7 }, { 2, 5, 8 }, { 3, 6, 9 },
                { 1, 5, 9 }, { 3, 5, 7 }
        };
        for (int[] line : wins) {
            int count = 0, empty = -1;
            for (int pos : line) {
                if (playerMoves.contains(pos))
                    count++;
                else if (isAvailable(pos))
                    empty = pos;
            }
            if (count == 2 && empty != -1)
                return empty;
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
        if (btn == null)
            return new Point(0, 0);
        return SwingUtilities.convertPoint(btn, btn.getWidth() / 2, btn.getHeight() / 2, relativeTo);
    }
}

