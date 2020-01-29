package cellsociety;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;

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
    private ArrayList<ArrayList<Rectangle>> cellGrid;


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

    private Scene createScene(String filename){
        instantiateCellGrid();
        loadConfigFile(filename);
        drawGrid();
        Scene scene = new Scene(null); //
        return scene;
    }
    //FIXME how do we figure out the size of the cellgrid?
    private void instantiateCellGrid() {
        cellGrid = new ArrayList<ArrayList<Rectangle>>();

    }

    private void update(double elapsedTime){
        stepGrid();
    }

    public void setSpeed(){
        return;
    }

    public void loadConfigFile(String filename){
        //Grid =
        return;
    }

    public void drawGrid(){
        Color[][] colorgrid = myGrid.getColorGrid();
        for(int i = 0; i < colorgrid.length; i++){
            cellGrid.add(new ArrayList<Rectangle>());
            for(int j = 0; j < colorgrid[i].length; j++){
                Rectangle cell = new Rectangle();
                cell.setFill(colorgrid[i][j]);
                cell.setWidth(SIZE/colorgrid[i].length);
                cell.setHeight(SIZE/colorgrid.length);
                cell.setX(j*cell.getWidth());
                cell.setY(i*cell.getHeight());
                cellGrid.get(i).add(cell);
            }
        }
        return;
    }

    public void drawCell(int x, int y, Color color){
        return;
    }

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
