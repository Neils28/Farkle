package farkle;

import javax.swing.*;
import java.awt.*;

public class FarkleView extends JFrame {

    private JButton rollButton;
    private JButton bankPointsButton;
    private JLabel[] diceLabels;
    private JPanel dicePanel;
    private JLabel currentScoreLabel;

    public FarkleView() {

        FarkleView view = this;
        this.setTitle("Farkle Game");
        this.setSize(600, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel turnLabel = new JLabel("Player 1's turn", SwingConstants.CENTER);
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

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(1, 6, 10, 0));
        for (int i = 0; i < 6; i++) {
            JPanel cellPanel = new JPanel();
            cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));
            JLabel spacer = new JLabel(); // Optional spacer
            spacer.setPreferredSize(new Dimension(50, 0));
            JCheckBox diceCheckBox = new JCheckBox();
            diceCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            cellPanel.add(Box.createVerticalStrut(5));
            cellPanel.add(diceCheckBox);
            radioPanel.add(cellPanel);
        }
        mainPanel.add(radioPanel);

        currentScoreLabel = new JLabel("Current Turn Score: 0", SwingConstants.CENTER);
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(currentScoreLabel);

        JPanel buttonPanel = new JPanel();
        rollButton = new JButton("Roll Dice");
        bankPointsButton = new JButton("Bank Points");
        buttonPanel.add(rollButton);
        buttonPanel.add(bankPointsButton);
        mainPanel.add(buttonPanel);

        JPanel scorePanel = new JPanel(new GridLayout(1, 2));
        JLabel player1ScoreLabel = new JLabel("Player 1 Score: 0", SwingConstants.CENTER);
        JLabel player2ScoreLabel = new JLabel("Player 2 Score: 0", SwingConstants.CENTER);
        scorePanel.add(player1ScoreLabel);
        scorePanel.add(player2ScoreLabel);
        mainPanel.add(scorePanel);

        this.add(mainPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public JButton getRollButton() {
        return rollButton;
    }

    public JButton getBankPointsButton() {
        return bankPointsButton;
    }

    public void updateScoreDisplay(int score) {
        // This method may need updating or removal depending on usage
    }

    public void updateCurrentScore(int score) {
        currentScoreLabel.setText("Current Turn Score: " + score);
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

}
