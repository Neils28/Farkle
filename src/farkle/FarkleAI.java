package farkle;

import java.util.*;

public class FarkleAI {

    private FarkleModel model;

    public FarkleAI(FarkleModel model) {
        this.model = model;
    }

    // Main method AI should call when it's its turn (Player 2)
    public void takeTurn() {
        if (model.getCurrentPlayer() != 1 || model.isGameOver()) {
            return; // Not AI's turn
        }

        boolean continueRolling = true;
        while (continueRolling) {
            model.rollDice();
            int[] dice = model.getDice();
            List<Integer> rolledDice = new ArrayList<>();

            for (int i = 0; i < dice.length; i++) {
                rolledDice.add(dice[i]);
            }

            // Pick which dice to hold based on score
            boolean[] diceToHold = chooseScoringDice(rolledDice);
            model.setHeldDice(diceToHold);

            List<Integer> heldDiceList = new ArrayList<>();
            for (int i = 0; i < diceToHold.length; i++) {
                if (diceToHold[i]) {
                    heldDiceList.add(dice[i]);
                }
            }

            int turnPoints = model.calculateScore(heldDiceList);
            model.setCurrentScore(model.getCurrentScore() + turnPoints);

            if (turnPoints == 0) {
                System.out.println("AI Farkled! Ending turn.");
                break;
            }

            if (model.allDiceHeld() && model.isHotDice()) {
                System.out.println("AI rolled Hot Dice! Resetting.");
                model.setIsAnotherTurn(true);
                model.setBaseScoreForHotDice(model.getCurrentScore());
                model.resetHotDice();
                continue; // Roll again with all dice
            }

            // Banking strategy: bank if score is 1000 or more, else keep rolling
            if (model.getCurrentScore() >= 1000 || model.getRollsRemaining() <= 0) {
                model.bankPoints();
                System.out.println("AI banks " + model.getCurrentScore() + " points.");
                model.endTurn();
                break;
            }

            // Otherwise, keep rolling
        }
    }

    // Choose which dice to hold based on score contribution
    private boolean[] chooseScoringDice(List<Integer> diceList) {
        boolean[] hold = new boolean[diceList.size()];
        int[] counts = new int[7]; // 1-6

        for (int die : diceList) {
            counts[die]++;
        }

        // Hold all scoring dice (simplified)
        for (int i = 0; i < diceList.size(); i++) {
            int die = diceList.get(i);
            if (die == 1 || die == 5 || counts[die] >= 3) {
                hold[i] = true;
            }
        }

        return hold;
    }
}