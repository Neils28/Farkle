import java.util.Scanner;

public class FarkleDriver {

    int playersTurn = 0;
    int[] playerScores = new int[2];
    int[] playerScoresThisTurn = new int[2];
    int[] dice = new int[5];
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        FarkleDriver game = new FarkleDriver();
        System.out.println("Welcome to Farkle!");
        System.out.println("Player 1 and Player 2 will take turns rolling dice.");
        System.out.println("The first player to reach 100 points wins the game.");
        System.out.println("Each player rolls 5 dice and scores points based on the rolled values.");
        System.out.println("If a player rolls no scoring dice, they lose their turn and score 0 points.");
        System.out.println(
                "If a player rolls scoring dice, they can choose to keep rolling or end their turn and score points.");
        System.out.println("Let's start the game!");
        game.playGame();

    }

    public void playGame() {
        while (true) {
            printState();
            System.out.println("Player " + (playersTurn + 1) + "'s turn. Press Enter to roll the dice.");
            scanner.nextLine(); // Wait for user input to roll the dice
            rollDice();
            System.out.println("Enter the dice you want to keep (comma-separated) or 'r' to roll again:");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("r")) {
                // Roll again
                continue;
            } else {
                String[] diceToKeep = input.split(",");
                for (String die : diceToKeep) {
                    int dieValue = Integer.parseInt(die.trim());
                    if (dieValue < 1 || dieValue > 6) {
                        System.out.println("Invalid die value. Please enter values between 1 and 6.");
                        continue;
                    }
                    // Keep the die value (implement logic to keep the die)
                }
            }
            System.out.println("Do you want to end your turn? (y/n)");
            String endTurnInput = scanner.nextLine();
            if (endTurnInput.equalsIgnoreCase("y")) {
                changeTurn(); // Change turn to the next player
                break;
            }

        }

    }

    public void rollDice() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = (int) (Math.random() * 6) + 1; // Roll a die (1-6)
        }
        System.out.println("Rolled dice: " + java.util.Arrays.toString(dice));

        // Implement logic to score the rolled dice
    }

    public void scoreDice() {
        // Implement scoring logic based on the rolled dice
        // Update playerScoresThisTurn based on the scoring
    }

    public void changeTurn() {
        playersTurn = (playersTurn + 1) % 2; // Switch turns
    }

    public void printState() {
        System.out.println("Player 1 Score: " + playerScores[0]);
        System.out.println("Player 2 Score: " + playerScores[1]);
    }

}
