package cellsociety;

import cellsociety.config.Config;
import cellsociety.exceptions.InvalidCellException;
import cellsociety.exceptions.InvalidGridException;
import cellsociety.visualizer.TriVisualizer;
import cellsociety.visualizer.Visualizer;
import cellsociety.exceptions.InvalidFileException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
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
  private static final String RESOURCES = "resources";
  public static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
  public static final String DEFAULT_RESOURCE_FOLDER = RESOURCES + "/";
  private static final String RESOURCE_PACKAGE = "Image";
  private static final String STYLESHEET = "default.css";
  private static final int MAX_UPDATE_PERIOD = 2;


  private String packagePrefixName = "cellsociety.visualizer.";

  private BorderPane frame;
  private Stage myStage;
  private Config myConfig;
  private Visualizer myVisualizer;
  private Slider slider;
  private Button playpause;
  private Button loadFile;
  private ResourceBundle myResources;
  private Menu newWindow;
  private Menu exit;
  private Button reset;
  private Button step;
  private MenuBar menuBar;
  private File myFile;
  private double secondsElapsed;
  private double speed;
  private boolean running;

  /**
   * Start method. Runs game loop after setting up stage and scene data.
   * @param stage the window in which the application runs
   * @throws Exception
   */
  @Override
  public void start(Stage stage){ //throws exception?
    myStage = stage;
    System.out.println(DEFAULT_RESOURCE_PACKAGE+RESOURCE_PACKAGE);
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + RESOURCE_PACKAGE);

    loadConfigFile(chooseFile());

    myStage.setScene(createScene());
    myStage.setTitle(TITLE);
    myStage.show();

    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->update(SECOND_DELAY));
    Timeline animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  /**
   * Creates the scene to be rendered in the stage. Calls the creation of UI controls as well as graphics from the visualizer class.
   * Applies CSS to the UI as well.
   * @return the scene to be rendered by the application
   */
  private Scene createScene() {
    frame = new BorderPane();
    frame.setTop(setToolBar());
    frame.setBottom(myVisualizer.instantiateCellGrid());
    running = false;
    setSpeed(myConfig.getSpeed());
    Scene scene = new Scene(frame, Color.AZURE);
    scene.getStylesheets()
        .add(getClass().getClassLoader().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET).toExternalForm());
    return scene;
  }

  /**
   * Handles
   * @param button
   */
  public void handlePlayPause (Button button){
    running = !running;
    final String IMAGEFILE_SUFFIXES = String
        .format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    String label = "";
    if (running) {
      label = myResources.getString("Pause");
    } else {
      label = myResources.getString("Play");
    }
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      button.setGraphic(
          new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    }
  }

  /**
   * Takes in a double representing a percent value. This reflects a percent of the max speed.
   *
   * @param percentSpeed the percent of the max speed to which to set the simulation
   */
  public void setSpeed ( double percentSpeed){
    percentSpeed *= MAX_UPDATE_PERIOD;
    speed = MAX_UPDATE_PERIOD - percentSpeed;
  }

  /**
   * Update method which calls the model to update the states of all the cells on the backend, and
   * redraws the rectangles with their new color values. Calls the inner update methods at a rate
   * dependent on the speed of the simulation (specified in the .xml config file and controlled by
   * the slider).
   *
   * @param elapsedTime - The elapsed time between frame calls made in the default start() method of
   *                    the Application
   */
  public void update ( double elapsedTime){
    secondsElapsed += elapsedTime;
    if (running && secondsElapsed > speed) {
      secondsElapsed = 0;
      myVisualizer.stepGrid();
    }
  }

  /**
   * Opens a file navigator dialogue and allows the user to select an .xml file for importing into
   * the simulation
   *
   * @return the File object representing the .xml file to be used by the simulation
   */
  //FIXME null
  public File chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Simulation File");
    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      myFile = file;
      return file;
    } else {
      return null;
    }
  }
  public Node setToolBar() {
    HBox toolbar = new HBox();
    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    menuBar = new MenuBar();
    newWindow = makeMenu("New", e -> makeWindow());
    loadFile = makeButton("Load", e -> {
      loadConfigFile(chooseFile());
      frame.setBottom(myVisualizer.instantiateCellGrid());
      myVisualizer.drawGrid();
    });
    //exit = makeMenu("Exit", e-> System.exit(0)); FIXME
    playpause = makeButton("Play", e -> handlePlayPause(playpause));
    reset = makeButton("Reset", e -> {
      loadConfigFile(myFile);
      frame.setBottom(myVisualizer.instantiateCellGrid());
      myVisualizer.drawGrid();
    });
    step = makeButton("Step", e -> {
      myVisualizer.stepGrid();
    });

    slider = new Slider();
    slider.setMin(0);
    slider.setMax(100);
    slider.setValue(50);
    slider.setMajorTickUnit(50);
    slider.setMinorTickCount(5);
    slider.setBlockIncrement(10);
    slider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
        setSpeed(new_val.doubleValue() / 100);
      }
    });
    //menuBar.getMenus().addAll(loadFile, newWindow);
    toolbar.getChildren().add(playpause);
    toolbar.getChildren().add(step);
    toolbar.getChildren().add(reset);
    toolbar.getChildren().add(loadFile);
    toolbar.getChildren().add(spacer);
    toolbar.getChildren().add(slider);
    return toolbar;
  }
  //fixme make
  private void makeWindow() {
    return;
  }

  public void loadConfigFile(File file) {
    if(file == null){
      return;
    }
    try {
      myConfig = new Config(file);
    } catch (InvalidCellException e) {
      retryLoadFile("Invalid Simulation Specified");
    } catch (InvalidGridException e){
      retryLoadFile("Invalid Shape Specified");
    } catch (InvalidFileException e){
      retryLoadFile("Invalid File Specified");
    }

    /*
    myVisualizer = new TriVisualizer(myConfig.getGrid()); //FIXME
    myVisualizer.setColorMap(myConfig.getStates());
     */

    Class visualizerClass = null;
    try {
      visualizerClass = Class.forName(packagePrefixName + myConfig.getVisualizer());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    try {
      Visualizer myVisualizer = (Visualizer) (visualizerClass.getConstructor().newInstance(myConfig.getGrid()));
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    //FIXME uncomment once config.getVisualizer() is working, construct with grid param
  }

  private void retryLoadFile(String message) {
    boolean badFile;
    displayError(message);
    do {
      badFile = false;
      try {
        myConfig = new Config(chooseFile());
      } catch (InvalidCellException e) {
        displayError(message);
        badFile = true;
      } catch (InvalidGridException e){
        displayError(message);
        badFile = true;
      } catch (InvalidFileException e){
        displayError(message);
        badFile = true;
      }
    } while (badFile);
  }

  private void displayError(String message) {
    Alert errorAlert = new Alert(AlertType.ERROR);
    errorAlert.setHeaderText(message);
    errorAlert.setContentText("Please Choose Another File");
    errorAlert.showAndWait();
  }

  private Button makeButton(String property, EventHandler<ActionEvent> handler) {
    final String IMAGEFILE_SUFFIXES = String
        .format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    Button result = new Button();
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(
          new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    } else {
      result.setText(label);
    }
    result.setOnAction(handler);
    return result;
  }

  //FIXME
  private Menu makeMenu (String property, EventHandler < ActionEvent > handler){
    final String IMAGEFILE_SUFFIXES = String
        .format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    Menu result = new Menu();
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(
          new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    } else {
      result.setText(label);
    }
    result.setOnAction(handler);
    return result;
  }

  /**
   * Runner method, actually runs the game when a user presses play in the IDE
   *
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
