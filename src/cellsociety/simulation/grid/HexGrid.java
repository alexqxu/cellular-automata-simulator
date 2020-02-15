package cellsociety.simulation.grid;

import cellsociety.simulation.cell.Cell;

public class HexGrid extends Grid {

  @Override
  protected Cell[] getNeighbors(int r, int c) {
    int[] dr;
    int[] dc;
    if (c % 2 == 0) {
      dr = new int[]{-1, 0, 1, 1, 1, 0};
    } else {
      dr = new int[]{-1, -1, 0, 1, 0, -1};
    }
    dc = new int[]{0, 1, 1, 0, -1, -1};

    return getSpecificNeighbors(r, c, dr, dc);
  }

}
