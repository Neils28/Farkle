import farkle.FarkleController;
import farkle.FarkleModel;
import farkle.FarkleView;

public class FarkleApp {
    public static void main(String[] args) {
        FarkleModel model = new FarkleModel();
        FarkleView view = new FarkleView();
        FarkleController controller = new FarkleController(model, view);

        javax.swing.JOptionPane.showMessageDialog(
                null,
                "🎲 Welcome to Farkle!\n\n" +
                        "OBJECT:\n" +
                        "- Be the first player to reach 10,000 points.\n\n" +
                        "HOW TO PLAY:\n" +
                        "- Roll 6 dice.\n" +
                        "- Score points from 1s, 5s, and combos (like three of a kind, straight, etc.).\n" +
                        "- Select which dice to keep.\n" +
                        "- You may roll the remaining dice again — but risk losing points if you Farkle!\n" +
                        "- You must score at least 500 points in one turn before you can bank for the first time.\n\n" +
                        "SCORING:\n" +
                        "- 1s = 100 points each.\n" +
                        "- 5s = 50 points each.\n" +
                        "- Three of a kind = 100 times the number (e.g., three 2s = 200 points).\n" +
                        "- Four of a kind = 1000.\n" +
                        "- Five of a kind = 2000.\n" +
                        "- Six of a kind = 3000.\n" +
                        "- 1-6 straight = 1500.\n" +
                        "- Three pairs = 1500.\n" +
                        "- Two triplets = 2500.\n" +
                        "- Four of a kind + pair = 1500.\n" +

                        "\nTIPS:\n" +
                        "- Farkle = no scoring dice in a roll → you lose all turn points.\n" +
                        "- Use the radio buttons to keep dice.\n" +
                        "- Click \"Bank Points\" to save your score and end your turn.\n\n" +
                        "Good luck!",
                "How to Play Farkle",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

        view.setVisible(true);
    }

}
