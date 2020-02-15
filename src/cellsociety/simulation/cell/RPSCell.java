package cellsociety.simulation.cell;

import java.util.Queue;
import java.util.Random;

public class RPSCell extends Cell {

  public static final String RPS_THRESHHOLD = "RPSThreshhold";
  public static final String RANDOM_THRESHHOLD = "randomThreshhold";
  public static final String NUM_STATES = "numStates";
  public static final double DEFAULT_THRESH = 3.0;

  public RPSCell() {
    super();
    defaultEdge = 0;
    setParam(RPS_THRESHHOLD, DEFAULT_THRESH);
    setParam(RANDOM_THRESHHOLD, 0.0);
    setParam(NUM_STATES, 3);
  }

  protected void setParams() {
    params = new String[]{RPS_THRESHHOLD, RANDOM_THRESHHOLD};
  }

  @Override
  protected void planUpdate(Cell[] neighbors, Queue<Cell> cellQueue) {
    int max = state;
    for (Cell cell : neighbors) {
      max = Math.max(max, cell.state);
    }
    int[] counts = new int[max + 1];
    for (Cell cell : neighbors) {
      counts[cell.state]++;
    }
    max = 0;
    int newVal = -1;
    Random rand = new Random();
    for (int i = 0; i < counts.length; i++) {
      if (rps(i, state)) {
        if (counts[i] == max && i != 0 && rand.nextDouble() > 0.5) {
          max = counts[i];
          newVal = i;
        } else if (counts[i] > max && i != 0) {
          max = counts[i];
          newVal = i;
        }
      }
    }

    double thresh = getParam(RPS_THRESHHOLD);
    thresh += (rand.nextDouble() - .5) * 2 * getParam(RANDOM_THRESHHOLD);
    if (newVal != -1 && (max >= thresh || state == 0)) {
      nextState = newVal;
    } else {
      nextState = state;
    }
  }

  private boolean rps(int i, int st) {
    int num = (int) getParam(NUM_STATES);
    return rps(i, st, num);
  }

  /**
   * Returns true if i beats state in a rock-paper-scissors duel
   *
   * Given an odd int number of "players", player i beats the floor(num/2) players below it and
   * is beat by the floor(num/2) players above it.
   *
   * Ex: Given 5 players, 4 beats 2 and 3, and is beat by 5 and 1.
   *
   * @param i Challenging player
   * @param st Defending player
   * @param num The number of players
   * @return whether or not i beats state
   */
  public static boolean rps(int i, int st, int num) {
    if (st == 0) {
      return true;
    }
    if (i == 0) {
      return false;
    }
    int tempstate = st-1;
    int tempi = i-1;
    if (Math.abs(tempstate-tempi)<=num/2){
      return tempi>tempstate;
    }
    return tempi < tempstate;
  }
}
