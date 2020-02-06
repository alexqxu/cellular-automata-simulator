package cellsociety;

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
  private Config myConfig;
  private Visualizer myVisualizer;

  /**
   * Start method. Runs game loop after setting up stage and scene data.
   *
   * @param stage the window in which the application runs
   * @throws Exception
   */
  //FIXME throws
  @Override
  public void start(Stage stage)
      throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException { //throws exception?
    myStage = stage;

    myConfig = new Config(chooseFile());
    myVisualizer = myConfig.createVisualizer();
    myVisualizer.setStage(myStage);
    //FIXME REFACTOR WITH NEW WINDOW
    //Visualizer myVisualizer = new RectVisualizer(myConfig);

    myStage.setScene(myVisualizer.createScene());
    stage.setTitle(TITLE);
    stage.show();

    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
        e -> myVisualizer.update(SECOND_DELAY));
    Timeline animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  /**
   * Loads an .xml file by passing it to the Config class which creates the model backend for the
   * simulation. Then updates the cell matrix to the new status of the loaded file.
   *
   * @param file
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static void loadConfigFile(File file)
      throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Config config = new Config(file);
    Visualizer myVisualizer = config.createVisualizer();
    Stage newStage = new Stage();
    newStage.setScene(myVisualizer.createScene());

    newStage.show();

    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
        e -> myVisualizer.update(SECOND_DELAY));
    Timeline animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  /**
   * Opens a file navigator dialogue and allows the user to select an .xml file for importing into
   * the simulation
   *
   * @return the File object representing the .xml file to be used by the simulation
   */
  public static File chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Simulation File");
    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      return file;
    } else {
      System.out.println("Error: File not found");
    }
    return null;
  }
  //FIXME throws
  /*
    public static void newWindow()
        throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      Stage newStage = new Stage();
      Config newConfig = new Config(chooseFile());
      newStage.setTitle(TITLE);
      //Visualizer newVisualizer = new HexVisualizer(newConfig);

      newStage.setScene(newVisualizer.createScene());
      newStage.show();
      KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->newVisualizer.update(SECOND_DELAY));
      Timeline animation = new Timeline();
      animation.setCycleCount(Timeline.INDEFINITE);
      animation.getKeyFrames().add(frame);
      animation.play();
    }

   */

  /**
   * Runner method, actually runs the game when a user presses play in the IDE
   *
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
