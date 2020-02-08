package cellsociety.visualizer;


import cellsociety.config.Config;
import cellsociety.simulation.Grid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

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
  protected Map<Integer, Color> myColorMap; //FIXME maverick's change
  private LineChart<Number, Number> myGraph;
  private List<Series> mySeries;
  private double timeElapsed;


  public Visualizer(Grid grid) {
    myGrid = grid;
    timeElapsed = 0;
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

  private Node setGraph() {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("Time (seconds)");
    yAxis.setLabel("Population");

    myGraph = new LineChart<>(xAxis, yAxis);
    myGraph.setTitle("Cell Population");

    mySeries = new ArrayList<XYChart.Series>();
    int[] populations = myGrid.getPopulations();
    for (int i = 0; i < populations.length; i++) {
      XYChart.Series tempSeries = new XYChart.Series<>();
      mySeries.add(tempSeries);
      myGraph.getData().add(tempSeries);
      //System.out.println(myColorMap.get(i).toString() + "YEET");
      Set<Node> nodes = myGraph.lookupAll(".series");
      for(Node series : nodes){
        StringBuilder style = new StringBuilder();
        style.append(myColorMap.get(i).toString().substring(2,8)+";");
        series.setStyle("-fx-stroke: "+style.toString());
      }
    }
    myGraph.applyCss();
    return myGraph;
  }

  public void updateChart(double secondsElapsed) {
    timeElapsed += secondsElapsed;
    for (int i = 0; i < mySeries.size(); i++) {
      mySeries.get(i).getData().add(new XYChart.Data(timeElapsed, myGrid.getPopulations()[i]));
    }
  }

  public Node bundledUI() {
    BorderPane bundle = new BorderPane();
    bundle.setCenter(instantiateCellGrid());
    bundle.setRight(setGraph());
    bundle.setBottom(setParamBar());
    return bundle;
  }

  public void stepGrid() {
    if(myGrid.update()){
      instantiateCellGrid();
    } else {
      drawGrid();
    }
  }

  public void drawGrid() {
    for (int i = 0; i < cellGrid.size(); i++) {
      for (int j = 0; j < cellGrid.get(i).size(); j++) {
        cellGrid.get(i).get(j).setFill(myColorMap.get(myGrid.getState(i, j)));
      }
    }
  }

//    public void setStateColor (int state, Color color){
//      myColorMap.put(state, color);
//    }

  public void setColorMap(Map<Integer, Color> newMap) {
    myColorMap = newMap;
  }

  protected Color[][] getColorGrid() {
    Color[][] colorgrid = new Color[myGrid.getWidth()][myGrid.getHeight()];
    for (int i = 0; i < colorgrid.length; i++) {
      for (int j = 0; j < colorgrid[i].length; j++) {
        colorgrid[i][j] = myColorMap.get(myGrid.getState(i, j));
      }
    }
    return colorgrid;
  }

  public void setGrid(Grid newGrid) {
    myGrid = newGrid;
  }

  private Node setParamBar() {
    HBox parameters = new HBox();
    String[] paramList = getParameters();
    for (String s : paramList) {
      TextField paramField = makeParamField(s);
      parameters.getChildren().add(paramField);
      final Pane spacer = new Pane();
      Label label = new Label(s);
      label.setMinWidth(50);
      parameters.getChildren().add(label);
      HBox.setHgrow(spacer, Priority.ALWAYS);
      parameters.getChildren().add(spacer);
    }
    return parameters;
  }

  private TextField makeParamField(String param) {
    TextField paramField = new TextField();
    paramField.setPrefColumnCount(50);
    paramField.setMaxWidth(50);
    paramField.setOnAction(e -> {
      if (paramField.getText() != null && !paramField.getText().isEmpty()) {
        double value = Double.parseDouble(paramField.getText());
        setParameters(param, value);
      } else {
        Alert errorAlert = new Alert(AlertType.WARNING);
        errorAlert.setHeaderText("Enter a valid double");
        errorAlert.setContentText("please");
        errorAlert.showAndWait();
      }
    });
    return paramField;
  }

  public String[] getParameters() {
    return myGrid.getParams();
  }

  public void setParameters(String param, double newValue) {
    myGrid.setParam(param, newValue);
  }

  public int[] getPopulations() {
    return myGrid.getPopulations();
  }
}
