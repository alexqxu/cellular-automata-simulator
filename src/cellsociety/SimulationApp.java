package cellsociety;

import cellsociety.config.Config;
import cellsociety.config.XMLWriter;
import cellsociety.exceptions.InvalidCellException;
import cellsociety.exceptions.InvalidFileException;
import cellsociety.exceptions.InvalidGridException;
import cellsociety.exceptions.InvalidXMLStructureException;
import cellsociety.exceptions.XMLWriteException;
import cellsociety.simulation.grid.Grid;
import cellsociety.visualizer.Visualizer;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

/**
 * @author: Alex Oesterling, axo
 * Purpose: is the actual application which holds the entire simulation: creates the stage, scene, all UI elements, and
 * passes information to the Config portion of the project for loading and saving of XML files.
 * Assumptions: No failure cases, but simplified the design constraints by assuming that there is not a global play/pause button for all applications
 *
 * Dependencies: cellsociety.config.Config and cellsociety.config.XMLWriter in the config package
 * as well as cellsociety.simulation.grid.Grid in the simulation package and cellsociety.visualizer.Visualizer in the v
 * visualizer package. In addition this is dependent on the JavaFX library.
 *
 * Example usage: In the SimulationRunner, an initial SimulationApp is created upon starting.
 * A new instance of the App is created in a new window every time the "new" menu option is selected, using the syntax:
 * SimulationApp newApp = new SimulationApp(new Stage);
 *
 * SimulationApp object with a new stage in the constructor will create a new instance of the simulation
 * in a new window.
 */
public class SimulationApp {

  private static final String TITLE = "Cell Simulator";
  private static final int FRAMES_PER_SECOND = 60;
  private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
  private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
  private static final String RESOURCES = "resources";
  private static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
  public static final String DEFAULT_RESOURCE_FOLDER = RESOURCES + "/";
  private static final String RESOURCE_PACKAGE = "Image";
  private static final String STYLESHEET = "default.css";
  private static final String IMAGEFILE_SUFFIXES = String
      .format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
  private static final int MAX_UPDATE_PERIOD = 2;
  private static final String ERROR_DIALOG = "Please Choose Another File";
  private static final String PACKAGE_PREFIX_NAME = "cellsociety.visualizer.";
  private static final String XML_FILEPATH = "user.dir";
  public static final String INVALID_CELL = "Invalid Cell/Simulation Type Specified";
  public static final String INVALID_SHAPE = "Invalid Shape Specified";
  public static final String INVALID_FILE = "Invalid File Specified";
  private BorderPane frame;
  private Stage myStage;
  private Config myConfig;
  private Visualizer myVisualizer;
  private Slider slider;
  private Button playpause;
  private Button gridLines;
  private Button shuffle;
  private ResourceBundle myResources;
  private MenuItem newWindow;
  private MenuItem exit;
  private MenuItem save;
  private MenuItem loadFile;
  private Button reset;
  private Button step;
  private MenuBar menuBar;
  private Menu menu;
  private File myFile;
  private double secondsElapsed;
  private double speed;
  private boolean running;

  /**
   * Constructor method. Runs simulation loop after setting up stage and scene data by creating
   * Config and Visualizer objects.
   *
   * Assumptions: There is a resource bundle which can be used to format the App.
   * Also, I assume that each creation of the App passes in a new stage. I have not tested
   * what happens if an existing stage object is used to create a new App.
   *
   * @param stage the window in which the application runs - to make a new copy of the app pass in a new stage (will create a new window)
   */
  public SimulationApp(Stage stage) {
    myStage = stage;
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + RESOURCE_PACKAGE);

    loadConfigFile(chooseFile());
    if(myConfig == null){
      System.exit(0);
    }

    myStage.setScene(createScene());
    myStage.setTitle(TITLE);
    myStage.show();

    KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> update(SECOND_DELAY));
    Timeline animation = new Timeline();
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.getKeyFrames().add(frame);
    animation.play();
  }

  /**
   * Creates the scene to be rendered in the stage. Calls the creation of UI controls as well as
   * graphics from the visualizer class. Applies CSS to the UI as well.
   *
   * @return the scene to be rendered by the application
   */
  private Scene createScene() {
    frame = new BorderPane();
    frame.setTop(setToolBar());
    frame.setCenter(myVisualizer.bundledUI());
    running = false;
    setSpeed(myConfig.getSpeed());
    Scene scene = new Scene(frame, Color.AZURE);
    scene.getStylesheets()
        .add(getClass().getClassLoader().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET)
            .toExternalForm());
    return scene;
  }

  /**
   * Update method which calls the model to update the states of all the cells on the backend, and
   * redraws the shapes with their new color values. Calls the inner update methods at a rate
   * dependent on the speed of the simulation (specified in the .xml config file and controlled by
   * the slider).
   *
   * @param elapsedTime - The elapsed time between frame calls made in the simulation loop of
   *                    the App
   */
  private void update(double elapsedTime) {
    if (running) {
      secondsElapsed += elapsedTime;
      if (secondsElapsed > speed) {
        myVisualizer.stepGrid();
        myVisualizer.updateChart();
        secondsElapsed = 0;
      }
    }
  }

  /**
   * Opens a file navigator dialogue and allows the user to select an .xml file for importing into
   * the simulation
   *
   * @return the File object representing the .xml file to be used by the simulation
   */
  private File chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Simulation File");
    fileChooser.setInitialDirectory(new File(System.getProperty(XML_FILEPATH)));
    fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    File file = fileChooser.showOpenDialog(myStage);
    if (file != null) {
      myFile = file;
      return file;
    } else {
      return null;
    }
  }

  /**
   * Loads a config file specified by the file selector in the chooseFile() method. Creates a
   * Config object and handles errors in selecting an invalid XML file.
   * @param file - the XML file to be loaded into the Config object
   */
  private void loadConfigFile(File file) {
    if (file == null) {
      return;
    } else {
      try {
        myConfig = new Config(file);
      } catch (InvalidCellException e) {
        retryLoadFile(INVALID_CELL);
      } catch (InvalidGridException e) {
        retryLoadFile(INVALID_SHAPE);
      } catch (InvalidFileException e) {
        retryLoadFile(INVALID_FILE);
      } catch (InvalidXMLStructureException e){
        retryLoadFile(e.getMessage());
      } catch (Exception e){
        retryLoadFile("Bad File");
      }

      Class visualizerClass = null;
      try {
        visualizerClass = Class.forName(PACKAGE_PREFIX_NAME + myConfig.getVisualizer());
        myVisualizer = (Visualizer) (visualizerClass.getConstructor(Grid.class)
            .newInstance(myConfig.getGrid()));
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        retryLoadFile(INVALID_SHAPE);
      } catch (NullPointerException e){
        return;
      }
      myVisualizer.setColorMap(myConfig.getStates());
    }
  }

  /**
   * Helps with error handling by creating a loop which will pop up an error message until the user has
   * selected a valid XML file
   * @param message - The string message to be displayed by the error popup
   */
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
      } catch (InvalidGridException e) {
        displayError(message);
        badFile = true;
      } catch (InvalidFileException e) {
        displayError(message);
        badFile = true;
      } catch (NullPointerException e){
        return;
      } catch (Exception e){
        displayError(message);
        badFile = true;
      }
    } while (badFile);
  }

  /**
   * Creates the error dialog box which pops up on handling different forms of exceptions
   * for an invalid XML file
   * @param message - The message to be displayed by the error dialog
   */
  private void displayError(String message) {
    Alert errorAlert = new Alert(AlertType.ERROR);
    errorAlert.setHeaderText(message);
    errorAlert.setContentText(ERROR_DIALOG);
    errorAlert.showAndWait();
  }

  /**
   * Creates the UI toolbar with all user-interactable menus, buttons, and sliders. Creates their eventhandlers
   * and bundles them into one Pane to be rendered in the scene
   * @return - an HBox pane with all of the UI tools inside of it.
   */
  private Node setToolBar() {
    HBox toolbar = new HBox();
    menuBar = new MenuBar();
    menu = makeMenu("Menu");
    newWindow = makeMenuItem("New", e -> makeWindow());
    loadFile = makeMenuItem("Load", e -> {
      loadConfigFile(chooseFile());
      frame.setCenter(myVisualizer.bundledUI());
      myVisualizer.drawGrid();
    });
    exit = makeMenuItem("Exit", e-> closeWindow());
    save = makeMenuItem("Save", e->{
      XMLWriter myWriter = new XMLWriter(myConfig, myVisualizer.getGrid());
      String filepath = saveFile();
      if(filepath != null){
        try {
          myWriter.saveXML(filepath);
        } catch (XMLWriteException x) {
          displayError("Writing File caused exceptional error. Please check disk space, working libraries, and status of loaded XML file.");
        }
      }
    });
    playpause = makeButton("Play", e -> handlePlayPause(playpause));
    reset = makeButton("Reset", e -> {
      loadConfigFile(myFile);
      frame.setCenter(myVisualizer.bundledUI());
      myVisualizer.drawGrid();
    });
    step = makeButton("Step", e -> {
      myVisualizer.stepGrid();
      myVisualizer.updateChart();
    });
    shuffle = makeButton("Shuffle", e->{
      myConfig.createRandomGrid(myVisualizer.getWidth(), myVisualizer.getHeight());
      myVisualizer.setGrid(myConfig.getGrid());
      myVisualizer.drawGrid();
    });
    gridLines = makeButton("GridLines", e->{
      myVisualizer.setGridLines(!myVisualizer.getGridLines());
      myVisualizer.reDrawGrid();
    });
    slider = new Slider();
    slider.setMin(0);
    slider.setMax(100);
    slider.setMajorTickUnit(50);
    slider.setMinorTickCount(5);
    slider.setBlockIncrement(10);
    slider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
        setSpeed(new_val.doubleValue() / 100);
      }
    });
    menuBar.getMenus().add(menu);
    menu.getItems().addAll(newWindow, loadFile, save, exit);
    toolbar.getChildren().add(menuBar);
    toolbar.getChildren().add(shuffle);
    toolbar.getChildren().add(playpause);
    toolbar.getChildren().add(step);
    toolbar.getChildren().add(reset);
    toolbar.getChildren().add(gridLines);
    toolbar.getChildren().add(slider);
    return toolbar;
  }

  /**
   * Creates filechooser dialog where the user can select the filepath for the current configuration of the simulation
   * to be saved to.
   * @return the filepath where the current simulation will be saved to.
   */
  private String saveFile() {
    FileChooser fileSaver = new FileChooser();
    fileSaver.setTitle("Save Simulation Configuration");
    fileSaver.setInitialDirectory(new File(System.getProperty(XML_FILEPATH)));
    fileSaver.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    File file = fileSaver.showSaveDialog(myStage);
    if (file != null) {
      return file.getPath();
    } else {
      return null;
    }
  }

  //FIXME Write about how I have duplicated code but Buttons and Menus inherit setGraphic() and setText() from different parents so cannot extract one unitary method

  /**
   * Creates a button and handles the rendering of images from the .properties file
   * @param property - The string which corresponds to a string or image in the .properties file
   * @param handler - The eventhandler which will trigger when the button is pressed
   * @return a button object with a label from the .properties file and an event on action
   */
  private Button makeButton(String property, EventHandler<ActionEvent> handler) {
    Button result = new Button();
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(
          new ImageView(new Image(
              getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    } else {
      result.setText(label);
    }
    result.setOnAction(handler);
    return result;
  }

  /**
   * Handles the toggling of the play/pause button. Switches between playing the animation and stopping it.
   * @param button - the button which gets its image swapped between play and pause when clicked
   */
  private void handlePlayPause(Button button) {
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
          new ImageView(new Image(
              getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    }
  }

  /**
   * Takes in a double representing a percent value. This reflects a percent of the max speed.
   * @param percentSpeed the percent of the max speed to which to set the simulation
   */
  private void setSpeed(double percentSpeed) {
    percentSpeed *= MAX_UPDATE_PERIOD;
    speed = MAX_UPDATE_PERIOD - percentSpeed;
  }

  /**
   * Closes the current window in which the exit button was pressed
   */
  private void closeWindow() {
    myStage.close();
  }

  /**
   * Creates a new window with a copy of the application running in it
   */
  private void makeWindow() {
    SimulationApp newWindow = new SimulationApp(new Stage());
  }

  /**
   * Handles the creation of Menu and MenuItem labels
   * @param property - the label to be applied to the items, from the .properties file
   * @param result - the menu item to which the label is applied
   */
  private void createLabel(String property, MenuItem result) {
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(
          new ImageView(new Image(
              getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_FOLDER + label))));
    } else {
      result.setText(label);
    }
  }

  /**
   * Handles the creation of MenuItem objects, applies a label to them as specified in the createLabel method and
   * gives them an action on click.
   * @param property - the label to be applied to the object specified in the .properties file
   * @param handler - the action to occur when the menu item is clicked
   * @return a MenuItem to be placed in a Menu object with a label and action on click
   */
  private MenuItem makeMenuItem(String property, EventHandler<ActionEvent> handler) {
    MenuItem result = new MenuItem();
    createLabel(property, result);
    result.setOnAction(handler);
    return result;
  }

  /**
   * Handles the creation of Menu objects, applies a label to them as specified in the createLabel method
   * @param property - the label to be applied to the menu, from the .properties file
   * @return a Menu to be rendered on stage with a label
   */
  private Menu makeMenu(String property) {
    Menu result = new Menu();
    createLabel(property, result);
    return result;
  }
}
