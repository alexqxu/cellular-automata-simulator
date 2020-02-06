package cellsociety.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

public abstract class Cell {

  protected String[] params;
  protected String[] groundParams;
  protected int state;
  protected int nextState;
  protected Map<Integer, Color> colorMap = new HashMap<>();
  protected Map<String, Double> paramMap = new HashMap<>();
  protected Map<String, Double> groundParamMap = new HashMap<>();
  protected int defaultEdge = -1;

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
    for (String s : tempMap.keySet()) {
      setParam(s, tempMap.get(s));
    }

    other.nextState = state;
    nextState = other.state;
  }

  /**
   * Returns the color of the cell.
   *
   * @return the color of the cell.
   */
  public Color getColor() {
    return colorMap.get(state);
  }

  /**
   * Plan the update for the next time step, without updating the state of the cell.
   *
   * @param neighbors Neighbors of the cell.
   * @param cellQueue Other information about the grid that the cell might need to plan its update.
   */
  abstract void planUpdate(Cell[] neighbors, LinkedList<Cell> cellQueue);

  /**
   * Update the state of this cell to its planned next state.
   */
  public void update() {
    if (nextState == -1) {
      System.out.println("state = " + state);
      throw new RuntimeException("cell state is -1 so something terrible has happened");
    }
    state = nextState;
    nextState = -1;
  }

  /**
   * Set the state/color pair of this cell
   *
   * @param state The int state to be changed
   * @param color The color to be changed to
   */
  public void setStateColor(int state, Color color) {
    colorMap.put(state, color);
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

  protected int getHighestState() {
    int max = 0;
    for (int st : colorMap.keySet()) {
      max = Math.max(max, st);
    }
    return ++max;
  }

  public void incrementState() {
    int max = getHighestState();
    state = (state + 1) % max;
  }
}
