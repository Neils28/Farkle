package farkle;

import java.util.*;
import javax.swing.JOptionPane;

public class FarkleAI {

    private FarkleModel model;
    private FarkleView view;
    private static final int THRESHOLD_SCORE_TO_BANK = 500;

    public FarkleAI(FarkleModel model, FarkleView view) {
        this.model = model;
        this.view = view;
    }

    public void takeTurn() {
        int rolls = 0;

        while (rolls < 3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            model.rollDice();
            rolls++;
            view.updateDiceDisplay(model.getDice());
            view.updateRollsLeft(model.getRollsRemaining());

            if (model.isFarkle()) {
                JOptionPane.showMessageDialog(view, "AI rolled a Farkle! No points this turn.", "Farkle",
                        JOptionPane.INFORMATION_MESSAGE);
                model.setCurrentScore(0);
                view.resetForNextTurn();
                model.endTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                return;
            }

            int[] dice = model.getDice();
            boolean[] heldAlready = model.getHeldDice();
            List<Integer> newlyRolledDice = new ArrayList<>();

            for (int i = 0; i < dice.length; i++) {
                if (!heldAlready[i]) {
                    newlyRolledDice.add(dice[i]);
                }
            }

            boolean[] diceToHold = chooseScoringDice(newlyRolledDice);
            boolean[] updatedHeld = Arrays.copyOf(heldAlready, dice.length);

            int indexInNew = 0;
            for (int i = 0; i < dice.length; i++) {
                if (!heldAlready[i]) {
                    if (indexInNew < diceToHold.length && diceToHold[indexInNew]) {
                        updatedHeld[i] = true;
                    }
                    indexInNew++;
                }
            }

            model.setHeldDice(updatedHeld);
            view.highlightHeldDice(updatedHeld);

            List<Integer> allHeldDice = new ArrayList<>();
            for (int i = 0; i < dice.length; i++) {
                if (updatedHeld[i]) {
                    allHeldDice.add(dice[i]);
                }
            }

            int totalHeldScore = model.calculateScore(allHeldDice);
            if (model.isAnotherTurn()) {
                model.addToCurrentScore(totalHeldScore);
            } else {
                model.setCurrentScore(totalHeldScore);
            }
            view.updateCurrentScore(model.getCurrentScore());

            if (!model.isAnotherTurn()) {
                model.setIsAnotherTurn(false);
            }

            int remainingDice = getRemainingDiceCount();
            int rollsLeft = 3 - rolls;
            int aiScore = model.getPlayerScore(1);
            int humanScore = model.getPlayerScore(0);
            int scoreDifference = aiScore - humanScore;

            double expectedValue = calculateExpectedValue(remainingDice, rollsLeft, scoreDifference);

            if (model.allDiceHeld() && model.isHotDice() && !allHeldDice.isEmpty()) {
                JOptionPane.showMessageDialog(view, "AI has hot dice! Rolling again.", "Hot Dice",
                        JOptionPane.INFORMATION_MESSAGE);
                model.setIsAnotherTurn(true);
                model.setBaseScoreForHotDice(model.getCurrentScore());
                model.resetHotDice();
                model.setHeldDice(new boolean[6]);
                rolls = 0;
                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if ((model.getCurrentScore() >= THRESHOLD_SCORE_TO_BANK &&
                    (expectedValue <= 150 || rolls >= 3))) {
                JOptionPane.showMessageDialog(view,
                        "AI is banking " + model.getCurrentScore() + " points.", "Banking Points",
                        JOptionPane.INFORMATION_MESSAGE);
                model.bankPoints();
                if (model.getPlayerScore(model.getCurrentPlayer()) >= model.getWinningScore()) {
                    JOptionPane.showMessageDialog(view,
                            "AI wins with the game!" +
                                    "\nPlayer 1: " + model.getPlayerScore(0) + " points" +
                                    "\nPlayer 2: " + model.getPlayerScore(1) + " points",
                            "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()), model.getCurrentPlayer());
                model.endTurn();
                view.resetForNextTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                return;
            }

            if (rolls == 3) {
                if (model.getPlayerScore(model.getCurrentPlayer()) == 0 && model.getCurrentScore() < 500) {
                    JOptionPane.showMessageDialog(view,
                            "AI doesn't have enough points to bank. Ending turn.",
                            "Not Enough Points", JOptionPane.INFORMATION_MESSAGE);
                    model.setCurrentScore(0);
                    view.resetForNextTurn();
                    model.endTurn();
                    view.updateTurnLabel(model.getCurrentPlayer());
                    return;
                } else {
                    JOptionPane.showMessageDialog(view,
                            "AI is banking " + model.getCurrentScore() + " points.", "Banking Points",
                            JOptionPane.INFORMATION_MESSAGE);
                    model.bankPoints();
                    if (model.getPlayerScore(model.getCurrentPlayer()) >= model.getWinningScore()) {
                        JOptionPane.showMessageDialog(view,
                                "AI wins with the game!" +
                                        "\nPlayer 1: " + model.getPlayerScore(0) + " points" +
                                        "\nPlayer 2: " + model.getPlayerScore(1) + " points",
                                "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                    view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()),
                            model.getCurrentPlayer());
                    model.endTurn();
                    view.resetForNextTurn();
                    view.updateTurnLabel(model.getCurrentPlayer());
                    return;
                }
            }
        }
    }

    private double calculateExpectedValue(int diceCount, int rollsLeft, int scoreDifference) {
        int[][] allRolls = generateAllRolls(diceCount);
        double totalScore = 0;
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
                totalScore += score;
                validRolls++;
            }
        }

        if (validRolls == 0) return 0;

        double averageScore = totalScore / allRolls.length;

        // Adjustment factor
        double aggressionFactor = 1.0;

        if (scoreDifference < -1000) {
            aggressionFactor += 0.3; // losing badly, take more risks
        } else if (scoreDifference > 1000) {
            aggressionFactor -= 0.3; // winning comfortably, play safe
        }

        aggressionFactor += 0.1 * rollsLeft; // more rolls left = more risk okay

        return averageScore * aggressionFactor;
    }

    private int[][] generateAllRolls(int diceCount) {
        int totalCombos = (int) Math.pow(6, diceCount);
        int[][] rollCombo = new int[totalCombos][diceCount];

        for (int i = 0; i < totalCombos; i++) {
            int num = i;
            for (int j = 0; j < diceCount; j++) {
                rollCombo[i][j] = (num % 6) + 1;
                num /= 6;
            }
        }

        return rollCombo;
    }

    private boolean[] chooseScoringDice(List<Integer> diceList) {
        boolean[] hold = new boolean[diceList.size()];
        boolean[] used = new boolean[diceList.size()];

        while (true) {
            int bestScore = 0;
            List<Integer> bestSubsetIndices = new ArrayList<>();

            int n = diceList.size();
            for (int mask = 1; mask < (1 << n); mask++) {
                List<Integer> subset = new ArrayList<>();
                List<Integer> indices = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    if (((mask & (1 << i)) != 0) && !used[i]) {
                        subset.add(diceList.get(i));
                        indices.add(i);
                    }
                }

                int score = model.calculateScore(subset);
                if (score > bestScore) {
                    bestScore = score;
                    bestSubsetIndices = indices;
                }
            }

            if (bestScore == 0) {
                break;
            }

            for (int idx : bestSubsetIndices) {
                hold[idx] = true;
                used[idx] = true;
            }
        }

        return hold;
    }

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