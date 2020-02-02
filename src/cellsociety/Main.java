package cellsociety;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.xml.sax.SAXException;


import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * @author: Alex Oesterling, axo
 */
public class Main extends Application {
    public static final String TITLE = "Cell Simulator";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 60; //FIXME Maverick changed to 60 from 120 for testing
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    private static final String RESOURCES = "resources";
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + RESOURCES + "/";
    public static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
    public static final String RESOURCE_PACKAGE = "Image";
    public static final String STYLESHEET = "default.css";

    private ResourceBundle myResources;
    private Stage myStage;
    private Grid myGrid;
    private Config config;
    private ArrayList<ArrayList<Rectangle>> cellGrid;
    private Slider slider;
    private Button playpause;
    private Button loadFile;
    private Button reset;
    private Button step;
    private FileChooser fileChooser;
    private File currentFile;
    private double secondsElapsed;
    private double speed;
    private boolean running;


    /**
     * Start method. Runs game loop after setting up stage and scene data.
     * @param stage the window in which the application runs
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws IOException, SAXException, ParserConfigurationException { //throws exception?
        myStage = stage;
        myResources = ResourceBundle.getBundle(RESOURCE_PACKAGE);
        myStage.setScene(createScene());
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    /**
     * Opens a file navigator dialogue and allows the user to select an .xml file for importing into the simulation
     * @return the File object representing the .xml file to be used by the simulation
     */
    private File chooseFile(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Simulation File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(myStage);
        if(file!=null){
            currentFile = file;
            return file;
        }else{
            System.out.println("Error: File not found");
        }
        return null;
    }

    //FIXME is filename necessary here or should I have instance var

    /**
     * Creates the scene for the visualization. Calls the loading of an .xml file, and uses that data to create
     * the UI and graphical components and assemble them into a GUI.
     * @return the scene to be placed in the stage
     * @throws ParserConfigurationException //FIXME
     * @throws SAXException FIXME
     * @throws IOException FIXME
     */
    private Scene createScene() throws ParserConfigurationException, SAXException, IOException {
        BorderPane frame = new BorderPane();
        loadConfigFile2(chooseFile());

        running = false;
        frame.setTop(setToolBar());
        frame.setBottom(instantiateCellGrid());

        setSpeed(.5); // FIXME set speed in loadconfigfile
        Scene scene = new Scene(frame, Color.AZURE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource(STYLESHEET).toExternalForm());
        
        return scene;
    }

    /**
     * Creates the toolbar with UI components (buttons, sliders) to be rendered in the scene.
     * @return A toolbar node containing all the buttons, sliders, and UI components which are interacted with by the user.
     */
    private Node setToolBar() {
        HBox toolbar = new HBox();
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        playpause = makeButton("Play", e -> handlePlayPause(playpause));
        loadFile = makeButton("Load", e -> {
            loadConfigFile2(chooseFile());
            drawGrid();
        });
        reset = makeButton("Reset", e->{ //FIXME add intentional exceptions
            loadConfigFile2(currentFile);
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
     * Instantiates a grid of rectangles in a gridpane to be rendered by the scene. Takes color data
     * from the Grid class and uses it to create scaled rectangles at the correct size and dimension
     * and collects these rectangles into a gridpane.
     * @return A gridpane containing all the rectangles in the simulation
     */
    private Node instantiateCellGrid() {
        GridPane gridpane = new GridPane();
        cellGrid = new ArrayList<ArrayList<Rectangle>>();
        Color[][] colorgrid = myGrid.getColorGrid();
        for(int i = 0; i < colorgrid.length; i++) {
            cellGrid.add(new ArrayList<Rectangle>());
            for (int j = 0; j < colorgrid[i].length; j++) {
                Rectangle cell = new Rectangle();
                cell.setFill(colorgrid[i][j]);
                cell.setStrokeType(StrokeType.INSIDE);
                cell.setStroke(Color.GRAY);
                cell.setStrokeWidth(.5);
                cell.setWidth(SIZE/colorgrid[i].length);
                cell.setHeight(SIZE/colorgrid.length);
                cell.setX(j*cell.getWidth());
                cell.setY(i*cell.getHeight());
                final int r = i;
                final int c = j;
                cell.setOnMouseClicked(e->{
                    myGrid.incrementCellState(r, c);
                    drawGrid();
                });
                cellGrid.get(i).add(cell);
                gridpane.add(cell, i, j);
            }
        }
        return gridpane;
    }

    /**
     * Update method which calls the model to update the states of all the cells on the backend, and redraws
     * the rectangles with their new color values.
     * Calls the inner update methods at a rate dependent on the speed of the simulation (specified in the .xml config file
     * and controlled by the slider).
     * @param elapsedTime - The elapsed time between frame calls made in the default start() method of the Application
     */
    private void update(double elapsedTime){
        secondsElapsed += elapsedTime;
        if(running && secondsElapsed > speed){
            secondsElapsed = 0;
            myGrid.update();
            drawGrid();
        }
    }

    /**
     * Takes in a double representing a percent value. This reflects a percent of the max speed.
     * @param percentSpeed the percent of the max speed to which to set the simulation
     */
    public void setSpeed(double percentSpeed){
        percentSpeed*=2;
        speed = 2-percentSpeed;
    }
/*
    public void loadConfigFile(File file) throws IOException, SAXException, ParserConfigurationException {

        config = new Config(file);
        myGrid = config.loadFile();
    }

 */

    public void loadConfigFile2(File file){
        myGrid = new Grid();
        HashMap<String, Double> paramMap = new HashMap<>();
        paramMap.put(FireCell.PROB_CATCH, 0.7);
        paramMap.put(SegregationCell.HAPPINESS_THRESH, .3);
        paramMap.put(WaTorCell.FISH_BREED_TIME, 5.0);
        paramMap.put(WaTorCell.SHARK_BREED_TIME, 40.0);
        paramMap.put(WaTorCell.FISH_FEED_ENERGY, 2.0);
        paramMap.put(WaTorCell.SHARK_START_ENERGY, 5.0);
        myGrid.setRandomGrid("PercolationCell", paramMap, new double[]{.2,.7,0}, 50, 50);
    }

    /**
     * Updates the colors of the rectangles rendered in the scene. Rechecks the Grid object for color data,
     * and then passes this color data into each cell.
     */
    public void drawGrid(){
        Color[][] colorgrid = myGrid.getColorGrid();
        for(int i = 0; i < colorgrid.length; i++){
            for(int j = 0; j < colorgrid[i].length; j++){
                cellGrid.get(i).get(j).setFill(colorgrid[i][j]);
            }
        }
    }
    /*
    public void drawCell(int x, int y, Color color){
        return;
    }
     */

    /**
     * Runner method, actually runs the game when a user presses
     * play in the IDE
     * @param args
     */
    public static void main (String[] args)  {
        launch(args);
    }
}
