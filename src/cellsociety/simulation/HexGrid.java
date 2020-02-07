package cellsociety.simulation;

public class HexGrid extends Grid {

  @Override
  public Cell[] getNeighbors(int r, int c) {
    int[] dr = {-1, 0, 1, 1, 1, 0};
    int[] dc = {0, 1, 1, 0, -1, -1};
    return getSpecificNeighbors(r, c, dr, dc);
  }

}
