package farkle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FarkleController {

    private FarkleModel model;
    private FarkleView view;

    public FarkleController(FarkleModel model, FarkleView view) {
        this.model = model;
        this.view = view;

        // Add action listeners to the view components
        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.rollDice();
                view.updateDiceDisplay(model.getDice());
                view.updateScoreDisplay(model.getCurrentScore());
            }
        });
    }

}
