package farkle;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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
                boolean[] selected = new boolean[6];
                for (int j = 0; j < buttons.length; j++) {
                    selected[j] = buttons[j].isSelected();
                }
                model.setHeldDice(selected);
                updateKeptDiceScore();
            });

        }

        // Add action listeners to the view components
        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Check if held dice are scoring dice before rolling
                if (!model.isHoldingScoringDice() && model.getRollsRemaining() < 3) {
                    JOptionPane.showMessageDialog(
                            null,
                            "You must set aside at least one scoring die before rolling.\nOnly valid combinations may be held",
                            "Invalid Roll",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                model.rollDice();
                view.updateDiceDisplay(model.getDice());
                view.updateRollsLeft(model.getRollsRemaining());
                updateKeptDiceScore();

                if (model.isFarkle()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Farkle! You lose all points for this turn.",
                            "Farkle",
                            JOptionPane.WARNING_MESSAGE);
                    model.endTurn();
                    view.updateRollsLeft(model.getRollsRemaining());
                    view.updateTurnLabel(model.getCurrentPlayer());
                    view.resetDiceDisplay();
                    view.resetRadioButtons();
                    view.resetCurrenScore();
                }

                if (model.getRollsRemaining() <= 0) {
                    view.disableRollButton();
                }
            }
        });

        view.getBankPointsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if the player has scored at least 500 points

                if (model.allDiceHeld() && model.isHotDice()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Hot dice! All dice scored, so you get to roll all 6 again!",
                            "Hot Dice!",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Hot dice! All dice scored, so you get to roll all 6 again!");
                    model.resetHotDice();
                    view.resetRadioButtons();
                    view.updateRollsLeft(model.getRollsRemaining());
                    return;
                }

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
                        "- 1-6 straight = 1500 points.\n" +
                        "- Three pairs = 1500 points.\n" +
                        "- Two triplets = 2500 points.\n" +
                        "- Four of a kind + a pair = 1500 points.\n" +
                        "- Six of a kind = 3000 points.\n" +
                        "- Five of a kind = 2000 points.\n" +
                        "- Four of a kind = 1000 points.\n" +
                        "- Three of a kind = 100 times the number (e.g., three 2s = 200 points; three 1s = 1000).\n" +
                        "- 1s = 100 points each.\n" +
                        "- 5s = 50 points each.";
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
