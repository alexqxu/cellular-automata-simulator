package cellsociety.simulation;

import java.util.LinkedList;

public class ConwayCell extends Cell {

  /**
   * Creates a cell for Conway's game of life
   * <p>
   * 0 - Dead, White 1 - Alive, Black
   */
  public ConwayCell() {
    super();
    addStates(new int[]{0, 1});
  }


  @Override
  void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue) {
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
