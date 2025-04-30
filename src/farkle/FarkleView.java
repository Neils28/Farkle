package farkle;

import javax.swing.*;
import java.awt.*;

public class FarkleView extends JFrame {

    private JButton rollButton;
    private JLabel scoreLabel;
    private JTextArea diceArea;
    private JTextArea messageArea;

    public FarkleView() {
        setTitle("Farkle Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        rollButton = new JButton("Roll Dice");
        scoreLabel = new JLabel("Score: 0");
        diceArea = new JTextArea();
        messageArea = new JTextArea();

        add(rollButton, BorderLayout.NORTH);
        add(scoreLabel, BorderLayout.SOUTH);
        add(new JScrollPane(diceArea), BorderLayout.CENTER);
        add(new JScrollPane(messageArea), BorderLayout.EAST);

        setVisible(true);
    }

    public JButton getRollButton() {
        return rollButton;
    }

    public void updateScoreDisplay(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateDiceDisplay(int[] dice) {
        StringBuilder sb = new StringBuilder();
        for (int die : dice) {
            sb.append(die).append(" ");
        }
        diceArea.setText(sb.toString());
    }

}
