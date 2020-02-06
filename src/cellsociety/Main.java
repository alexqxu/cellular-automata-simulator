package cellsociety;

import cellsociety.simulation.Grid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import javafx.scene.control.Button;
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
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
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

  private Stage myStage;
    /**
     * Start method. Runs game loop after setting up stage and scene data.
     * @param stage the window in which the application runs
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException { //throws exception?
        myStage = stage;
        Visualizer myVisualizer = new Visualizer();
        myStage.setScene(myVisualizer.createScene());
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e->myVisualizer.update(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
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
