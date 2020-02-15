package cellsociety.simulation.cell;

import java.util.Queue;


/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Represents a percolating system. Open cells near percolated cells become percolated.
 * Blocked states remain blocked indefinitely
 *
 * Assumptions: See Cell. Additionally, assumes states are 0-2
 *
 * Dependencies: Cell
 */
public class PercolationCell extends Cell {

  public static final int WATER_EDGE_STATE = 3;

  public PercolationCell() {
    super();
    defaultEdge = WATER_EDGE_STATE;
  }

  @Override
  protected void planUpdate(Cell[] neighbors, Queue<Cell> emptyQueue) {
    if (neighbors[0].getState() == WATER_EDGE_STATE) {
      neighbors[0].setState(2); //this is okay because cells with state 3 are dummy cells
    }
    if (getState() == 1) {
      for (int i = 0; i < neighbors.length; i += 1) {
        if (neighbors[i].getState() == 2) {
          nextState = 2;
          return;
        }
      }
      nextState = 1;
    } else {
      if (state == 0) {
        nextState = 0;
      }
      if (state == 2) {
        nextState = 2;
      }
    }
  }
}
