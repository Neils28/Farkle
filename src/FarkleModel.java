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
    private int currrentScore = 0;
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
        System.out.println("\nRolled dice: " + Arrays.toString(dice));
    }

    /**
     * Checks if the current roll is a Farkle (no scoring dice).
     * 
     * @return true if it's a Farkle, false otherwise
     */
    private boolean isFarkle() {
        boolean hasScoringDice = false;
        for (int die : dice) {
            if (die == 1 || die == 5) {
                hasScoringDice = true;
                break;
            }
        }
        return !hasScoringDice;
    }

    /**
     * 
     * Stores selected dice and calculates score.
     * 
     * @param input Comma-separated dice values
     */
    public void scoreDice(String input) {
        String[] keptDice = input.split(",");
        currrentScore = 0; // Reset current score for this turn

        // if player has 0 points, they must have more than 500 points to score

        for (String die : keptDice) {
            try {
                int dieIndex = Integer.parseInt(die.trim());
                if (dice[dieIndex - 1] == 1) {
                    currrentScore += 100; // 1s are worth 100 points
                } else if (dice[dieIndex - 1] == 5) {
                    currrentScore += 50; // 5s are worth 50 points
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + die);
            }
        }
        System.out.println("\nCurrent score this turn: " + currrentScore);
    }

    public void startTurn() {
        rollsRemaining = 2; // Reset rolls for the new turn
        currrentScore = 0; // Reset current score for the new turn
        printState();
        System.out.println("Player " + (currentPlayer + 1) + "'s turn.");
        System.out.println("\nPress Enter to roll the dice.");
        scanner.nextLine(); // Wait for user input
        rollDice();

        // Check for farkle
        if (isFarkle()) {
            System.out.println("\nFarkle! You lose all your points and your turn!");
            endTurn();
        } else {

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
                        bankPoints();
                        endTurn();
                        return; // Exit the turn
                    } else {
                        System.out.println("\nRerolling dice...");
                        reRollDice(keptDice); // Only reroll if they kept dice
                        rollsRemaining--;
                    }
                }
            }
            System.out.println("That was your last turn!");
            scoreDice("1,2,3,4,5,6"); // Score all dice
            bankPoints();
            endTurn();
        }

    }

    /**
     * Banks the current score and adds it to the player's total score.
     */
    public void bankPoints() {
        if (((playerScores[0] == 0) && (currrentScore < 500)) || ((playerScores[1] == 0) && (currrentScore < 500))) {
            System.out.println(
                    "\nIn order to bank your points for the first time, you must have a running total of 500 points before you stop rolling.");
        } else {
            playerScores[currentPlayer] += currrentScore;
            System.out.println("\nYou banked " + currrentScore + " points!");
            currrentScore = 0; // Reset current score after banking
        }
    }

    /**
     * Ends the current player's turn.
     */
    public void endTurn() {
        currentPlayer = (currentPlayer + 1) % 2;
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
