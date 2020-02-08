package cellsociety.visualizer;

import cellsociety.simulation.Grid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

public class RectVisualizer extends Visualizer {

  public RectVisualizer(Grid grid){
      super(grid);
  }

  public Node instantiateCellGrid() {
    GridPane gridpane = new GridPane();
    cellGrid = new ArrayList<ArrayList<Shape>>();
    Color[][] colorgrid = getColorGrid();
    for (int i = 0; i < colorgrid.length; i++) {
      cellGrid.add(new ArrayList<Shape>());
      for (int j = 0; j < colorgrid[i].length; j++) {
        Rectangle cell = new Rectangle();
        cell.setFill(colorgrid[i][j]);
        cell.setStrokeType(StrokeType.INSIDE);
        cell.setStroke(Color.GRAY);
        cell.setStrokeWidth(.5);
        cell.setWidth(SIZE / colorgrid.length);
        cell.setHeight(SIZE / colorgrid[i].length);
        cell.setX(i * cell.getWidth());
        cell.setY(j * cell.getHeight());
        final int r = i;
        final int c = j;
        cell.setOnMouseClicked(e -> {
          myGrid.incrementCellState(r, c);
          drawGrid();
        });
        cellGrid.get(i).add(cell);
        gridpane.add(cell, j, i); //order of these was changed by Maverick to make other things work
      }
    }
    return gridpane;
  }
}
