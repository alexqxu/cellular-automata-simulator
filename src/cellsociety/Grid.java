package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Grid {
    private ArrayList<ArrayList<Cell>> grid;

    public void update() {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                int[] neighbors = getNeighbors(i, j);
                grid.get(i).get(j).planUpdate(neighbors, getEmptyQueue());
            }
        }
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                grid.get(i).get(j).update();
            }
        }
    }


    //FIXME grid needs to be toroidal for some cells (prey, conway) but empty boundaries for others (fire)
    //FIXME some cells need 8 neighbors (like conway)

    /**
     * Returns the 8 neighbors of the cell at r,c starting with North and rotating clockwise
     * Acts as though the grid is toroidal
     *
     * @param r
     * @param c
     * @return
     */
    private int[] getNeighbors(int r, int c) {
        int[] ret = new int[8];
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};
        if (getCell(r, c).getDefaultEdge() == -1) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = grid.get((r + dr[i] + getHeight()) % getHeight()).get((c + dc[i] + getWidth()) % getWidth()).getState();
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                try {
                    ret[i] = grid.get(r+dr[i]).get(c+dc[i]).getState();
                } catch (IndexOutOfBoundsException e) {
                    ret[i] = getCell(r, c).getDefaultEdge();
                }
            }
        }
        return ret;
    }

    private int getWidth() {
        return grid.get(0).size();
    }

    private int getHeight() {
        return grid.size();
    }

    private Queue<Cell> getEmptyQueue() {
        Queue<Cell> ret = new LinkedList<>();
        for (ArrayList<Cell> row: grid) {
            for (Cell cell: row) {
                if (cell.getState()==0) {
                    ret.add(cell);
                }
            }
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

    private Cell getCell(int r, int c) {
        return grid.get(r).get(c);
    }

    public Color getColor(int r, int c) {
        return grid.get(r).get(c).getColor();
    }

    public Color[][] getColorGrid() {
        Color[][] ret = new Color[grid.size()][grid.get(0).size()];
        for (int r = 0; r < ret.length; r++) {
            for (int c = 0; c < ret[c].length; c++) {
                ret[r][c] = getColor(r, c);
            }
        }
        return ret;
    }

    public void placeCell(int r, int c, Cell cell) {
        grid.get(r).set(c, cell);
    }
}
