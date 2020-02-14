package cellsociety;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author: Alex Oesterling, axo
 *
 * Purpose: Starts up simulation, creates a new instance of the SimulationApp class in a new stage.
 * This class begins the entire process of the application.
 *
 * Assumptions: this class assumes nothing except that the SimulationApp takes in a stage as its constructor
 *
 * Dependencies: this class is dependent on JavaFX as well as the SimulationApp class
 *
 * Example usage: Simply press the "Run" button in whatever IDE you are using: This class contains the
 * main() method and so is only used when called by the run button.
 */
public class SimulationRunner extends Application {

  /**
   * Start method inherited from the Application class. Called when the main method runs and launches
   * the application. No assumptions other than that SimulationApp is an existing class which takes in a stage.
   *
   * @param primaryStage: The stage in which the first instance of a SimulationApp will be rendered.
   */
  @Override
  public void start(Stage primaryStage) {
    SimulationApp Application = new SimulationApp(primaryStage);
  }

  /**
   * The main method of this program. Begins the Simulation by calling the start() method
   * @param args: the arguments to the main method (unused in this case, syntax for main method)
   */
  public static void main(String[] args){
    launch(args);
  }
}
