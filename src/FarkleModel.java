import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

/**
 * FarkleModel encapsulates the logic and state of the Farkle game.
 */
public class FarkleModel {

    // Constants for the game
    private static final int NUM_DICE = 6;
    private static final int WINNING_SCORE = 10000;

    private int[] playerScores = new int[2];
    private int[] dice = new int[NUM_DICE];
    private int currentPlayer = 0;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);
    private int rollsRemaining = 2;

    /**
     * Rolls all 6 dice randomly.
     */
    public void rollDice() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = random.nextInt(6) + 1;
        }
        System.out.println("Rolled dice: " + Arrays.toString(dice));
        // Add scoring logic here if you want to auto-calculate
    }

    /**
     * Stores selected dice and calculates score.
     * 
     * @param input Comma-separated dice values
     */
    public void scoreDice(String input) {
        String[] keptDice = input.split(",");
        int scoreThisTurn = 0;

        for (String die : keptDice) {
            try {
                int dieIndex = Integer.parseInt(die.trim());
                if (dice[dieIndex - 1] == 1) {
                    scoreThisTurn += 100; // 1s are worth 100 points
                } else if (dice[dieIndex - 1] == 5) {
                    scoreThisTurn += 50; // 5s are worth 50 points
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + die);
            }
        }

        System.out.println("\nYou scored " + scoreThisTurn + " points!");
        playerScores[currentPlayer] += scoreThisTurn;
    }

    public void startTurn() {
        printState();
        System.out.println("Player " + (currentPlayer + 1) + "'s turn.");
        System.out.println("\nPress Enter to roll the dice.");
        scanner.nextLine(); // Wait for user input
        rollDice();

        while (rollsRemaining > 0) {

            System.out.println("Enter the dice you want to keep (comma-separated values) or 'r' to roll again:");
            String keptDice = scanner.nextLine();

            if (keptDice.equalsIgnoreCase("r")) {
                rollDice(); // Roll all dice
                rollsRemaining--;
                System.out.println("\nYou have " + (rollsRemaining) + " rolls remaining.");
            } else {
                scoreDice(keptDice);
                System.out.println("\nYou have " + (rollsRemaining) + " rolls remaining.");
                System.out.println("Do you want to end your turn? (y/n)");
                String endTurn = scanner.nextLine();

                if (endTurn.equalsIgnoreCase("y")) {
                    endTurn();
                    return; // Exit the turn
                } else {
                    System.out.println("\nRerolling dice...");
                    reRollDice(keptDice); // Only reroll if they kept dice
                    rollsRemaining--;
                }
            }
        }
        System.out.println("You have no rolls remaining. Ending your turn.");
        endTurn();

    }

    /**
     * Ends the current player's turn.
     */
    public void endTurn() {
        currentPlayer = (currentPlayer + 1) % 2;
        rollsRemaining = 2; // Reset turns for the next player
    }

    /**
     * Prints the current scores of both players.
     */
    public void printState() {
        System.out.println("\nPlayer 1 Score: " + playerScores[0]);
        System.out.println("Player 2 Score: " + playerScores[1] + "\n");
    }

    /**
     * @return current player index (0 or 1)
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return true if a player has reached 100 or more points
     */
    public boolean isGameOver() {
        return playerScores[0] >= WINNING_SCORE || playerScores[1] >= WINNING_SCORE;
    }

    /**
     * @return winner's index
     */
    public int getWinner() {
        return playerScores[0] >= WINNING_SCORE ? 0 : 1;
    }

    /**
     * @param player Player index
     * @return Score of the player
     */
    public int getPlayerScore(int player) {
        return playerScores[player];
    }

    public void reRollDice(String keptDice) {
        // Logic to re-roll only the non-kept dice
        String[] tokens = keptDice.split(",");
        boolean[] kept = new boolean[dice.length];
        for (String token : tokens) {
            try {
                kept[Integer.parseInt(token.trim()) - 1] = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + token);
            }
        }

        for (int i = 0; i < dice.length; i++) {
            if (!kept[i]) {
                dice[i] = random.nextInt(6) + 1;
            }
        }
        System.out.println("Re-rolled dice: " + Arrays.toString(dice));
    }
}
