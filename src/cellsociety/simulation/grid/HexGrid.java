package cellsociety.simulation.grid;

import cellsociety.simulation.cell.Cell;

/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Holds cells in a grid and calculates their neighbors as though they were in a hexagonal grid.
 * Uses "even-q" storage: The cells are stored in a grid, and even columns are shifted downwards to create the hexes.
 *
 * Assumptions: See Grid
 *
 * Dependencies: Grid, Cell
 */
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
