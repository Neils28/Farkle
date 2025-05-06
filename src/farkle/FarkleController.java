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
                updateSelectedScore();
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
                // Prevent banking if player has not reached 500 points to get on the board
                if (!model.canBankPoints()) {
                    javax.swing.JOptionPane.showMessageDialog(
                            null,
                            "You need at least 500 points to get on the board!",
                            "Not Enough Points",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    // Bank points and update the score display
                    model.bankPoints();
                    model.endTurn();
                    view.enableRollButton();
                    view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()), model.getCurrentPlayer());
                    view.updateTurnLabel(model.getCurrentPlayer());
                    view.updateRollsLeft(model.getRollsRemaining());
                    view.resetDiceDisplay();
                    view.resetRadioButtons();
                    view.resetCurrenScore(model.getCurrentScore());
                }

            }
        });

        view.getEndTurnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // End the current player's turn
                model.endTurn();
                view.enableRollButton();
                view.updateRollsLeft(model.getRollsRemaining());
                view.updateTurnLabel(model.getCurrentPlayer());
                view.resetDiceDisplay();
                view.resetRadioButtons();
                view.resetCurrenScore(model.getCurrentScore());
            }
        });

    }

    private void updateSelectedScore() {
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
