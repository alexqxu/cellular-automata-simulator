package cellsociety.simulation;

import cellsociety.exceptions.InvalidCellException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class Grid {

  public static final String SIMULATION_PREFIX = "cellsociety.simulation.";
  protected ArrayList<ArrayList<Cell>> grid;
  private Set<Integer> states = new HashSet<>();

  public Grid() {
    grid = new ArrayList<>();
  }

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

  public void incrementCellState(int r, int c) {
    grid.get(r).get(c).incrementState(getHighestState()); //FIXME
  }

  private int getHighestState() {
    return Collections.max(states);
  }

  public void addState(int st) {
    states.add(st);
  }

  public int[] getPopulations() {
    int[] ret = new int[getHighestState() + 1];
    for (int r = 0; r < getHeight(); r++) {
      for (int c = 0; c < getWidth(); c++) {
        ret[getState(r, c)]++;
      }
    }
    return ret;
  }

  public String[] getParams() {
    return getCell(0, 0).getParams();
  }

  public void setParam(String param, double value) {
    for (int r = 0; r < getHeight(); r++) {
      for (int c = 0; c < getWidth(); c++) {
        getCell(r, c).setParam(param, value);
      }
    }
  }

  abstract Cell[] getNeighbors(int r, int c);

  /**
   * Returns the 8 neighbors of the cell at r,c starting with North and rotating clockwise Acts as
   * though the grid is toroidal
   *
   * @param r
   * @param c
   * @return
   */
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

  public void setRandomGrid(String className, Map<String, Double> paramMap, double[] stateChances,
      int rows, int cols) throws ClassNotFoundException {
    ArrayList<ArrayList<Cell>> ret = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      ArrayList<Cell> row = new ArrayList<>();
      for (int j = 0; j < cols; j++) {
        row.add(getRandomCell(className, paramMap, stateChances));
      }
      ret.add(row);
    }
    grid = ret;
  }

  public static Cell getRandomCell(String className, Map<String, Double> paramMap,
      double[] stateChances) throws ClassNotFoundException {
    Class cellClass = null;
    Cell cell = null;
    try {
      cellClass = Class.forName(SIMULATION_PREFIX + className);
      cell = (Cell) (cellClass.getConstructor().newInstance());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
        NoSuchMethodException | InvocationTargetException e) {
      throw new InvalidCellException(e);
    }
    for (String param : paramMap.keySet()) {
      cell.setParam(param, paramMap.get(param));
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

  public int getWidth() {
    return grid.get(0).size();
  }

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

  public int getState(int r, int c) {
    return grid.get(r).get(c).getState();
  }

  public void placeCell(int r, int c, Cell cell) { //FIXME r/c convention flipped here?
    while (c >= grid.size()) {
      grid.add(new ArrayList<>());
    }
    while (r >= grid.get(c).size()) {
      grid.get(c).add(cell);
    }
    grid.get(c).set(r, cell);
  }
}
