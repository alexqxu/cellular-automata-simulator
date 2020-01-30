package cellsociety;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private Stage myStage;
    private Grid myGrid;
    private Configuration config;
    private Group root;
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

        setSpeed(1); // FIXME added by Maverick

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }
    //FIXME is filename necessary here or should I have instance var
    private Scene createScene(String filename){
        root = new Group();
        config = new Configuration();
        loadConfigFile(filename);
        instantiateCellGrid();
        running = false;
        Button playpause = new Button("Play");
        playpause.setOnAction(e -> handlePlayPause(playpause));
        root.getChildren().add(playpause);
        Scene scene = new Scene(root, SIZE, SIZE, Color.AZURE);
        return scene;
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
    private void instantiateCellGrid() {
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
                root.getChildren().add(cell);
            }
        }
    }

    private void update(double elapsedTime){
        secondsElapsed += elapsedTime;
        if(running && secondsElapsed > speed){
            secondsElapsed = 0;
            stepGrid();
            drawGrid();
        }
    }

    public void setSpeed(double s){
        speed = s;
    }

    public void loadConfigFile(String filename){
        myGrid = new Grid();
        HashMap<String, Double> paramMap = new HashMap<>();
        paramMap.put("probCatch", 0.8);
        paramMap.put("happinessThresh", .3);
        myGrid.setRandomGrid("SegregationCell", paramMap, new double[]{.3, .2, .6}, 20, 20);
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
