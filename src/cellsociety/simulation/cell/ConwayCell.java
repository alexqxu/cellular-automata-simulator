package cellsociety.simulation.cell;

import java.util.Queue;

/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Implements Conway's Game of Life.
 *
 * Assumptions: See Cell. Additionally, assumes state is only 0 or 1.
 *
 * Dependencies: Cell
 */
public class ConwayCell extends Cell {

  /**
   * Creates a cell for Conway's game of life
   * <p>
   * 0 - Dead, White 1 - Alive, Black
   */
  public ConwayCell() {
    super();
    defaultEdge = INFINTE;
  }


  @Override
  protected void planUpdate(Cell[] neighbors, Queue<Cell> emptyQueue) {
    int sum = 0;
    for (int i = 0; i < neighbors.length; i++) {
      sum += neighbors[i].getState();
    }
    if (getState() == 0) {
      if (sum == 3) {
        nextState = 1;
      } else {
        nextState = 0;
      }
    } else {
      if (sum == 2 || sum == 3) {
        nextState = 1;
      } else {
        nextState = 0;
      }
    }
    //System.out.println("nextState = " + nextState);
  }
}
