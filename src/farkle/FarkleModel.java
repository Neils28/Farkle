package farkle;

import java.util.*;

public class FarkleModel {

    private static final int NUM_DICE = 6;
    private static final int WINNING_SCORE = 10000;

    private int[] playerScores;
    private int currentScore;
    private int[] dice;
    private int currentPlayer;
    private Random random;
    private int rollsRemaining;
    private boolean[] heldDice;
    private boolean isAnotherTurn = false;
    private int baseScoreForHotDice = 0;

    public FarkleModel() {
        this.playerScores = new int[2];
        this.currentScore = 0;
        this.dice = new int[NUM_DICE];
        this.currentPlayer = 0;
        this.random = new Random();
        this.rollsRemaining = 3;
        this.heldDice = new boolean[NUM_DICE];
    }

    public void rollDice() {

        for (int i = 0; i < NUM_DICE; i++) {
            if (!heldDice[i]) {
                dice[i] = random.nextInt(6) + 1;
            }
        }
        rollsRemaining--;
    }

    public boolean isFarkle() {
        List<Integer> rolledDice = new ArrayList<>();
        for (int i = 0; i < NUM_DICE; i++) {
            if (!heldDice[i]) {
                rolledDice.add(dice[i]);
            }
        }
        return calculateScore(rolledDice) == 0;
    }

    public int calculateScore(List<Integer> diceList) {
        int[] counts = new int[7]; // Index 1-6
        for (int die : diceList) {
            counts[die]++;
        }

        int score = 0;

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

        // Two triplets
        int tripletCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 3) {
                tripletCount++;
            }
        }
        if (tripletCount == 2) {
            return 2500;
        }

        // Four of a kind + a pair
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 4) {
                for (int j = 1; j <= 6; j++) {
                    if (i != j && counts[j] == 2) {
                        return 1500;
                    }
                }
            }
        }

        // Six of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 6)
                return 3000;
        }

        // Five of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) {
                score += 2000;
                counts[i] -= 5;
            }
        }

        // Four of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 4) {
                score += 1000;
                counts[i] -= 4;
            }
        }

        // Three of a kind
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 3) {
                score += (i == 1) ? 1000 : i * 100;
                counts[i] -= 3;
            }
        }

        // Remaining ones and fives
        score += counts[1] * 100;
        score += counts[5] * 50;

        return score;
    }

    public void bankPoints() {
        playerScores[currentPlayer] += currentScore;
    }

    public void endTurn() {
        currentScore = 0;
        rollsRemaining = 3;
        Arrays.fill(heldDice, false);
        currentPlayer = (currentPlayer + 1) % 2;
        baseScoreForHotDice = 0;
    }

    // Getters
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
        this.heldDice = Arrays.copyOf(selected, NUM_DICE);
    }

    public void setCurrentScore(int score) {
        this.currentScore = score;
    }

    public boolean isHoldingScoringDice() {
        List<Integer> heldValues = new ArrayList<>();
        for (int i = 0; i < NUM_DICE; i++) {
            if (heldDice[i]) {
                heldValues.add(dice[i]);
            }
        }

        if (heldValues.isEmpty())
            return false;

        int totalScore = calculateScore(heldValues);

        for (int i = 0; i < heldValues.size(); i++) {
            List<Integer> copy = new ArrayList<>(heldValues);
            copy.remove(i);
            if (calculateScore(copy) == totalScore) {
                return false; // this die contributed nothing
            }
        }

        return true;
    }

    public boolean allDiceHeld() {
        for (boolean held : heldDice) {
            if (!held)
                return false;
        }
        return true;
    }

    public void resetHotDice() {
        Arrays.fill(heldDice, false);
        rollsRemaining = 3;
    }

    public boolean isHotDice() {
        List<Integer> heldValues = new ArrayList<>();
        int heldCount = 0;
        for (int i = 0; i < NUM_DICE; i++) {
            if (heldDice[i]) {
                heldValues.add(dice[i]);
                heldCount++;
            }
        }
        if (heldCount != NUM_DICE)
            return false;

        // Re-score using calculateScore, but compare to the score of individual dice
        int totalScore = calculateScore(heldValues);

        // Simulate unscoring each die: if removing any one reduces the score, it's
        // contributing
        for (int i = 0; i < heldValues.size(); i++) {
            List<Integer> copy = new ArrayList<>(heldValues);
            copy.remove(i);
            if (calculateScore(copy) == totalScore) {
                // This die didn't contribute to the score
                return false;
            }
        }

        return true;
    }

    public boolean isAnotherTurn() {
        return isAnotherTurn;
    }

    public void setIsAnotherTurn(boolean anotherTurn) {
        isAnotherTurn = anotherTurn;
    }

    public int getBaseScoreForHotDice() {
        return baseScoreForHotDice;
    }

    public void setBaseScoreForHotDice(int score) {
        this.baseScoreForHotDice = score;
    }

    public int getWinningScore() {
        return WINNING_SCORE;
    }

    public boolean[] getHeldDice() {
        return heldDice;
    }
}