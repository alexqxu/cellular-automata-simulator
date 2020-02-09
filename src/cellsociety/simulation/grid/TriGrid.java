package cellsociety.simulation.grid;

import cellsociety.simulation.cell.Cell;

public class TriGrid extends Grid {

  @Override
  public Cell[] getNeighbors(int r, int c) {
    int[] dr;
    int[] dc;
    if ((r + c) % 2 == 0) {
      dr = new int[]{-1, -1, 0, 0, 1, 1, 1, 1, 1, 0, 0, -1};
      dc = new int[]{0, 1, 1, 2, 2, 1, 0, -1, -2, -2, -1, -1};
    } else {
      dr = new int[]{-1, -1, -1, 0, 0, 1, 1, 1, 0, 0, -1, -1};
      dc = new int[]{0, 1, 2, 2, 1, 1, 0, -1, -1, -2, -2, -1};
    }
    return getSpecificNeighbors(r, c, dr, dc);
  }

  @Override
  protected boolean padGrid() {
    boolean ret = super.padGrid();
    return (ret || checkSides());
  }

  private int[] rotate(int[] arr) {
    int[] ret = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      ret[i] = arr[(i + arr.length / 2) % arr.length];
    }
    return ret;
  }

  @Override
  public void placeCell(int c, int r, Cell cell) {
    super.placeCell(c, r, cell);
    cell.setPointUp((c + r) % 2 == 0);
  }

}
