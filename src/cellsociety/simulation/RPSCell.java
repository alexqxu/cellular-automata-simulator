package cellsociety.simulation;

import java.util.LinkedList;

public class RPSCell extends Cell {
  public static final String RPS_THRESHHOLD = "RPSThreshhold";
  public static final String RANDOM_THRESHHOLD = "randomThreshhold";

  public RPSCell() {
    super();
    defaultEdge = -1;
  }

  protected void setParams() {
    params = new String[]{RPS_THRESHHOLD, RANDOM_THRESHHOLD};
  }

  @Override
  void planUpdate(Cell[] neighbors, LinkedList<Cell> cellQueue) {
    
  }
}
