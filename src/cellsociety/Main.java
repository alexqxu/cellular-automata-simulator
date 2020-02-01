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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: Alex Oesterling, axo
 */
public class Main extends Application {
    public static final String TITLE = "Cell Simulator";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 120;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    private static final String RESOURCES = "resources";
    public static final String DEFAULT_RESOURCE_FOLDER = "/" + RESOURCES + "/";
    public static final String STYLESHEET = "default.css";

    private Stage myStage;
    private Grid myGrid;
    private Configuration config;
    private ArrayList<ArrayList<Rectangle>> cellGrid;
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
    //FIXME is filename necessary here or should I have instance var
    private Scene createScene(String filename){
        BorderPane frame = new BorderPane();
        config = new Configuration(); //FIXME Instance class?
        loadConfigFile(filename);

        //instantiateCellGrid();
        running = false;
        //extract to create UI components?
        frame.setTop(setToolBar());
        frame.setBottom(instantiateCellGrid());

        setSpeed(.5); // FIXME added by Maverick
        Scene scene = new Scene(frame, SIZE, SIZE, Color.AZURE);
        scene.getStylesheets().add(getClass().getResource(DEFAULT_RESOURCE_FOLDER + STYLESHEET).toExternalForm());
        return scene;
    }

    private Node setToolBar() {
        HBox toolbar = new HBox();
        //FIXME instance variable buttons/sliders?
        Button playpause = new Button("Play");
        playpause.setOnAction(e -> handlePlayPause(playpause));

        toolbar.getChildren().add(playpause);
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(50);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
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
        paramMap.put("probCatch", 1.0);
        paramMap.put("happinessThresh", .3);
        myGrid.setRandomGrid("ConwayCell", paramMap, new double[]{.2, .2, .01}, 20, 20);
        return;
    }

    public void drawGrid(){
        Color[][] colorgrid = myGrid.getColorGrid();
        for(int i = 0; i < colorgrid.length; i++){
            for(int j = 0; j < colorgrid[i].length; j++){
                Rectangle cell = cellGrid.get(i).get(j);
                cell.setFill(colorgrid[i][j]);
                cell.setStrokeType(StrokeType.INSIDE);
                cell.setStroke(Color.GRAY);
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
