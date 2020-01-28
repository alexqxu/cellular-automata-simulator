package cellsociety;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author: Alex Oesterling, axo
 */
public class Main extends Application {
    private Stage myStage;

    @Override
    public void start(Stage stage) throws Exception {
        myStage = stage;
        
    }

    /**
     * Runner method, actually runs the game when a user presses
     * play in the IDE
     * @param args
     */
    public static void main (String[] args)  {
        launch(args);
    }
}
