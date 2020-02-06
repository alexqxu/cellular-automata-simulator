package cellsociety.simulation;

import cellsociety.simulation.Cell;
import java.util.LinkedList;
import java.util.Random;
import javafx.scene.paint.Color;

public class FireCell extends Cell {

  public static final String PROB_CATCH = "probCatch";
  public static final String PROB_GROW = "probGrow";

  public FireCell() {
    super();
    defaultEdge = 0;
    setStateColor(0, Color.YELLOW); //Empty
    setStateColor(1, Color.GREEN); //Tree
    setStateColor(2, Color.ORANGE); //Burning
  }

  public FireCell(double probCatch) {
    this();
    setParam(PROB_CATCH, probCatch);
  }

  @Override
  protected void setParams() {
    params = new String[]{PROB_CATCH};
  }

  @Override
  void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue) {
    if (getState() == 0) {
      nextState = 0;
    }
    if (getState() == 2) {
      nextState = 0;
    }
    if (getState() == 1) {
      boolean canBurn = false;
      for (int i = 0; i < neighbors.length; i += 2) {
        if (neighbors[i].getState() == 2) {
          canBurn = true;
        }
      }
      Random rand = new Random();
      if (canBurn && rand.nextDouble() < getParam(PROB_CATCH)) {
        nextState = 2;
      } else {
        nextState = 1;
      }
    }
  }
}
