package farkle;

import javax.swing.*;
import java.awt.*;

public class FarkleView extends JFrame {

    private JButton rollButton;
    private JButton bankPointsButton;
    private JButton endTurnButton;
    private JButton scoreSheetButton;
    private JLabel[] diceLabels;
    private JPanel dicePanel;
    private JLabel currentScoreLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel turnsLeft;
    private JLabel turnLabel;
    private JLabel farkleLabel;
    private JRadioButton[] diceButtons = new JRadioButton[6];

    public FarkleView() {

        FarkleView view = this;
        this.setTitle("Farkle Game");
        this.setSize(600, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the location of the window to the center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel scoreSheetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        scoreSheetButton = new JButton("Score Sheet");
        scoreSheetButton.setPreferredSize(new Dimension(100, 30));
        scoreSheetPanel.add(scoreSheetButton);
        mainPanel.add(scoreSheetPanel);

        turnLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(turnLabel);

        dicePanel = new JPanel();
        dicePanel.setLayout(new GridLayout(1, 6));
        diceLabels = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            diceLabels[i] = new JLabel();
            diceLabels[i].setPreferredSize(new Dimension(50, 50));
            diceLabels[i].setMaximumSize(new Dimension(50, 50));
            diceLabels[i].setMinimumSize(new Dimension(50, 50));
            diceLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            diceLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            diceLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dicePanel.add(diceLabels[i]);
        }
        mainPanel.add(dicePanel);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new GridLayout(1, 6, 10, 0));
        for (int i = 0; i < 6; i++) {
            JPanel radioPanel = new JPanel();
            radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
            JLabel spacer = new JLabel(); // Optional spacer
            spacer.setPreferredSize(new Dimension(50, 0));
            diceButtons[i] = new JRadioButton();
            JRadioButton diceRadioButton = diceButtons[i];
            diceRadioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            radioPanel.add(Box.createVerticalStrut(5));
            radioPanel.add(diceRadioButton);
            checkBoxPanel.add(radioPanel);
        }
        mainPanel.add(checkBoxPanel);

        farkleLabel = new JLabel("", SwingConstants.CENTER);
        farkleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(farkleLabel);

        currentScoreLabel = new JLabel("Current Turn Score: 0", SwingConstants.CENTER);
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(currentScoreLabel);

        turnsLeft = new JLabel("Rolls Left: 3", SwingConstants.CENTER);
        turnsLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(turnsLeft);

        JPanel buttonPanel = new JPanel();
        rollButton = new JButton("Roll Dice");
        bankPointsButton = new JButton("Bank Points");
        endTurnButton = new JButton("End Turn");
        buttonPanel.add(rollButton);
        buttonPanel.add(bankPointsButton);
        buttonPanel.add(endTurnButton);
        mainPanel.add(buttonPanel);

        JPanel scorePanel = new JPanel(new GridLayout(1, 2));
        player1ScoreLabel = new JLabel("Player 1 Score: 0", SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Player 2 Score: 0", SwingConstants.CENTER);
        scorePanel.add(player1ScoreLabel);
        scorePanel.add(player2ScoreLabel);
        mainPanel.add(scorePanel);

        this.add(mainPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public JButton getScoreSheetButton() {
        return scoreSheetButton;
    }

    public JButton getRollButton() {
        return rollButton;
    }

    public JButton getBankPointsButton() {
        return bankPointsButton;
    }

    public JButton getEndTurnButton() {
        return endTurnButton;
    }

    public void updateScoreDisplay(int score, int player) {
        if (player == 0) {
            player1ScoreLabel.setText("Player 1 Score: " + score);
        } else {
            player2ScoreLabel.setText("Player 2 Score: " + score);
        }
    }

    public void updateCurrentScore(int score) {
        currentScoreLabel.setText("Current Turn Score: " + score);
    }

    public void updateRollsLeft(int rollsLeft) {
        turnsLeft.setText("Rolls Left: " + rollsLeft);
    }

    public void updateDiceDisplay(int[] dice) {
        for (int i = 0; i < dice.length; i++) {
            String path = "/farkle/images/die" + dice[i] + ".png";
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
            Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            diceLabels[i].setIcon(icon);
        }
    }

    public void resetDiceDisplay() {
        for (int i = 0; i < diceLabels.length; i++) {
            diceLabels[i].setIcon(null);
        }
    }

    public void updateTurnLabel(int currentPlayer) {
        turnLabel.setText("Player " + (currentPlayer + 1) + "'s Turn");
    }

    public void disableRollButton() {
        rollButton.setEnabled(false);
    }

    public void enableRollButton() {
        rollButton.setEnabled(true);
    }

    public JRadioButton[] getDiceButtons() {
        return diceButtons;
    }

    public void resetRadioButtons() {
        for (JRadioButton button : diceButtons) {
            button.setSelected(false);
        }
    }

    public void resetCurrenScore() {
        currentScoreLabel.setText("Current Turn Score: " + 0);
    }

    public void updateFarkleLabel() {
        farkleLabel.setText("Farkle! You lose all your points and your turn!");
        farkleLabel.setForeground(Color.RED);
    }

    public void disableBankPointsButton() {
        bankPointsButton.setEnabled(false);
    }

    public void resetFarkleLable() {
        farkleLabel.setText("");
    }

    public void enableBankPointsButton() {
        bankPointsButton.setEnabled(true);
    }

    public void highlightHeldDice(boolean[] held) {
        for (int i = 0; i < held.length; i++) {
            diceButtons[i].setSelected(held[i]);
        }
    }

    public void resetForNextTurn() {
        resetDiceDisplay();
        resetRadioButtons();
        resetCurrenScore();
        resetFarkleLable();
        enableRollButton();
        enableBankPointsButton();
    }
}
