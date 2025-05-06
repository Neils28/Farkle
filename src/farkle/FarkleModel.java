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
    private int rollsRemaining = 3;
    private boolean[] heldDice = new boolean[NUM_DICE];

    /**
     * Rolls all 6 dice randomly.
     */
    public void rollDice() {
        for (int i = 0; i < NUM_DICE; i++) {
            if (!heldDice[i]) {
                dice[i] = random.nextInt(6) + 1;
            }
        }
        rollsRemaining--;
    }

    /**
     * Checks if the current roll is a Farkle (no scoring dice).
     */
    public boolean isFarkle() {
        // int[] counts = new int[7]; // Index 1-6
        // for (int die : dice) {
        // counts[die]++;
        // }

        // // Check if there are no scoring dice
        // boolean hasScoringDice = false;
        // for (int i = 1; i <= 6; i++) {
        // if (counts[i] >= 3 || (i == 1 && counts[i] > 0) || (i == 5 && counts[i] > 0))
        // {
        // hasScoringDice = true;
        // break;
        // }
        // }
        return false; // Placeholder, implement logic to check for Farkle
    }

    /**
     * Calculates score for a list of dice values.
     */
    public int calculateScore(List<Integer> diceList) {
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

    public boolean canBankPoints() {
        if (playerScores[currentPlayer] == 0 && currentScore < 500) {
            return false; // Player needs at least 500 points to bank
        }
        return true; // Player can bank points if they have a score
    }

    /**
     * Banks the current score if valid.
     */
    public void bankPoints() {
        playerScores[currentPlayer] += currentScore;
    }

    /**
     * Ends the current player's turn.
     */
    public void endTurn() {
        currentScore = 0;
        rollsRemaining = 3;
        currentPlayer = (currentPlayer + 1) % 2;
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

    public void setHeldDice(boolean[] selected) {
        for (int i = 0; i < NUM_DICE; i++) {
            heldDice[i] = selected[i];
        }
    }

    public void setCurrentScore(int score) {
        currentScore = score;
    }
}
