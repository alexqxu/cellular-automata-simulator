package cellsociety.simulation.cell;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Models segregation. Cells are "happy" with a certain percent of like neighbors, and will move about
 * to open cells until they are "happy".
 *
 * Assumptions: See Cell. Additionally, assumes states are 0-2.
 *
 * Dependencies: Cell
 */
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
  protected void planUpdate(Cell[] neighbors, Queue<Cell> emptyQueue) {
    LinkedList<Cell> cellQueue = new LinkedList<>(emptyQueue);
    Collections.shuffle(cellQueue);
    emptyQueue.clear();
    while (!cellQueue.isEmpty()) {
      emptyQueue.add(cellQueue.remove());
    }
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
      } else {
        nextState = state;
      }
      return;
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
