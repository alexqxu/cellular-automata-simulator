package cellsociety;

import javafx.application.Application;
import javafx.stage.Stage;

public class SimulationRunner extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    SimulationApp Application = new SimulationApp(primaryStage);
  }

  public static void main(String[] args){
    launch(args);
  }
}
