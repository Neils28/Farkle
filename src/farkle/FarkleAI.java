package farkle;

import java.util.*;

import javax.swing.JOptionPane;

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
            // show popup message
            JOptionPane.showMessageDialog(view, "It's not AI's turn or the game is over.", "Invalid Turn",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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
                // show popup message
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

            List<Integer> heldThisTurn = new ArrayList<>();
            indexInNew = 0;
            for (int i = 0; i < dice.length; i++) {
                if (!heldAlready[i]) {
                    if (indexInNew < diceToHold.length && diceToHold[indexInNew]) {
                        heldThisTurn.add(dice[i]);
                    }
                    indexInNew++;
                }
            }

            int selectedScore = model.calculateScore(heldThisTurn);
            model.addToCurrentScore(selectedScore);
            view.updateCurrentScore(model.getCurrentScore());

            if (!model.isAnotherTurn()) {
                model.setIsAnotherTurn(false);
            }

            int remainingDice = getRemainingDiceCount();
            double expectedValue = calculateExpectedValue(remainingDice);

            if (model.allDiceHeld() && model.isHotDice() && !heldThisTurn.isEmpty()) {
                // show popup message
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
                // check for winning condition and then display both players' scores
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

            if (rolls >= 3) {
                if (model.getCurrentScore() >= THRESHOLD_SCORE_TO_BANK) {
                    // show popup message
                    JOptionPane.showMessageDialog(view,
                            "AI has reached the max rolls and is banking points.", "Max Rolls",
                            JOptionPane.INFORMATION_MESSAGE);
                    model.bankPoints();
                    view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()), model.getCurrentPlayer());
                } else {
                    // show popup message
                    JOptionPane.showMessageDialog(view,
                            "AI has reached the max rolls, but doesn't have enough points to bank", "Not Enough Points",
                            JOptionPane.INFORMATION_MESSAGE);
                    model.bankPoints();
                    view.updateScoreDisplay(model.getPlayerScore(model.getCurrentPlayer()), model.getCurrentPlayer());
                    model.setCurrentScore(0);
                }
                model.endTurn();
                view.resetForNextTurn();
                view.updateTurnLabel(model.getCurrentPlayer());
                return;
            }
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
        int[] counts = new int[7];
        for (int die : diceList) {
            counts[die]++;
        }

        // Straight (1-6)
        boolean isStraight = true;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] != 1) {
                isStraight = false;
                break;
            }
        }
        if (isStraight) {
            Arrays.fill(hold, true);
            return hold;
        }

        // Three pairs
        int pairCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 2) {
                pairCount++;
            }
        }
        if (pairCount == 3) {
            Arrays.fill(hold, true);
            return hold;
        }

        // Two triplets
        List<Integer> triplets = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 3) {
                triplets.add(i);
            }
        }
        if (triplets.size() == 2) {
            for (int triplet : triplets) {
                int need = 3;
                for (int i = 0; i < diceList.size(); i++) {
                    if (diceList.get(i) == triplet && need > 0) {
                        hold[i] = true;
                        need--;
                    }
                }
            }
            return hold;
        }

        // Four of a kind + a pair
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 4) {
                for (int j = 1; j <= 6; j++) {
                    if (i != j && counts[j] == 2) {
                        Arrays.fill(hold, true);
                        return hold;
                    }
                }
            }
        }

        // Six of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 6) {
                Arrays.fill(hold, true);
                return hold;
            }
        }

        // Five of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 5) {
                int need = 5;
                for (int j = 0; j < diceList.size(); j++) {
                    if (diceList.get(j) == i && need > 0) {
                        hold[j] = true;
                        need--;
                    }
                }
                counts[i] -= 5;
            }
        }

        // Four of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 4) {
                int need = 4;
                for (int j = 0; j < diceList.size(); j++) {
                    if (diceList.get(j) == i && need > 0) {
                        hold[j] = true;
                        need--;
                    }
                }
                counts[i] -= 4;
            }
        }

        // Three of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 3) {
                int need = 3;
                for (int j = 0; j < diceList.size(); j++) {
                    if (diceList.get(j) == i && need > 0) {
                        hold[j] = true;
                        need--;
                    }
                }
                counts[i] -= 3;
            }
        }

        // Remaining 1s and 5s
        for (int i = 0; i < diceList.size(); i++) {
            int die = diceList.get(i);
            if ((die == 1 && counts[1] > 0) || (die == 5 && counts[5] > 0)) {
                hold[i] = true;
                counts[die]--;
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