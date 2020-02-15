package cellsociety.simulation.grid;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.simulation.cell.Cell;
import cellsociety.simulation.cell.FireCell;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class Grid {

  public static final String CELL_PREFIX = "cellsociety.simulation.cell.";
  protected ArrayList<ArrayList<Cell>> grid;
  private Set<Integer> states = new HashSet<>();

  public Grid() {
    grid = new ArrayList<>();
  }

  private static Cell getRandomCell(String className, Map<String, Double> paramMap,
      double[] stateChances) throws ClassNotFoundException {
    Class cellClass = null;
    Cell cell = null;
    try {
      cellClass = Class.forName(CELL_PREFIX + className);
      cell = (Cell) (cellClass.getConstructor().newInstance());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
        NoSuchMethodException | InvocationTargetException e) {
      throw new InvalidCellException(e);
    }
    for (Map.Entry<String, Double> param : paramMap.entrySet()) {
      cell.setParam(param.getKey(), param.getValue());
    }
    double chanceSum = 0;
    for (int i = 0; i < stateChances.length; i++) {
      chanceSum += stateChances[i];
    }
    Random rand = new Random();
    double roll = rand.nextDouble() * chanceSum;
    int state = 0;
    for (int i = 0; i < stateChances.length; i++) {
      roll -= stateChances[i];
      if (roll <= 0) {
        cell.setState(i);
        break;
      }
    }
    return cell;
  }

  /**
   * Steps the grid by 1 timestep. Returns true if the gridsize has changed, and false otherwise
   * @return boolean value, true if the grid size has changed
   */
  public boolean update() {
    LinkedList<Cell> emptyQueue = getEmptyQueue();
    boolean padded = false;
    if (getCell(0, 0).getDefaultEdge() == Cell.INFINTE) {
      padded = padGrid();
    }
    for (int i = 0; i < getHeight(); i++) {
      for (int j = 0; j < getWidth(); j++) {
        Cell[] neighbors = getNeighbors(i, j);
        getCell(i, j).planUpdateFull(neighbors, emptyQueue);
      }
    }
    for (int i = 0; i < getHeight(); i++) {
      for (int j = 0; j < getWidth(); j++) {
        grid.get(i).get(j).update();
      }
    }
    return padded;
  }

  /**
   * Increases the state of the cell by 1, looping around to 0 if it gets too high.
   * Used to click on cells to change their state.
   * @param r Row of the cell to be changed
   * @param c Column of the cell to be changed
   */
  public void incrementCellState(int r, int c) {
    grid.get(r).get(c).incrementState(getHighestState());
  }

  private int getHighestState() {
    return Collections.max(states);
  }

  /**
   * Adds a valid state to the grid. Used in incrementing states
   * @param st State to be added
   */
  public void addState(int st) {
    states.add(st);
  }

  /**
   * Returns an int array, where the ith value of the returned array is the number of state i cells
   * @return an int array, where the ith value of the returned array is the number of state i cells
   */
  public int[] getPopulations() {
    int[] ret = new int[getHighestState() + 1];
    for (int r = 0; r < getHeight(); r++) {
      for (int c = 0; c < getWidth(); c++) {
        ret[getState(r, c)]++;
      }
    }
    return ret;
  }

  /**
   * Returns the value of a simulation parameter. Used in saving to XML
   * @param param The parameter to be read
   * @return the double value of the parameter
   */
  public double getParam(String param) {
    return getCell(0, 0).getParam(param);
  }

  /**
   * Returns an array of the parameters being used in simulation
   * @return an array of the parameters being used in simulation
   */
  public String[] getParams() {
    return getCell(0, 0).getParams();
  }

  /**
   * Sets the value of a simulation parameter
   * @param param the parameter to be set
   * @param value the double value of the parameter
   */
  public void setParam(String param, double value) {
    for (int r = 0; r < getHeight(); r++) {
      for (int c = 0; c < getWidth(); c++) {
        getCell(r, c).setParam(param, value);
      }
    }
  }

  /**
   * Returns the Cell[] of neighbors of the cell at r,c. Should start with the northmost Cell and
   * rotate clockwise
   * @param r Row of the cell
   * @param c Column of the cell
   * @return the Cell[] of neighbors of the cell at r,c
   */
  abstract Cell[] getNeighbors(int r, int c);

  protected Cell[] getSpecificNeighbors(int r, int c, int[] dr, int[] dc) {
    Cell[] ret = new Cell[dr.length];
    if (getCell(r, c).getDefaultEdge() == Cell.TOROIDAL
        || getCell(r, c).getDefaultEdge() == Cell.INFINTE) {
      for (int i = 0; i < ret.length; i++) {
        ret[i] = grid.get((r + dr[i] + getHeight()) % getHeight())
            .get((c + dc[i] + getWidth()) % getWidth());
      }
    } else {
      for (int i = 0; i < ret.length; i++) {
        try {
          ret[i] = grid.get(r + dr[i]).get(c + dc[i]);
        } catch (IndexOutOfBoundsException e) {
          Cell cell = dupeCell(getCell(r, c));
          cell.setState(getCell(r, c).getDefaultEdge());
          ret[i] = cell;
        }
      }
    }
    return ret;
  }

  private Cell dupeCell(Cell cell) {
    Cell ret = null;
    try {
      ret = cell.getClass().getConstructor().newInstance();
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      ret = new FireCell(); //FIXME Should never happen due to error handling elsewhere
    }
    for (String s : cell.getParams()) {
      ret.setParam(s, cell.getParam(s));
    }
    return ret;
  }

  private ArrayList<Cell> generateEmptyRow() {
    ArrayList<Cell> newRow = new ArrayList<>();
    for (int i = 0; i < getWidth(); i++) {
      Cell cell = dupeCell(getCell(0, 0));
      cell.setState(0); //FIXME make changable?
      cell.setNextState(0);
      newRow.add(cell);
    }
    return newRow;
  }

  protected boolean padGrid() {
    boolean ret = false;
    if (!topRowEmpty()) {
      ret = true;
      grid.add(0, generateEmptyRow());
    }
    if (!bottomRowEmpty()) {
      ret = true;
      grid.add(generateEmptyRow());
    }
    return (ret || checkSides());
  }

  protected boolean checkSides() {
    boolean ret = false;
    if (!leftColumnEmpty()) {
      ret = true;
      for (int r = 0; r < grid.size(); r++) {
        Cell newCell = dupeCell(getCell(0, 0));
        newCell.setState(0);
        newCell.setNextState(0);
        grid.get(r).add(0, newCell);
      }
    }
    if (!rightColumnEmpty()) {
      ret = true;
      for (int r = 0; r < grid.size(); r++) {
        Cell newCell = dupeCell(getCell(0, 0));
        newCell.setState(0);
        newCell.setNextState(0);
        grid.get(r).add(newCell);
      }
    }
    return ret;
  }

  private boolean rightColumnEmpty() {
    for (int i = 0; i < grid.size(); i++) {
      ArrayList<Cell> row = grid.get(i);
      Cell cell = row.get(row.size() - 1);
      if (cell.getState() != 0) {
        return false;
      }
    }
    return true;
  }

  private boolean leftColumnEmpty() {
    for (int i = 0; i < grid.size(); i++) {
      Cell cell = grid.get(i).get(0);
      if (cell.getState() != 0) {
        return false;
      }
    }
    return true;
  }

  private boolean bottomRowEmpty() {
    ArrayList<Cell> row = grid.get(grid.size() - 1);
    for (Cell cell : row) {
      if (cell.getState() != 0) {
        return false;
      }
    }
    return true;
  }

  private boolean topRowEmpty() {
    ArrayList<Cell> row = grid.get(0);
    for (Cell cell : row) {
      if (cell.getState() != 0) {
        return false;
      }
    }
    return true;
  }

  protected Cell[] getNeighbors(Cell cell) {
    for (int r = 0; r < grid.size(); r++) {
      int c = grid.get(r).indexOf(cell);
      if (c != -1) {
        return getNeighbors(r, c);
      }
    }
    throw new RuntimeException("cell wasn't in the grid");
  }

  /**
   * Creates a random grid with the given parameters
   * @param className name of the class of the Cell for the simulation
   * @param paramMap <String, Double> map of parameters for the simulation
   * @param stateChances An int[] where the value of the ith element is the probability of state i. Automatically normalizes
   * @param borderState The state of cells on the border. Can be Cell.INFINITE or Cell.TOROIDAL
   * @param mask The array neighbor mask for this simulation
   * @param rows Number of rows for the grid
   * @param cols Number of columns for the grid
   * @throws ClassNotFoundException if the className does not exist
   */
  public void setRandomGrid(String className, Map<String, Double> paramMap, double[] stateChances,
      int borderState, int[] mask, int rows, int cols) throws ClassNotFoundException {
    ArrayList<ArrayList<Cell>> ret = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < cols; j++) {
        Cell cell = getRandomCell(className, paramMap, stateChances);
        cell.setDefaultEdge(borderState);
        cell.setMask(mask);
        row.add(cell);
      }
      ret.add(row);
    }
    grid = ret;
  }

  /**
   * Returns the width of the grid.
   * @return the width of the grid.
   */
  public int getWidth() {
    return grid.get(0).size();
  }

  /**
   * Returns the height of the grid.
   * @return the height of the grid.
   */
  public int getHeight() {
    return grid.size();
  }

  protected LinkedList<Cell> getEmptyQueue() {
    LinkedList<Cell> ret = new LinkedList<>();
    for (ArrayList<Cell> row : grid) {
      for (Cell cell : row) {
        if (cell.isEmpty()) {
          ret.add(cell);
        }
      }
    }
    return ret;
  }

  protected Cell getCell(int r, int c) {
    return grid.get(r).get(c);
  }

  /**
   * Returns the state of the cell at r,c
   * @param r row of the cell
   * @param c column of the cell
   * @return integer state of the cell
   */
  public int getState(int r, int c) {
    return grid.get(r).get(c).getState();
  }

  /**
   * Places a cell at r,c in the grid
   * @param r row of the cell to be placed
   * @param c column of the cell to be placed
   * @param cell cell to be placed
   */
  public void placeCell(int r, int c, Cell cell) {
    while (c >= grid.size()) {
      grid.add(new ArrayList<>());
    }
    while (r >= grid.get(c).size()) {
      grid.get(c).add(cell);
    }
    grid.get(c).set(r, cell);
  }
}
