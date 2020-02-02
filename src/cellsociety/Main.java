package cellsociety;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
        myStage.setScene(createScene("file"));
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private File chooseFile(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Simulation File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(myStage);
        if(file!=null){
            System.out.println(file.getPath());
            currentFile = file;
            return file;
        }else{
            System.out.println("Error: File not found");
        }
        return null;
    }

    //FIXME is filename necessary here or should I have instance var
    private Scene createScene(String filename) throws ParserConfigurationException, SAXException, IOException {
        BorderPane frame = new BorderPane();
        //FIXME Instance class?
        loadConfigFile2(chooseFile());

        running = false;
        frame.setTop(setToolBar());
        frame.setBottom(instantiateCellGrid());

        setSpeed(.5); // FIXME added by Maverick
        Scene scene = new Scene(frame, Color.AZURE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource(STYLESHEET).toExternalForm());
        
        return scene;
    }

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
           stepGrid();
           drawGrid();
        });

        slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(50);
        //slider.setShowTickLabels(true);
        //slider.setShowTickMarks(true);
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

    private Button makeButton (String property, EventHandler<ActionEvent> handler) {
        // represent all supported image suffixes
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

    //FIXME how do we figure out the size of the cellgrid?
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
                cellGrid.get(i).add(cell);
                gridpane.add(cell, i, j);
            }
        }
        return gridpane;
    }

    private void update(double elapsedTime){
        secondsElapsed += elapsedTime;
        if(running && secondsElapsed > speed){
            secondsElapsed = 0;
            stepGrid();
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
        myGrid.setRandomGrid("FireCell", paramMap, new double[]{.2,.7,.1}, 50, 50);
    }



    public void drawGrid(){
        Color[][] colorgrid = myGrid.getColorGrid();
        for(int i = 0; i < colorgrid.length; i++){
            for(int j = 0; j < colorgrid[i].length; j++){
                Rectangle cell = cellGrid.get(i).get(j);
                cell.setFill(colorgrid[i][j]);
                cell.setStrokeType(StrokeType.INSIDE);
                cell.setStroke(Color.GRAY);
                cell.setStrokeWidth(.5);
                cell.setWidth(SIZE/colorgrid[i].length);
                cell.setHeight(SIZE/colorgrid.length);
                cell.setX(j*cell.getWidth());
                cell.setY(i*cell.getHeight());
            }
        }
    }
    /*
    public void drawCell(int x, int y, Color color){
        return;
    }

     */

    public void stepGrid(){
        myGrid.update();
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
