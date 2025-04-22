import java.util.Scanner;

/**
 * FarkleDriver handles the console-based interaction for the two-player game of Farkle.
 */
public class FarkleDriver {

    public static void main(String[] args) {
        FarkleModel game = new FarkleModel();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Farkle!");
        System.out.println("Player 1 and Player 2 will take turns rolling dice.");
        System.out.println("The first player to reach 100 points wins the game.");
        System.out.println("Each player rolls 5 dice and scores points based on the rolled values.");
        System.out.println("If a player rolls no scoring dice, they lose their turn and score 0 points.");
        System.out.println("If a player rolls scoring dice, they can choose to keep rolling or end their turn and score points.");
        System.out.println("Let's start the game!");

        while (!game.isGameOver()) {
            game.printState();
            System.out.println("Player " + (game.getCurrentPlayer() + 1) + "'s turn. Press Enter to roll the dice.");
            scanner.nextLine();

            game.rollDice();
            System.out.println("Enter the dice you want to keep (comma-separated values) or 'r' to roll again:");
            String input = scanner.nextLine();

            if (!input.equalsIgnoreCase("r")) {
                game.keepDice(input);
            }

            System.out.println("Do you want to end your turn? (y/n)");
            String endTurn = scanner.nextLine();
            if (endTurn.equalsIgnoreCase("y")) {
                game.endTurn();
            }
        }

        System.out.println("Player " + (game.getWinner() + 1) + " wins the game with " + game.getPlayerScore(game.getWinner()) + " points!");
        scanner.close();
    }
}
