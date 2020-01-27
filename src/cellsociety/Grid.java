package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Grid {
    private ArrayList<ArrayList<Cell>> grid;

    public void update(){
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                ArrayList<Cell> neighbors = getNeighbors(i, j);
                grid.get(i).get(j).planUpdate(neighbors);
            }
        }
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                grid.get(i).get(j).update();
            }
        }
    }

    /**
     * Returns the neighbors of the cell at r,c in North-East-South-West order.
     * Acts as though the grid is toroidal
     * @param r
     * @param c
     * @return
     */
    public ArrayList<Cell> getNeighbors(int r, int c){
        ArrayList<Cell> ret = new ArrayList<>();
        if (r == 0) {
            ret.add(grid.get(grid.size()-1).get(c));
        } else {
            ret.add(grid.get(r-1).get(c));
        }
        if (c == grid.get(r).size()) {
            ret.add(grid.get(r).get(0));
        } else {
            ret.add(grid.get(r).get(c+1));
        }
        if (r == grid.size()) {
            ret.add(grid.get(0).get(c));
        } else {
            ret.add(grid.get(r+1).get(c));
        }
        if (c == 0) {
            ret.add(grid.get(r).get(grid.get(r).size()-1));
        } else {
            ret.add(grid.get(r).get(c-1));
        }
        return ret;
    }

    public ArrayList<Cell> getNeighbors(Cell cell) {
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
