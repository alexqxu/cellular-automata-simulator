package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Grid {
    private ArrayList<ArrayList<Cell>> grid;

    public void update(){
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                int[] neighbors = getNeighbors(i, j);
                grid.get(i).get(j).planUpdate(neighbors);
            }
        }
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                grid.get(i).get(j).update();
            }
        }
    }


    //FIXME grid needs to be toroidal for some cells (prey, conway) but empty boundaries for others (fire)
    /**
     * Returns the neighbors of the cell at r,c in North-East-South-West order.
     * Acts as though the grid is toroidal
     * @param r
     * @param c
     * @return
     */
    private int[] getNeighbors(int r, int c){
        int[] ret = new int[4];
        if (r == 0) {
            ret[0] = grid.get(grid.size()-1).get(c).getState();
        } else {
            ret[0] = grid.get(r-1).get(c).getState();
        }
        if (c == grid.get(r).size()) {
            ret[1] = grid.get(r).get(0).getState();
        } else {
            ret[1] = grid.get(r).get(c+1).getState();
        }
        if (r == grid.size()) {
            ret[2] = grid.get(0).get(c).getState();
        } else {
            ret[2] = grid.get(r+1).get(c).getState();
        }
        if (c == 0) {
            ret[3] = grid.get(r).get(grid.get(r).size()-1).getState();
        } else {
            ret[3] = grid.get(r).get(c-1).getState();
        }
        return ret;
    }

    private int[] getNeighbors(Cell cell) {
        for (int r = 0; r < grid.size(); r++) {
            int c = grid.get(r).indexOf(cell);
            if (c != -1) {
                return getNeighbors(r, c);
            }
        }
        throw new RuntimeException("cell wasn't in the grid");
    }

    public Color getColor(int r, int c) {
        return grid.get(r).get(c).getColor();
    }

    public Color[][] getColorGrid() {
        Color[][] ret = new Color[grid.size()][grid.get(0).size()];
        for (int r = 0; r < ret.length; r++) {
            for (int c = 0; c < ret[c].length; c++) {
                ret[r][c] = getColor(r,c);
            }
        }
        return ret;
    }

    public void placeCell(int r, int c, Cell cell) {
        grid.get(r).set(c,cell);
    }
}
