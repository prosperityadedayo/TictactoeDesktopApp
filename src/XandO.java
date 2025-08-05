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

    JPanel myFrame = new JPanel((new GridLayout(3, 3)));

    JFrame nameWindow = new JFrame("Enter Player Names");
    JTextField playerOneNameField = new JTextField(15);
    JTextField playerTwoNameField = new JTextField(15);
    JButton continueButton = new JButton("Continue");

    String playerOneName = "Player One";
    String playerTwoName = "Player Two";

    // --- Homepage Method ---
    void showHomepage() {
        JFrame homepageFrame = new JFrame("X and O");
        homepageFrame.setSize(400, 300);
        homepageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homepageFrame.setResizable(false);
        homepageFrame.setLocationRelativeTo(null); // Center the window

        JPanel homepagePanel = new JPanel();
        homepagePanel.setBackground(Color.BLACK);
        homepagePanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering content

        JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 100)); // Set font for the text
        // Use HTML to apply specific colors to parts of the text
        String htmlText = "<html><font color='green'>X</font><font color='white'>|</font><font color='red'>O</font></html>";
        titleLabel.setText(htmlText);

        homepagePanel.add(titleLabel);
        homepageFrame.add(homepagePanel);
        homepageFrame.setVisible(true);

        // Timer to close homepage and open name input window after 2 seconds
        Timer timer = new Timer(5000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                homepageFrame.dispose(); // Close homepage
                getPlayerNames();         // Open name input window
            }
        });
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();           // Start the timer
    }
    // --- End Homepage Method ---


    void getPlayerNames() {
        JPanel namePanel = new JPanel(new GridLayout(4, 2));
        namePanel.add(new JLabel("Player One Name:"));
        namePanel.add(playerOneNameField);
        namePanel.add(new JLabel("Player Two Name:"));
        namePanel.add(playerTwoNameField);

        JLabel instructionLabel = new JLabel("<html>Type '<b>Computer</b>' in Player Two's name field to play against the computer.</html>");
        namePanel.add(instructionLabel);
        namePanel.add(new JLabel());

        namePanel.add(new JLabel(""));
        namePanel.add(continueButton);

        nameWindow.add(namePanel);
        nameWindow.setSize(400, 180);
        nameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameWindow.setVisible(true);
        nameWindow.setLocationRelativeTo(null); // Center the name input window

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerOneName = playerOneNameField.getText().trim();
                playerTwoName = playerTwoNameField.getText().trim();
                if (playerTwoName.equalsIgnoreCase("computer") || playerTwoName.isEmpty()) {
                    playerTwoName = "Computer";
                    computerMode = true;
                }

                if (playerOneName.isEmpty()) playerOneName = "Player One";

                nameWindow.dispose();
                drawgame();
            }
        });
    }


    void drawgame() {

        // --- Apply styles to buttons ---
        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};
        for (JButton btn : buttons) {
            btn.setBackground(Color.BLACK); // Set background to black
            btn.setBorder(new LineBorder(Color.WHITE, 2)); // Set white border with 2px thickness
            btn.setFont(new Font("Arial", Font.BOLD, 40)); // Make X/O larger
            btn.setFocusPainted(false); // Remove focus border on click
        }
        // --- End button styling ---


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
        windows.add(myFrame);
        windows.setSize(300, 300);
        windows.setVisible(true);
        windows.setLocationRelativeTo(null); // Center the game window

    }
    void playerMove(JButton btn, int position) {
        if (flag == 0) {
            playerOne.add(position);
            btn.setText("X");
            btn.setForeground(Color.GREEN); // Set X to green
            btn.setEnabled(false);
            flag = 1;
            checkWin();
            if (computerMode) {
                // Only make computer move if the game isn't already over
                if (!isGameOver()) {
                    makeComputerMove();
                }
            }
        } else {
            playertwo.add(position);
            btn.setText("O");
            btn.setForeground(Color.RED); // Set O to red
            btn.setEnabled(false);
            flag = 0;
            checkWin();
        }
    }

    // Helper method to determine if the game has concluded (win or draw)
    boolean isGameOver() {
        // Checking win for playerOne, then playerTwo, then for a draw
        return (checkWinConditionOnly(playerOne) || checkWinConditionOnly(playertwo) || (playerOne.size() + playertwo.size()) == 9);
    }

    public static void main(String[] args) {
        XandO xando = new XandO();
        xando.showHomepage(); // Start by showing the homepage
    }

    void checkWin() {
        if (checkWinConditionOnly(playerOne)) {
            playerOneWins++;
            JOptionPane.showMessageDialog(null, playerOneName + " Wins!");
            disableAllButtons();
            askToPlayAgain();
            return;

        }
        if (checkWinConditionOnly(playertwo)) {
            playerTwoWins++;
            JOptionPane.showMessageDialog(null, playerTwoName + " Wins!");
            disableAllButtons();
            askToPlayAgain();
            return;

        }
        if ((playerOne.size() + playertwo.size()) == 9) {
            draws++;
            JOptionPane.showMessageDialog(null, "It's a Draw!");
            askToPlayAgain();
            return;
        }
    }

    // New helper method to only check for win condition without dialogs or side effects
    boolean checkWinConditionOnly(ArrayList<Integer> playerMoves) {
        int[][] winCombinations = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, // Rows
                {1, 4, 7}, {2, 5, 8}, {3, 6, 9}, // Columns
                {1, 5, 9}, {3, 5, 7}             // Diagonals
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


    void askToPlayAgain() {
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            showScoreboard();
            JOptionPane.showMessageDialog(null, "Thanks for playing!");
            System.exit(0);
        }
    }
    void resetGame() {
        // Clear all button texts and reset appearance
        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};
        for (JButton btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setForeground(null); // Reset foreground color
        }

        // Clear player moves
        playerOne.clear();
        playertwo.clear();

        // Reset turn flag
        flag = 0;
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
        int playerOneLosses = playerTwoWins;
        int playerTwoLosses = playerOneWins;

        String scoreboard = playerOneName + "'s Record:\n"
                + "Wins: " + playerOneWins + "\n\n"
                + playerTwoName + "'s Record:\n"
                + "Wins: " + playerTwoWins + "\n\n"
                + "Draws: " + draws;

        JOptionPane.showMessageDialog(null, scoreboard, "Final Scoreboard", JOptionPane.INFORMATION_MESSAGE);
    }
    boolean isAvailable(int position) {
        return !(playerOne.contains(position) || playertwo.contains(position));
    }
    void makeComputerMove() {
        // Step 1: Try to win
        int move = getWinningMove(playertwo);
        if (move == -1) {
            // Step 2: Try to block player one
            move = getWinningMove(playerOne);
        }
        if (move == -1) {
            // Step 3: Pick center
            if (isAvailable(5)) move = 5;
        }
        if (move == -1) {
            // Step 4: Pick corner
            for (int i : new int[]{1, 3, 7, 9}) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }
        if (move == -1) {
            // Step 5: Pick side
            for (int i : new int[]{2, 4, 6, 8}) {
                if (isAvailable(i)) {
                    move = i;
                    break;
                }
            }
        }

        if (move != -1) {
            JButton targetButton = getButton(move);
            playerMove(targetButton, move);
        }
    }


    int getWinningMove(ArrayList<Integer> playerMoves) {
        int[][] wins = {
                {1,2,3},{4,5,6},{7,8,9},
                {1,4,7},{2,5,8},{3,6,9},
                {1,5,9},{3,5,7}
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
        switch(position) {
            case 1: return btn1;
            case 2: return btn2;
            case 3: return btn3;
            case 4: return btn4;
            case 5: return btn5;
            case 6: return btn6;
            case 7: return btn7;
            case 8: return btn8;
            case 9: return btn9;
        }
        return null;
    }
}