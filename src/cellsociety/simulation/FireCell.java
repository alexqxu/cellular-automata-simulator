package cellsociety.simulation;

import java.util.LinkedList;
import java.util.Random;

public class FireCell extends Cell {

  public static final String PROB_CATCH = "probCatch";
  public static final String PROB_GROW = "probGrow";

  public FireCell() {
    super();
    defaultEdge = 0;
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
    if (pointingUp) {
      neighbors = rotateNeighbors(neighbors);
    }
    if (getState() == 0) {
      nextState = 0;
    }
    if (getState() == 2) {
      nextState = 0;
    }
    if (getState() == 1) {
      boolean canBurn = false;
      int offset = getSideOffset(neighbors.length);
      for (int i = 0; i < neighbors.length; i += offset) {
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
