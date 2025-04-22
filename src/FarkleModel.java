import java.util.Arrays;
import java.util.Random;

/**
 * FarkleModel encapsulates the logic and state of the Farkle game.
 */
public class FarkleModel {

    private int[] playerScores = new int[2];
    private int[] dice = new int[5];
    private int currentPlayer = 0;
    private Random random = new Random();

    /**
     * Rolls all 5 dice randomly.
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
     * @param input Comma-separated dice values
     */
    public void keepDice(String input) {
        String[] tokens = input.split(",");
        int scoreThisTurn = 0;

        for (String token : tokens) {
            try {
                int value = Integer.parseInt(token.trim());
                if (value >= 1 && value <= 6) {
                    scoreThisTurn += value; // Basic scoring, just summing dice for now
                } else {
                    System.out.println("Invalid die value: " + value);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + token);
            }
        }

        System.out.println("You scored " + scoreThisTurn + " points this turn.");
        playerScores[currentPlayer] += scoreThisTurn;
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
        System.out.println("Player 1 Score: " + playerScores[0]);
        System.out.println("Player 2 Score: " + playerScores[1]);
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
        return playerScores[0] >= 100 || playerScores[1] >= 100;
    }

    /**
     * @return winner's index
     */
    public int getWinner() {
        return playerScores[0] >= 100 ? 0 : 1;
    }

    /**
     * @param player Player index
     * @return Score of the player
     */
    public int getPlayerScore(int player) {
        return playerScores[player];
    }
}
