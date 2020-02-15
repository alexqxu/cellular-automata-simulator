package cellsociety.simulation.grid;

import cellsociety.simulation.cell.Cell;

public class RectGrid extends Grid {

  @Override
  protected Cell[] getNeighbors(int r, int c) {
    int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
    int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};
    return getSpecificNeighbors(r, c, dr, dc);
  }

}
