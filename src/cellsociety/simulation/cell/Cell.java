package cellsociety.simulation.cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class Cell {

  public static final int TOROIDAL = -1;
  public static final int INFINTE = -2;

  protected String[] params;
  protected String[] groundParams;
  protected int state;
  protected int nextState;
  protected int defaultEdge = TOROIDAL;
  protected boolean pointingUp = false;
  protected int[] mask = new int[0];
  private Map<String, Double> paramMap = new HashMap<>();
  private Map<String, Double> groundParamMap = new HashMap<>();

  /**
   * Default constructor for all Cells. Sets the parameters to an empty array.
   */
  public Cell() {
    setParams();
  }

  protected void setParams() {
    params = new String[0];
  }

  protected void setGroundParams() {
    params = new String[0];
  }

  /**
   * Returns the parameters for this type of cell.
   *
   * @return the parameters for this type of cell.
   */
  public String[] getParams() {
    return params;
  }

  protected List<String> getAllParams() {
    ArrayList<String> ret = new ArrayList<>();
    ret.addAll(paramMap.keySet());
    return ret;
  }

  public String[] getGroundParams() {
    return groundParams;
  }

  public void setGroundParam(String param, double value) {
    groundParamMap.put(param, value);
  }

  public void swap(Cell other) {
    Map<String, Double> tempMap = new HashMap<>();
    for (String s : other.getAllParams()) {
      tempMap.put(s, other.getParam(s));
    }
    for (String s : getAllParams()) {
      other.setParam(s, getParam(s));
    }
    for (Map.Entry<String, Double> s : tempMap.entrySet()) {
      setParam(s.getKey(), s.getValue());
    }

    other.nextState = state;
    nextState = other.state;
  }

  /**
   * Plan the update for the next time step, without updating the state of the cell.
   *
   * @param neighbors Neighbors of the cell.
   * @param cellQueue Other information about the grid that the cell might need to plan its update.
   */
  abstract void planUpdate(Cell[] neighbors, Queue<Cell> cellQueue);

  public void planUpdateFull(Cell[] neighbors, Queue<Cell> cellQueue) {
    applyMask(neighbors);
    planUpdate(neighbors, cellQueue);
    removeMask(neighbors);
  }

  private void removeMask(Cell[] neighbors) {
    if (mask.length != neighbors.length) {
      return;
    }
    for (int i = 0; i < neighbors.length; i++) {
      if (neighbors[i].state == 0 && mask[i] <= 0) {
        neighbors[i].state = -mask[i];
        mask[i] = 0;
      }
    }
  }

  private void applyMask(Cell[] neighbors) {
    if (mask.length != neighbors.length) {
      return;
    }
    for (int i = 0; i < mask.length; i++) {
      if (mask[i] == 0) {
        System.out.println("mask[i] = " + mask[i]);
        mask[i] = -neighbors[i].state;
        neighbors[i].state = 0;
      }
    }
  }

  /**
   * Update the state of this cell to its planned next state.
   */
  public void update() {
    if (nextState == -1) {
      System.out.println("state = " + state);
      System.out.println("cell state is -1 so something terrible has happened");
      nextState = state;
      //throw new RuntimeException("cell state is -1 so something terrible has happened");

    }
    state = nextState;
    nextState = -1;
  }

  /**
   * Sets a cell parameter
   *
   * @param param the string name of the param
   * @param value the double value of the param
   */
  public void setParam(String param, double value) {
    paramMap.put(param, value);
  }

  /**
   * Get a parameter for the cell
   *
   * @param param The param to be retrieved
   * @return the value of the parameter
   */
  public double getParam(String param) {
    Double ret = paramMap.get(param);
    if (ret == null) {
      throw new RuntimeException("param (" + param + ") asked for but not set.");
    }
    return ret;
  }

  public boolean isEmpty() {
    return state == 0;
  }

  public int getState() {
    return state;
  }

  public void setState(int stat) {
    this.state = stat;
  }

  public int getDefaultEdge() {
    return defaultEdge;
  }

  @Override
  public String toString() {
    return "" + getState();
  }

  public void incrementState(int max) {
    state = (state + 1) % (max + 1);
  }


  protected int getSideOffset(int len) {
    if (len % 4 == 0) {
      return len / 3;
    }
    return 1;
  }

  /*protected Cell[] triAdjust(int r, int c, Cell[] arr) {
    if ((r+c)%2==0 && arr.length==12) {
      return rotateNeighbors(arr);
    }
    return arr;
  }*/

  protected Cell[] rotateNeighbors(Cell[] arr) {
    Cell[] ret = new Cell[arr.length];
    for (int i = 0; i < arr.length; i++) {
      ret[i] = arr[(i + arr.length / 2) % arr.length];
    }
    return ret;
  }

  public void setPointUp(boolean point) {
    pointingUp = point;
  }

  public Map<Integer, HashMap<String, Integer>> getRuleTableMap(String ruleTable) {
    String[] rules = ruleTable.split(" ");
    HashMap<Integer, HashMap<String, Integer>> ret = new HashMap<>();
    for (String rule : rules) {
      int st = Integer.parseInt("" + rule.charAt(0));
      int nextSt = Integer.parseInt("" + rule.charAt(rule.length() - 1));
      ret.putIfAbsent(st, new HashMap<>());
      HashMap<String, Integer> mapRule = ret.get(st);
      String[] combos = getStringRotations(rule.substring(1, rule.length() - 1));
      for (String combo : combos) {
        mapRule.put(combo, nextSt);
      }
    }
    return ret;
  }

  private String[] getStringRotations(String str) {
    String[] ret = new String[str.length()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = str;
      str = str.substring(1) + str.charAt(0);
    }
    return ret;
  }

  public void setNextState(int st) {
    nextState = st;
  }

  public void setMask(int[] msk) {
    mask = msk;
  }
}
