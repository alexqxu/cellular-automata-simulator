package cellsociety.simulation.cell;

import java.util.Collections;
import java.util.LinkedList;

public class SegregationCell extends Cell {

  public static final String HAPPINESS_THRESH = "happinessThresh";

  public SegregationCell() {
    super();
    defaultEdge = 0;
  }

  public SegregationCell(double happinessThresh) {
    this();
    setParam(HAPPINESS_THRESH, happinessThresh);
  }

  @Override
  protected void setParams() {
    params = new String[]{HAPPINESS_THRESH};
  }

  @Override
  void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue) {
    Collections.shuffle(emptyQueue);
    if (getState() != 0) {
      if (!happy(neighbors)) {
        if (emptyQueue.isEmpty()) {
          nextState = state;
          return;
        }
        Cell empty = emptyQueue.remove();
        empty.nextState = state;
        nextState = 0;
        emptyQueue.add(this);
        return;
      } else {
        nextState = state;
        return;
      }
    }
    if (nextState == -1) {
      nextState = 0;
    }
  }

  private boolean happy(Cell[] neighbors) {
    double total = 0;
    double same = 0;
    for (int i = 0; i < neighbors.length; i++) {
      if (neighbors[i].getState() > 0) {
        total++;
      }
      if (neighbors[i].getState() == getState()) {
        same++;
      }
    }
    return total == 0 || (same / total) > getParam(HAPPINESS_THRESH);
  }
}
