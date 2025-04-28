/**
 * FarkleDriver handles the console-based interaction for the two-player game of
 * Farkle.
 */
public class FarkleDriver {

    public static void main(String[] args) {
        FarkleModel game = new FarkleModel();

        // Print welcome message and game instructions
        System.out.println("\n==================");
        System.out.println("Welcome to Farkle!");
        System.out.println("==================\n");
        System.out.println("Player 1 and Player 2 will take turns rolling dice.");
        System.out.println("The first player to reach 100 points wins the game.");
        System.out.println("Each player rolls 6 dice and scores points based on the rolled values.");
        System.out.println("If a player rolls no scoring dice, they lose their turn and score 0 points.");
        System.out.println(
                "If a player rolls scoring dice, they can choose to keep rolling or end their turn and score points.");
        System.out.println("Let's start the game!");

        // Main game loop
        while (!game.isGameOver()) {
            game.startTurn();
        }
        System.out.println("Player " + (game.getWinner() + 1) + " wins the game with "
                + game.getPlayerScore(game.getWinner()) + " points!");
    }
}
