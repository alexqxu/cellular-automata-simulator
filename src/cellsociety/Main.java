package cellsociety;

import cellsociety.visualizer.HexVisualizer;
import cellsociety.visualizer.RectVisualizer;
import cellsociety.visualizer.TriVisualizer;
import cellsociety.visualizer.Visualizer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author: Alex Oesterling, axo
 */
public class Main extends Application {

  public static final String TITLE = "Cell Simulator";
  public static final int FRAMES_PER_SECOND = 60; //FIXME Maverick changed to 60 from 120 for testing
  public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
  public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

  private Stage myStage;
    /**
     * Start method. Runs game loop after setting up stage and scene data.
     * @param stage the window in which the application runs
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException { //throws exception?
        myStage = stage;

        Config myConfig = new Config(chooseFile());
        //Visualizer myVisualizer = myConfig.getVisualizer();
        Visualizer myVisualizer = new RectVisualizer(myConfig);

        myStage.setScene(myVisualizer.createScene());
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->myVisualizer.update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

  /**
   * Opens a file navigator dialogue and allows the user to select an .xml file for importing into the simulation
   * @return the File object representing the .xml file to be used by the simulation
   */
    public static File chooseFile(){
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Choose Simulation File");
      fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
      fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
      File file = fileChooser.showOpenDialog(null);
      if(file!=null){
        return file;
      }else{
        System.out.println("Error: File not found");
      }
      return null;
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
