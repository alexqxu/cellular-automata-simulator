package cellsociety.visualizer;

import cellsociety.simulation.grid.Grid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * @author: axo
 * Handles rendering of cell grid based on Grid object data and passes it up to the SimulationApp application
 */
public abstract class Visualizer {
  protected static final int SIZE = 400;

  protected Grid myGrid;
  private BorderPane bundle;
  protected ArrayList<ArrayList<Shape>> cellGrid;
  protected Map<Integer, Color> myColorMap;
  private LineChart<Number, Number> myGraph;
  private List<Series> mySeries;
  private long stepsElapsed;
  protected boolean gridLines;

  /**
   * Constructor, gives Visualizer access to the grid created by the Config after reading the XML, and sets
   * the step tracker for the graph
   */
  public Visualizer(Grid grid) {
    bundle = new BorderPane();
    myGrid = grid;
    stepsElapsed = 0;
    gridLines = true;
  }

  /**
   * Instantiates a grid of shapes in a pane to be rendered by the scene. Takes color data
   * from the Grid class and uses it to create scaled shapes at the correct size and dimension
   * and collects these shapes into a pane
   *
   * @return A node containing all the shapes in the simulation
   */
  protected abstract Node instantiateCellGrid();

  /**
   * Creates the graph which tracks cell populations. Creates and color-codes legend
   * based on color data given to Visualizer by the Grid object
   * @return an empty Line Chart for points to be placed into as simulation steps
   */
  private Node setGraph() {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("Steps");
    yAxis.setLabel("Population");

    myGraph = new LineChart<>(xAxis, yAxis);

    myGraph.applyCss();
    myGraph.setCreateSymbols(false);
    myGraph.setTitle("Cell Population");

    mySeries = new ArrayList<XYChart.Series>();
    int[] populations = myGrid.getPopulations();
    for (int i = 0; i < populations.length; i++) {
      XYChart.Series tempSeries = new XYChart.Series<>();
      mySeries.add(tempSeries);
      myGraph.getData().add(tempSeries);
    }
    addPoint();
    return myGraph;
  }

  /**
   * Updates graph by placing a point at the next integer step representing the populations of each cell in the simulation
   */
  public void updateChart() {
    stepsElapsed += 1;
    addPoint();
  }

  /**
   * Places a point for each cell's population on the graph when called
   */
  private void addPoint() {
    for (int i = 0; i < mySeries.size(); i++) {
      XYChart.Data point = new XYChart.Data(stepsElapsed, myGrid.getPopulations()[i]);
      mySeries.get(i).getData().add(point);
      Set<Node> nodes = myGraph.lookupAll(".series"+i);
      for(Node series : nodes){
        StringBuilder style = new StringBuilder();
        style.append("-fx-stroke: " + "#"+myColorMap.get(i).toString().substring(2, 8)+"; ");
        style.append("-fx-background-color: "+"#"+myColorMap.get(i).toString().substring(2,8)+", white;");
        series.setStyle(style.toString());
      }
    }
  }

  /**
   * Bundles the cell grid, graph, and parameter text fields into a single Node to be passed into the
   * Simulation Application
   * @return a Node containing the Cell Grid render, a Graph, and a set of Text Fields for tuning parameters
   */
  public Node bundledUI() {
    bundle.setCenter(instantiateCellGrid());
    bundle.setRight(setGraph());
    bundle.setBottom(setParamBar());
    return bundle;
  }

  /**
   * Steps the state of the grid by one. Checks for rescaling, and if so, recreates the entire grid
   * at the new size. If not, just repaints the existing grid.
   */
  public void stepGrid() {
    if(myGrid.update()){
      bundle.setCenter(instantiateCellGrid());
    }
    drawGrid();
  }

  /**
   * Repaints the grid by iterating through every cell and checking its new color data, and then
   * sets the cell to this new color data.
   */
  public void drawGrid() {
    for (int i = 0; i < cellGrid.size(); i++) {
      for (int j = 0; j < cellGrid.get(i).size(); j++) {
        cellGrid.get(i).get(j).setFill(myColorMap.get(myGrid.getState(i, j)));
      }
    }
  }

  public void reDrawGrid() {
    bundle.setCenter(instantiateCellGrid());
  }

  /**
   * Sets the color mapping of integer to color for use in rendering the correct colors.
   * Allows the grid to not have to pass Color data (or even access color data) from JavaFX
   * @param newMap - the map with which the Visualizer will map the Grid's passed integer cell state
   *               data to color data
   */
  public void setColorMap(Map<Integer, Color> newMap) {
    myColorMap = newMap;
  }

  /**
   * Generates an array of Color objects used to generate the colored shapes in the visualizer
   * @return an array of Color objects corresponding to the colors of different cells
   */
  protected Color[][] getColorGrid() {
    Color[][] colorgrid = new Color[myGrid.getHeight()][myGrid.getWidth()];
    for (int i = 0; i < colorgrid.length; i++) {
      for (int j = 0; j < colorgrid[i].length; j++) {
        colorgrid[i][j] = myColorMap.get(myGrid.getState(i, j));
      }
    }
    return colorgrid;
  }

  /**
   * Sets the Visualizer's Grid instance variable to a new grid
   * @param newGrid - The grid to which the Visualizer's Grid instance variable will be set to
   */
  public void setGrid(Grid newGrid) {
    myGrid = newGrid;
  }

  /**
   * Generates a set of text fields which allow the user to change certain parameters unique to
   * each simulation.
   * @return an HBox containing a number of text fields corresponding to tunable parameters in the simulation
   */
  private Node setParamBar() {
    HBox parameters = new HBox();
    String[] paramList = myGrid.getParams();
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

  /**
   * Makes a textfield box to put into the pane of textfields used to tune parameters.
   * Creates a textfield with a label corresponding to the parameter it tunes
   * @param param - the parameter which the textfield will tune
   * @return a TextField object to be placed in a pane and rendered in the scene which affects certain
   * parameters unique to each simulation
   */
  private TextField makeParamField(String param) {
    TextField paramField = new TextField();
    paramField.setPrefColumnCount(50);
    paramField.setMaxWidth(50);
    paramField.setOnAction(e -> {
      if (paramField.getText() != null && !paramField.getText().isEmpty()) {
        double value = Double.parseDouble(paramField.getText());
        myGrid.setParam(param, value);
      } else {
        Alert errorAlert = new Alert(AlertType.WARNING);
        errorAlert.setHeaderText("Enter a valid double");
        errorAlert.setContentText("please");
        errorAlert.showAndWait();
      }
    });
    return paramField;
  }

  /**
   * @return the current size of the grid being rendered
   */
  public int getWidth() {
    return myGrid.getWidth();
  }

  /**
   * @return the current size of the grid being rendered
   */
  public int getHeight() {
    return myGrid.getHeight();
  }

  /**
   * @return the current Grid object
   */
  public Grid getGrid() {return myGrid;}

  /**
   * Sets whether gridLines should be rendered or not
   */
  public void setGridLines(boolean newGridLines) {gridLines = newGridLines;}

  /**
   * @return whether gridLines should be rendered or not
   */
  public boolean getGridLines(){return gridLines;}
}
