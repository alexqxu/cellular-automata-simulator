package cellsociety;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
    public static final String STYLESHEET = "default.css";

    private Stage myStage;
    private Grid myGrid;
    private Configuration config;
    private ArrayList<ArrayList<Rectangle>> cellGrid;
    private Slider slider;
    private Button playpause;
    private Button loadFile;
    private Button reset;
    private FileChooser fileChooser;
    private String currentFile;
    private double secondsElapsed;
    private double speed;
    private boolean running;


    /**
     * Start method. Runs game loop after setting up stage and scene data.
     * @param stage the window in which the application runs
     * @throws Exception
     */
    @Override
    public void start(Stage stage) { //throws exception?
        myStage = stage;
        myStage.setScene(createScene("file"));
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }
    private String chooseFile(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Simulation File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(myStage);
        if(file!=null){
            System.out.println(file.getPath());
            currentFile = file.getPath();
            return file.getPath();
        }else{
            System.out.println("Error: File not found");
        }
        return "";
    }
    //FIXME is filename necessary here or should I have instance var
    private Scene createScene(String filename){
        BorderPane frame = new BorderPane();
        config = new Configuration(); //FIXME Instance class?
        loadConfigFile(chooseFile());

        running = false;
        frame.setTop(setToolBar());
        frame.setBottom(instantiateCellGrid());

        setSpeed(.5); // FIXME added by Maverick
        Scene scene = new Scene(frame, Color.AZURE);
        scene.getStylesheets().add(getClass().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET).toExternalForm());

        scene.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.SPACE){
                handlePlayPause(playpause);
            }
        });
        return scene;
    }

    private Node setToolBar() {
        HBox toolbar = new HBox();
        //FIXME instance variable buttons/sliders?
        playpause = new Button("Play");
        playpause.setOnAction(e -> handlePlayPause(playpause));

        loadFile = new Button("Load File");
        loadFile.setOnAction(e -> {
            loadConfigFile2(chooseFile());
            drawGrid();
        });

        reset = new Button("Reset");
        reset.setOnAction(e->{
            loadConfigFile(currentFile);
            drawGrid();
        });

        toolbar.getChildren().add(reset);
        toolbar.getChildren().add(loadFile);
        toolbar.getChildren().add(playpause);
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

        toolbar.getChildren().add(slider);
        return toolbar;
    }

    private void handlePlayPause(Button button) {
        running = !running;
        if(running){
            button.setText("Pause");
        } else {
            button.setText("Play");
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

    public void loadConfigFile(String filename){
        myGrid = new Grid();
        HashMap<String, Double> paramMap = new HashMap<>();
        paramMap.put(FireCell.PROB_CATCH, 0.7);
        paramMap.put(SegregationCell.HAPPINESS_THRESH, .3);
        paramMap.put(WaTorCell.FISH_BREED_TIME, 5.0);
        paramMap.put(WaTorCell.SHARK_BREED_TIME, 40.0);
        paramMap.put(WaTorCell.FISH_FEED_ENERGY, 2.0);
        paramMap.put(WaTorCell.SHARK_START_ENERGY, 5.0);
        myGrid.setRandomGrid("WaTorCell", paramMap, new double[]{.2,.7,.1}, 50, 50);
    }
    public void loadConfigFile2(String filename){
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
