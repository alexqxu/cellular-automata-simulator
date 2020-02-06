package cellsociety.visualizer;


import cellsociety.Config;
import cellsociety.Main;
import cellsociety.simulation.Grid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public abstract class Visualizer {
  protected static final int SIZE = 400;
  protected static final String RESOURCE_PACKAGE = "Image";
  protected static final String STYLESHEET = "default.css";
  protected static final int MAX_UPDATE_PERIOD = 2;

  protected Grid myGrid;
  protected Scene myScene;
  protected Stage myStage;
  private Config config;
  private ResourceBundle myResources;
  private BorderPane frame;
  protected ArrayList<ArrayList<Shape>> cellGrid;
  private Slider slider;
  private Button playpause;
  private Button loadFile;
  private Menu newWindow;
  private Menu exit;
  private Button reset;
  private Button step;
  private MenuBar menuBar;
  private File currentFile;
  private Map<Integer, Color> myColorMap;
  private double secondsElapsed;
  private double speed;
  private boolean running;

  public Visualizer(Grid grid)
      throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    myGrid = grid;
    setSpeed(50);
    myResources = ResourceBundle.getBundle(RESOURCE_PACKAGE);
  }

  /**
   * Creates the scene for the visualization. Calls the loading of an .xml file, and uses that data to create
   * the UI and graphical components and assemble them into a GUI.
   * @return the scene to be placed in the stage
   * @throws ParserConfigurationException //FIXME
   * @throws SAXException FIXME
   * @throws IOException FIXME
   */
  public Scene createScene() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    frame = new BorderPane();
    running = false;
    frame.setTop(setToolBar());
    frame.setBottom(instantiateCellGrid());
    Scene scene = new Scene(frame, Color.AZURE);
    scene.getStylesheets().add(getClass().getClassLoader().getResource(STYLESHEET).toExternalForm());
    myScene = scene;
    return scene;
  }

  /**
   * Instantiates a grid of rectangles in a gridpane to be rendered by the scene. Takes color data
   * from the Grid class and uses it to create scaled rectangles at the correct size and dimension
   * and collects these rectangles into a gridpane.
   * @return A gridpane containing all the rectangles in the simulation
   */
  //FIXME I set the width equal to the size/num vert cells. This will only work for squares, I am wondering why it is breaking like this.
  protected abstract Node instantiateCellGrid();
