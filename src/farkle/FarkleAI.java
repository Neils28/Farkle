package farkle;

import java.util.*;

public class FarkleAI {

    private FarkleModel model;
    private FarkleView view;
    private static final int THRESHOLD_SCORE_TO_BANK = 500; // Updated threshold to match human rules

    public FarkleAI(FarkleModel model, FarkleView view) {
        this.model = model;
        this.view = view;
    }

    // Main method AI should call when it's its turn (Player 2)
    public void takeTurn() {
        if (model.getCurrentPlayer() != 1 || model.isGameOver()) {
            return; // Not AI's turn
        }

        while (model.getRollsRemaining() > 0) {

            System.out.println("AI is rolling the dice...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            model.rollDice();
            System.out.println("AI rolled: " + Arrays.toString(model.getDice()));
            int[] dice = model.getDice();
            view.updateDiceDisplay(dice);
            view.updateRollsLeft(model.getRollsRemaining());

            // Check for Farkle
            if (model.isFarkle()) {
                System.out.println("AI rolled a Farkle! No points this turn.");
                view.updateFarkleLabel();
                model.setCurrentScore(0);
                view.resetCurrenScore();
                view.resetDiceDisplay();
                view.resetRadioButtons();
                model.endTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                return;
            }

            List<Integer> rolledDice = new ArrayList<>();
            for (int die : dice) {
                rolledDice.add(die);
            }

            System.out.println("AI evaluating scoring dice...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean[] diceToHold = chooseScoringDice(rolledDice);
            model.setHeldDice(diceToHold);
            view.highlightHeldDice(diceToHold);

            List<Integer> heldDiceList = new ArrayList<>();
            for (int i = 0; i < diceToHold.length; i++) {
                if (diceToHold[i]) {
                    heldDiceList.add(dice[i]);
                }
            }

            int turnPoints = model.calculateScore(heldDiceList);
            System.out.println("AI is keeping: " + heldDiceList + " for " + turnPoints + " points");
            model.setCurrentScore(model.getCurrentScore() + turnPoints);
            view.updateCurrentScore(model.getCurrentScore());

            System.out.println("AI checking for Hot Dice...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (model.allDiceHeld() && model.isHotDice() && !heldDiceList.isEmpty() && turnPoints > 0) {
                System.out.println("AI rolled Hot Dice! Resetting.");
                model.setIsAnotherTurn(true);
                model.setBaseScoreForHotDice(model.getCurrentScore());
                model.resetHotDice();
                continue;
            }

            int remainingDice = getRemainingDiceCount();
            double expectedValue = calculateExpectedValue(remainingDice);

            System.out.println("AI evaluating whether to bank or roll again...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // AI only banks if it has at least 500 points and either:
            //  - expected value is low
            //  - no rolls left
            if ((model.getCurrentScore() >= THRESHOLD_SCORE_TO_BANK && (expectedValue <= 150 || model.getRollsRemaining() == 0))) {
                System.out.println("AI banks " + model.getCurrentScore() + " points (Expected value: " + expectedValue + ")");
                model.bankPoints();
                view.updateScoreDisplay(model.getCurrentScore(), model.getCurrentPlayer());
                model.endTurn();
                view.resetCurrenScore();
                view.resetDiceDisplay();
                view.resetRadioButtons();
                view.updateTurnLabel(model.getCurrentPlayer());
                return;
            }

            // Else, continue rolling
        }
    }

    // Helper to estimate expected value of rolling N dice
    private double calculateExpectedValue(int diceCount) {
        int[][] allRolls = generateAllRolls(diceCount);
        double total = 0;
        int validRolls = 0;

        for (int[] roll : allRolls) {
            List<Integer> rollList = new ArrayList<>();
            for (int die : roll)
                rollList.add(die);

            boolean[] toHold = chooseScoringDice(rollList);
            List<Integer> heldDice = new ArrayList<>();
            for (int i = 0; i < toHold.length; i++) {
                if (toHold[i])
                    heldDice.add(roll[i]);
            }

            int score = model.calculateScore(heldDice);
            if (score > 0) {
                total += score;
                validRolls++;
            }
        }

        return validRolls == 0 ? 0 : total / allRolls.length;
    }

    // Helper to generate all possible rolls of N dice
    private int[][] generateAllRolls(int diceCount) {
        int totalCombos = (int) Math.pow(6, diceCount);
        int[][] rolls = new int[totalCombos][diceCount];

        for (int i = 0; i < totalCombos; i++) {
            int num = i;
            for (int j = 0; j < diceCount; j++) {
                rolls[i][j] = (num % 6) + 1;
                num /= 6;
            }
        }

        return rolls;
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

    // Helper to count the number of dice not held
    private int getRemainingDiceCount() {
        boolean[] held = model.getHeldDice();
        int count = 0;
        for (boolean b : held) {
            if (!b)
                count++;
        }
        return count;
    }
}