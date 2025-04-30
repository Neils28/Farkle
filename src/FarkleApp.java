import farkle.FarkleController;
import farkle.FarkleModel;
import farkle.FarkleView;

public class FarkleApp {
    public static void main(String[] args) {
        FarkleModel model = new FarkleModel();
        FarkleView view = new FarkleView();
        FarkleController controller = new FarkleController(model, view);

        view.setVisible(true);
    }
}