/*
  /**
   * Loads an .xml file by passing it to the Config class which creates the model backend for the simulation.
   * Then updates the cell matrix to the new status of the loaded file.
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
/*
  public void loadConfigFile(File file) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    config = new Config(file);
    //myGrid = config.loadFile();  //takes in grid constructor
    frame.setBottom(instantiateCellGrid());

  }

 */

  /**
   * Creates the toolbar with UI components (buttons, sliders) to be rendered in the scene.
   * @return A toolbar node containing all the buttons, sliders, and UI components which are interacted with by the user.
   */
  private Node setToolBar() {
    HBox toolbar = new HBox();
    final Pane spacer = new Pane();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    menuBar = new MenuBar();
    //FIXME throws
    newWindow = makeMenu("New", e-> {
      try {
        Main.loadConfigFile(Main.chooseFile());
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (SAXException ex) {
        ex.printStackTrace();
      } catch (ParserConfigurationException ex) {
        ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      } catch (NoSuchMethodException ex) {
        ex.printStackTrace();
      } catch (InvocationTargetException ex) {
        ex.printStackTrace();
      } catch (InstantiationException ex) {
        ex.printStackTrace();
      } catch (IllegalAccessException ex) {
        ex.printStackTrace();
      }
    });
    loadFile = makeButton("Load", e -> {
      //FIXME We will die if we dont deal with these exception calls
      try {
        Main.loadConfigFile(Main.chooseFile());
        myStage.hide();
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (SAXException ex) {
        ex.printStackTrace();
      } catch (ParserConfigurationException ex) {
        ex.printStackTrace();
      } catch (InstantiationException ex) {
        ex.printStackTrace();
      } catch (InvocationTargetException ex) {
        ex.printStackTrace();
      } catch (NoSuchMethodException ex) {
        ex.printStackTrace();
      } catch (IllegalAccessException ex) {
        ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      }
      drawGrid();
    });
    //exit = makeMenu("Exit", e-> System.exit(0)); FIXME
    playpause = makeButton("Play", e -> handlePlayPause(playpause));
    reset = makeButton("Reset", e->{ //FIXME add intentional exceptions
      try {
        Main.loadConfigFile(currentFile);
        myStage.hide();
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (SAXException ex) {
        ex.printStackTrace();
      } catch (ParserConfigurationException ex) {
        ex.printStackTrace();
      } catch (InstantiationException ex) {
        ex.printStackTrace();
      } catch (InvocationTargetException ex) {
        ex.printStackTrace();
      } catch (NoSuchMethodException ex) {
        ex.printStackTrace();
      } catch (IllegalAccessException ex) {
        ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      }
      drawGrid();
    });
    step = makeButton("Step", e->{
      myGrid.update();
      drawGrid();
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
        setSpeed(new_val.doubleValue()/100);
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

  /**
   * Handles the creation of button objects for the UI. Checks if there is a valid image file referenced in the Image.properties
   * file in the resources folder, and if so renders an image for the button. If not, adds a simple label.
   * @param property - A string referencing a property in the Image.properties file, or just the label to be placed on the button
   * @param handler - The EventHandler to be triggered when the button is pressed.
   * @return A button object to be rendered in the scene
   */
  private Button makeButton (String property, EventHandler<ActionEvent> handler) {
    final String IMAGEFILE_SUFFIXES = String.format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    Button result = new Button();
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(label))));
    }
    else {
      result.setText(label);
    }
    result.setOnAction(handler);
    return result;
  }

  private Menu makeMenu (String property, EventHandler<ActionEvent> handler) {
    final String IMAGEFILE_SUFFIXES = String.format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    Menu result = new Menu();
    String label = myResources.getString(property);
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      result.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(label))));
    }
    else {
      result.setText(label);
    }
    result.setOnAction(handler);
    return result;
  }

  /**
   * Handles the toggling of the play/pause button. Switches the image rendered on the play/pause button as well
   * as toggling whether the simulation is running or not.
   * @param button - the play/pause button to be rerendered with a new image.
   */
  private void handlePlayPause(Button button) {
    running = !running;
    final String IMAGEFILE_SUFFIXES = String.format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));
    String label = "";
    if(running){
      label = myResources.getString("Pause");
    } else {
      label = myResources.getString("Play");
    }
    if (label.matches(IMAGEFILE_SUFFIXES)) {
      button.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(label))));

    }
  }

  /**
   * Takes in a double representing a percent value. This reflects a percent of the max speed.
   * @param percentSpeed the percent of the max speed to which to set the simulation
   */
  public void setSpeed(double percentSpeed){
    percentSpeed*=MAX_UPDATE_PERIOD;
    speed = MAX_UPDATE_PERIOD-percentSpeed;
  }

  /**
   * Update method which calls the model to update the states of all the cells on the backend, and redraws
   * the rectangles with their new color values.
   * Calls the inner update methods at a rate dependent on the speed of the simulation (specified in the .xml config file
   * and controlled by the slider).
   * @param elapsedTime - The elapsed time between frame calls made in the default start() method of the Application
   */
  public void update(double elapsedTime){
    secondsElapsed += elapsedTime;
    if(running && secondsElapsed > speed){
      secondsElapsed = 0;
      myGrid.update();
      drawGrid();
    }
  }

  /**
   * Updates the colors of the rectangles rendered in the scene. Rechecks the Grid object for color data,
   * and then passes this color data into each cell.
   */
  public void drawGrid(){
    Color[][] colorgrid = myGrid.getColorGrid();
    for(int i = 0; i < cellGrid.size(); i++){
      for(int j = 0; j < colorgrid[i].length; j++){
        cellGrid.get(i).get(j).setFill(colorgrid[i][j]);
      }
    }
  }

  public void setGrid(Grid newGrid){myGrid = newGrid;}
  public void setStage(Stage newStage){myStage = newStage;}
}
