package cellsociety.simulation.cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Keeps track of the cell's state, its next state, its simulation parameters, and the ground parameters.
 * Calculates the next state based on simulation rules, its neighbors, and other grid info depending on simulation.
 *
 * Assumptions: States are non-negative and contiguous. Default edge is non-negative, Cell.TOROIDAL,
 * or Cell.INFINITE. All parameters are representable by double values.
 *
 * Dependencies: None
 */
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

  /**
   * Currently unused. Intended to return the parameters for the ground underneath the cell entity (e.g. sugar quantity)
   * @return a String[] of the ground parameter names
   */
  public String[] getGroundParams() {
    return groundParams;
  }

  /**
   * Currently unused. Sets the value of a ground parameter (e.g. sugar value)
   * @param param parameter to be set
   * @param value double value of the parameter
   */
  public void setGroundParam(String param, double value) {
    groundParamMap.put(param, value);
  }

  /**
   * Swaps a cell's parameters with another, which gets updated on the next update
   * @param other the other cell to be swapped with
   */
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

  /**
   * Applies the neighbor mask to a cell before planning the update
   * @param neighbors
   * @param cellQueue
   */
  public void planUpdateFull(Cell[] neighbors, Queue<Cell> cellQueue) {
    applyMask(neighbors);
    planUpdate(neighbors, cellQueue);
    removeMask(neighbors);
  }

  protected void removeMask(Cell[] neighbors) {
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

  protected void applyMask(Cell[] neighbors) {
    if (mask.length != neighbors.length) {
      return;
    }
    for (int i = 0; i < mask.length; i++) {
      if (mask[i] == 0) {
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
    paramMap.put(param.toLowerCase(), value);
  }

  /**
   * Get a parameter for the cell
   *
   * @param param The param to be retrieved
   * @return the value of the parameter
   */
  public double getParam(String param) {
    Double ret = paramMap.get(param.toLowerCase());
    if (ret == null) {
      throw new RuntimeException("param (" + param + ") asked for but not set.");
    }
    return ret;
  }

  /**
   * Returns if the cell is empty or not. Used to tell if the grid should include this cell in the cell queue for planUpdate
   * @return if the cell is empty
   */
  public boolean isEmpty() {
    return state == 0;
  }

  /**
   * Returns the current state of the cell
   * @return the integer state of the cell
   */
  public int getState() {
    return state;
  }

  /**
   * Sets the state of the cell
   * @param stat state to be set
   */
  public void setState(int stat) {
    this.state = stat;
  }

  /**
   * Returns the default border type of the cell
   * @return the default border type of the cell
   */
  public int getDefaultEdge() {
    return defaultEdge;
  }

  /**
   * Sets the default border type of the cell
   * @param edge the default border type of the cell
   */
  public void setDefaultEdge(int edge) {
    defaultEdge = edge;
  }

  /**
   * Prints out the state of the cell
   * @return
   */
  @Override
  public String toString() {
    return "" + getState();
  }

  /**
   * Increases the state of the cell, looping around to 0 if the current value is the max
   * @param max the highest allowed state
   */
  public void incrementState(int max) {
    state = (state + 1) % (max + 1);
  }

  protected Cell[] rotateNeighbors(Cell[] arr) {
    Cell[] ret = new Cell[arr.length];
    for (int i = 0; i < arr.length; i++) {
      ret[i] = arr[(i + arr.length / 2) % arr.length];
    }
    return ret;
  }

  /**
   * Intended for triangles. Tells the cell that it is pointing upwards
   * @param point whether or not the cell is pointing upwards
   */
  public void setPointUp(boolean point) {
    pointingUp = point;
  }

  /**
   * Sets the next state of the cell
   * @param st the next state of the cell
   */
  public void setNextState(int st) {
    nextState = st;
  }

  /**
   * Sets the neighbor mask of the cell
   * @param msk the mask to be set
   */
  public void setMask(int[] msk) {
    mask = msk;
  }
}
