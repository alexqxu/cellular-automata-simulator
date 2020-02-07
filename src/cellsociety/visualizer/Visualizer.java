package cellsociety.visualizer;


import cellsociety.Config;
import cellsociety.Main;
import cellsociety.simulation.Grid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public abstract class Visualizer {

  protected static final int SIZE = 400;
  private static final String RESOURCE_PACKAGE = "Image";
  private static final String STYLESHEET = "default.css";
  private static final int MAX_UPDATE_PERIOD = 2;

  protected Grid myGrid;
  private Scene myScene;
  private Stage myStage;
  private Config myConfig;
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
  //private Map<Integer, Color> myColorMap;
  private double secondsElapsed;
  private double speed;
  private boolean running;
  protected Map<Integer, Color> myColorMap = new HashMap<>(); //FIXME maverick's change

  public Visualizer(Grid grid) {
    myGrid = grid;
  }


  /**
   * Instantiates a grid of rectangles in a gridpane to be rendered by the scene. Takes color data
   * from the Grid class and uses it to create scaled rectangles at the correct size and dimension
   * and collects these rectangles into a gridpane.
   *
   * @return A gridpane containing all the rectangles in the simulation
   */
  //FIXME I set the width equal to the size/num vert cells. This will only work for squares, I am wondering why it is breaking like this.
  public abstract Node instantiateCellGrid();


  public void stepGrid() {
    myGrid.update();
    drawGrid();
  }

    public void drawGrid () {
      for (int i = 0; i < cellGrid.size(); i++) {
        for (int j = 0; j < cellGrid.get(i).size(); j++) {
          cellGrid.get(i).get(j).setFill(myColorMap.get(myGrid.getState(i, j)));
        }
      }
    }

    public void setStateColor (int state, Color color){
      myColorMap.put(state, color);
    }

    public void setColorMap(Map<Integer, Color> newMap){
      myColorMap = newMap;
    }

    protected Color[][] getColorGrid (){
      Color[][] colorgrid = new Color[myGrid.getWidth()][myGrid.getHeight()];
      for (int i = 0; i < colorgrid.length; i++) {
        for (int j = 0; j < colorgrid[i].length; j++) {
          colorgrid[i][j] = myColorMap.get(myGrid.getState(i, j));
        }
      }
      return colorgrid;
    }

    public void setGrid (Grid newGrid){
      myGrid = newGrid;
    }

}
