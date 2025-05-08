package farkle;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FarkleController {

    private FarkleModel model;
    private FarkleView view;

    public FarkleController(FarkleModel model, FarkleView view) {
        this.model = model;
        this.view = view;

        JRadioButton[] buttons = view.getDiceButtons();
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].addActionListener(e -> {
                // Toggle the selection of the radio button
                updateKeptDiceScore();
            });

        }

        // Add action listeners to the view components
        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if the player has rolls remaining
                // If not, disable the roll button
                if ((model.getRollsRemaining() - 1) <= 0) {
                    view.disableRollButton();
                    model.rollDice();
                    view.updateDiceDisplay(model.getDice());
                    view.updateRollsLeft(model.getRollsRemaining());
                    return;
                }
                if (model.isFarkle()) {
                    javax.swing.JOptionPane.showMessageDialog(
                            null,
                            "Farkle! You lose all points for this turn.",
                            "Farkle",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    model.endTurn();
                    view.updateRollsLeft(model.getRollsRemaining());
                    view.updateTurnLabel(model.getCurrentPlayer());
                }

                // Roll the dice and update the display
                JRadioButton[] buttons = view.getDiceButtons();
                boolean[] selected = new boolean[6];
                for (int i = 0; i < buttons.length; i++) {
                    selected[i] = buttons[i].isSelected();
                }
                model.setHeldDice(selected);
                model.rollDice();
                view.updateDiceDisplay(model.getDice());
                view.updateRollsLeft(model.getRollsRemaining());
            }
        });

        view.getBankPointsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if the player has scored at least 500 points
                if (model.getCurrentScore() < 500 && model.getPlayerScore(model.getCurrentPlayer()) == 0) {
                    javax.swing.JOptionPane.showMessageDialog(
                            null,
                            "You must score at least 500 points to bank.",
                            "Bank Points",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    model.bankPoints();

                    view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()),
                            model.getCurrentPlayer());
                    model.endTurn();
                    view.updateTurnLabel(model.getCurrentPlayer());
                    view.updateRollsLeft(model.getRollsRemaining());
                    view.resetDiceDisplay();
                    view.resetRadioButtons();
                    view.resetCurrenScore();
                    view.enableRollButton();

                }

            }
        });

        view.getEndTurnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // End the turn and update the display
                model.endTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                view.updateRollsLeft(model.getRollsRemaining());
                view.resetDiceDisplay();
                view.resetRadioButtons();
                view.resetCurrenScore();
                view.enableRollButton();
            }
        });

        view.getScoreSheetButton().addActionListener(new ActionListener() {
            // Show a pop up with the scoring rules
            @Override
            public void actionPerformed(ActionEvent e) {
                String rules = "SCORING:\n" +
                        "- 1s = 100 points each.\n" +
                        "- 5s = 50 points each.\n" +
                        "- Three of a kind = 100 times the number (e.g., three 2s = 200 points).\n" +
                        "- Four of a kind = 1000.\n" +
                        "- Five of a kind = 2000.\n" +
                        "- Six of a kind = 3000.\n" +
                        "- 1-6 straight = 1500.\n" +
                        "- Three pairs = 1500.\n" +
                        "- Two triplets = 2500.\n" +
                        "- Four of a kind + pair = 1500.";
                JOptionPane.showMessageDialog(view, rules, "Scoring Rules", JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }

    private void updateKeptDiceScore() {
        JRadioButton[] buttons = view.getDiceButtons();
        int[] dice = model.getDice();
        List<Integer> selectedDice = new ArrayList<>();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].isSelected()) {
                selectedDice.add(dice[i]);
            }
        }
        int score = model.calculateScore(selectedDice);
        model.setCurrentScore(score);
        view.updateCurrentScore(score);
    }

}
