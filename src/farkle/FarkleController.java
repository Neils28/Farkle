package farkle;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * FarkleController serves as the main controller for handling user interactions
 * and game logic
 * between the FarkleModel and FarkleView. It sets up listeners and processes
 * actions like rolling,
 * selecting dice, banking points, and ending turns.
 */
public class FarkleController {

    private FarkleModel model;
    private FarkleView view;
    private FarkleAI ai;

    /**
     * Constructs a FarkleController, initializing the model, view, and AI
     * components.
     * Sets up action listeners for dice selection, rolling, banking points, ending
     * turns,
     * and displaying the score sheet.
     * 
     * @param model the game model
     * @param view  the game view
     */
    public FarkleController(FarkleModel model, FarkleView view) {
        this.model = model;
        this.view = view;
        this.ai = new FarkleAI(model, view);

        // Setup dice selection: When a dice radio button is selected, update held dice
        // and score.
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

        // Handle Roll Dice button logic
        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enforce rule: must hold at least one scoring die before rerolling
                if (!model.isHoldingScoringDice() && model.getRollsRemaining() < 3) {
                    JOptionPane.showMessageDialog(
                            null,
                            "You must set aside at least one scoring die before rolling.\nOnly valid combinations may be held",
                            "Invalid Roll",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update dice display and roll count
                model.rollDice();
                view.updateDiceDisplay(model.getDice());
                view.updateRollsLeft(model.getRollsRemaining());

                // If the roll results in a Farkle, disable further action
                if (model.isFarkle()) {
                    view.updateFarkleLabel();
                    view.disableRollButton();
                    view.disableBankPointsButton();
                }

                if (model.getRollsRemaining() <= 0) {
                    view.disableRollButton();
                }
            }
        });

        // Handle Bank Points button logic
        view.getBankPointsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Hot Dice logic — reset for another turn
                if ((model.getPlayerScore(model.getCurrentPlayer()) + model.getCurrentScore()) >= model
                        .getWinningScore()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Player wins the game!" +
                                    "\nPlayer: " + (model.getPlayerScore(0) + model.getCurrentScore()) +
                                    "\nAI score: " + model.getPlayerScore(1),
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                if (model.allDiceHeld() && model.isHotDice()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Hot dice! All dice scored, so you get to roll all 6 again!",
                            "Hot Dice!",
                            JOptionPane.INFORMATION_MESSAGE);
                    model.setIsAnotherTurn(true);
                    model.resetHotDice();
                    view.resetForNextTurn();
                    view.updateRollsLeft(model.getRollsRemaining());
                    return;
                }

                // Enforce first bank rule: minimum 500 points
                if (model.getCurrentScore() < 500 && model.getPlayerScore(model.getCurrentPlayer()) == 0) {
                    javax.swing.JOptionPane.showMessageDialog(
                            null,
                            "You must score at least 500 points to bank.",
                            "Bank Points",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Bank score, update UI and prepare next player's turn
                model.bankPoints();
                view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()),
                        model.getCurrentPlayer());
                model.endTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                view.updateRollsLeft(model.getRollsRemaining());
                view.resetForNextTurn();

                if (model.getCurrentPlayer() == 1) {
                    new Thread(() -> ai.takeTurn()).start();
                }
            }
        });

        // Handle End Turn button logic (manual override)
        view.getEndTurnButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // End current turn and transition to next player
                model.endTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                view.updateRollsLeft(model.getRollsRemaining());
                view.resetForNextTurn();

                if (model.getCurrentPlayer() == 1) {
                    new Thread(() -> ai.takeTurn()).start();
                }
            }
        });

        // Display scoring rules in a dialog box
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

    /**
     * updateKeptDiceScore calculates the current turn's score based on selected
     * dice.
     * Handles regular scoring and Hot Dice bonus turns.
     */
    private void updateKeptDiceScore() {
        JRadioButton[] buttons = view.getDiceButtons();
        int[] dice = model.getDice();
        List<Integer> selectedDice = new ArrayList<>();
        // Gather all selected dice
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].isSelected()) {
                selectedDice.add(dice[i]);
            }
        }

        // Calculate score from selected dice
        int selectedScore = model.calculateScore(selectedDice);

        // Add to score if in a Hot Dice bonus round
        if (model.isAnotherTurn()) {
            int combinedScore = model.getBaseScoreForHotDice() + selectedScore;
            model.setCurrentScore(combinedScore);
        } else {
            model.setCurrentScore(selectedScore);
            model.setBaseScoreForHotDice(selectedScore);
        }

        // Update the score display
        view.updateCurrentScore(model.getCurrentScore());
    }

}
