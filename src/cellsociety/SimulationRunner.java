package cellsociety;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author: Alex Oesterling, axo
 *
 * Purpose: Starts up simulation, creates a new instance of the SimulationApp class in a new stage.
 * This class begins the entire process of the application.
 *
 * Assumptions:
 */
public class SimulationRunner extends Application {

  @Override
  public void start(Stage primaryStage) {
    SimulationApp Application = new SimulationApp(primaryStage);
  }

  public static void main(String[] args){
    launch(args);
  }
}
