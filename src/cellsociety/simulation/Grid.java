package cellsociety.simulation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public abstract class Grid {

  public static final String SIMULATION_PREFIX = "cellsociety.simulation.";
  protected ArrayList<ArrayList<Cell>> grid;

  public Grid() {
    grid = new ArrayList<ArrayList<Cell>>();
  }

  public void update() {
    LinkedList<Cell> emptyQueue = getEmptyQueue();
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(i).size(); j++) {
        Cell[] neighbors = getNeighbors(i, j);
        grid.get(i).get(j).planUpdate(neighbors, emptyQueue);
      }
    }
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(i).size(); j++) {
        grid.get(i).get(j).update();
      }
    }
  }

  public void incrementCellState(int r, int c) {
    grid.get(r).get(c).incrementState();
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
    if (getCell(r, c).getDefaultEdge() == -1) {
      for (int i = 0; i < ret.length; i++) {
        ret[i] = grid.get((r + dr[i] + getHeight()) % getHeight())
            .get((c + dc[i] + getWidth()) % getWidth());
      }
    } else {
      for (int i = 0; i < ret.length; i++) {
        try {
          ret[i] = grid.get(r + dr[i]).get(c + dc[i]);
        } catch (IndexOutOfBoundsException e) {
          Cell cell = null;
          try {
            cell = getCell(r, c).getClass().getConstructor().newInstance();
          } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
          }
          cell.setState(getCell(r, c).getDefaultEdge());
          ret[i] = cell;
        }
      }
    }
    return ret;
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
      int rows, int cols) {
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
      double[] stateChances) {
    Class cellClass = null;
    Cell cell = null;
    try {
      cellClass = Class.forName(SIMULATION_PREFIX + className);
      cell = (Cell) (cellClass.getConstructor().newInstance());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
        NoSuchMethodException | InvocationTargetException e) {
      cell = new FireCell(); //FIXME this should never happen bc error handling in cellsociety.config
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

  public void placeCell(int c, int r, Cell cell) { //FIXME r/c convention flipped here?
    while (c >= grid.size()) {
      grid.add(new ArrayList<>());
    }
    while (r >= grid.get(c).size()) {
      grid.get(c).add(cell);
    }
    grid.get(c).set(r, cell);
  }
}
