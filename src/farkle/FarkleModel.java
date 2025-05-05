package farkle;

import java.util.*;

public class FarkleModel {

    private static final int NUM_DICE = 6;
    private static final int WINNING_SCORE = 10000;

    private int[] playerScores = new int[2];
    private int currentScore = 0;
    private int[] dice = new int[NUM_DICE];
    private int currentPlayer = 0;
    private Random random = new Random();
    private int rollsRemaining = 2;

    /**
     * Rolls all 6 dice randomly.
     */
    public void rollDice() {
        for (int i = 0; i < NUM_DICE; i++) {
            dice[i] = random.nextInt(6) + 1;
        }
        System.out.println("\nRolled dice: " + Arrays.toString(dice));
    }

    /**
     * Re-rolls only the dice that are not kept.
     * 
     * @param keptDice indices (1-based) of kept dice
     */
    public void reRollDice(String keptDice) {
        boolean[] keep = new boolean[NUM_DICE];
        String[] tokens = keptDice.split(",");
        for (String token : tokens) {
            try {
                int idx = Integer.parseInt(token.trim()) - 1;
                if (idx >= 0 && idx < NUM_DICE) {
                    keep[idx] = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid index: " + token);
            }
        }

        for (int i = 0; i < NUM_DICE; i++) {
            if (!keep[i]) {
                dice[i] = random.nextInt(6) + 1;
            }
        }
        System.out.println("Re-rolled dice: " + Arrays.toString(dice));
    }

    /**
     * Calculates the score for the selected dice values.
     *
     * @param input String of comma-separated indices (1-based)
     */
    public void scoreDice(String input) {
        String[] tokens = input.split(",");
        List<Integer> selectedDice = new ArrayList<>();
        for (String token : tokens) {
            try {
                int idx = Integer.parseInt(token.trim()) - 1;
                if (idx >= 0 && idx < NUM_DICE) {
                    selectedDice.add(dice[idx]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + token);
            }
        }

        int points = calculateScore(selectedDice);
        if (points > 0) {
            currentScore += points;
            System.out.println("Scored: " + points + " points this round. Total this turn: " + currentScore);
        } else {
            System.out.println("No valid scoring combination selected.");
        }
    }

    /**
     * Checks if the current roll is a Farkle (no scoring dice).
     */
    private boolean isFarkle() {
        List<Integer> rollList = new ArrayList<>();
        for (int die : dice) {
            rollList.add(die);
        }
        return calculateScore(rollList) == 0;
    }

    /**
     * Calculates score for a list of dice values.
     */
    private int calculateScore(List<Integer> diceList) {
        int[] counts = new int[7]; // Index 1-6
        for (int die : diceList) {
            counts[die]++;
        }

        int score = 0;

        // Six of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 6) {
                return 3000;
            }
        }

        // Five of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) {
                score += 2000;
                counts[i] = 0;
            }
        }

        // Four of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 4) {
                score += 1000;
                counts[i] = 0;
            }
        }

        // Three pairs
        int pairCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 2) {
                pairCount++;
            }
        }
        if (pairCount == 3) {
            return 1500;
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
            return 1500;
        }

        // Three of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 3) {
                score += (i == 1) ? 1000 : i * 100;
                counts[i] -= 3;
            }
        }

        // Ones and fives (outside sets)
        score += counts[1] * 100;
        score += counts[5] * 50;

        return score;
    }

    /**
     * Banks the current score if valid.
     */
    public void bankPoints() {
        if (playerScores[currentPlayer] == 0 && currentScore < 500) {
            System.out.println("You need at least 500 points to get on the board.");
        } else {
            playerScores[currentPlayer] += currentScore;
            System.out.println("Banked " + currentScore + " points.");
            currentScore = 0;
        }
    }

    /**
     * Ends the current player's turn.
     */
    public void endTurn() {
        currentScore = 0;
        rollsRemaining = 2;
        currentPlayer = (currentPlayer + 1) % 2;
    }

    public void startTurn() {
        currentScore = 0;
        rollsRemaining = 2;
        System.out.println("Player " + (currentPlayer + 1) + "'s turn.");
        rollDice();

        if (isFarkle()) {
            System.out.println("Farkle! No points this turn.");
            endTurn();
        }
    }

    // Getter methods for controller/view
    public int[] getDice() {
        return dice;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayerScore(int player) {
        return playerScores[player];
    }

    public boolean isGameOver() {
        return playerScores[0] >= WINNING_SCORE || playerScores[1] >= WINNING_SCORE;
    }

    public int getWinner() {
        return playerScores[0] >= WINNING_SCORE ? 0 : 1;
    }

    public int getRollsRemaining() {
        return rollsRemaining;
    }

    public void decrementRolls() {
        if (rollsRemaining > 0) rollsRemaining--;
    }

    public void resetRolls() {
        rollsRemaining = 2;
    }
}
