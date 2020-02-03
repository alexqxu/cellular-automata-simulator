package cellsociety;

import javafx.scene.paint.Color;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Grid {
    private ArrayList<ArrayList<Cell>> grid;

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

    /**
     * Returns the 8 neighbors of the cell at r,c starting with North and rotating clockwise
     * Acts as though the grid is toroidal
     *
     * @param r
     * @param c
     * @return
     */
    private Cell[] getNeighbors(int r, int c) {
        Cell[] ret = new Cell[8];
        int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};
        if (getCell(r, c).getDefaultEdge() == -1) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = grid.get((r + dr[i] + getHeight()) % getHeight()).get((c + dc[i] + getWidth()) % getWidth());
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                try {
                    ret[i] = grid.get(r+dr[i]).get(c+dc[i]);
                } catch (IndexOutOfBoundsException e) {
                    Cell cell = null;
                    try {
                        cell = getCell(r,c).getClass().getConstructor().newInstance();
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                    cell.setState(getCell(r,c).getDefaultEdge());
                    ret[i] = cell;
                }
            }
        }
        return ret;
    }

    public void setRandomGrid(String className, Map<String, Double> paramMap, double[] stateChances,
                                     int rows, int cols){
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

    public static Cell getRandomCell(String className, Map<String, Double> paramMap, double[] stateChances) {
        Class cellClass = null;
        Cell cell = null;
        try {
            cellClass = Class.forName("cellsociety."+className);
            cell = (Cell)(cellClass.getConstructor().newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        for (String param: paramMap.keySet()) {
            cell.setParam(param, paramMap.get(param));
        }
        double chanceSum = 0;
        for (int i = 0; i < stateChances.length; i++) {
            chanceSum += stateChances[i];
        }
        Random rand = new Random();
        double roll = rand.nextDouble()*chanceSum;
        int state = 0;
        for (int i = 0; i < stateChances.length; i++) {
            roll -= stateChances[i];
            if (roll <= 0){
                cell.setState(i);
                break;
            }
        }
        return cell;
    }

    private int getWidth() {
        return grid.get(0).size();
    }

    private int getHeight() {
        return grid.size();
    }

    private LinkedList<Cell> getEmptyQueue() {
        LinkedList<Cell> ret = new LinkedList<>();
        for (ArrayList<Cell> row: grid) {
            for (Cell cell: row) {
                if (cell.isEmpty()) {
                    ret.add(cell);
                }
            }
        }
        return ret;
    }

    private Cell[] getNeighbors(Cell cell) {
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
            for (int c = 0; c < ret[r].length; c++) {
                ret[r][c] = getColor(r, c);
            }
        }
        return ret;
    }

    public void placeCell(int r, int c, Cell cell) {
        while(r >= grid.size()){
            grid.add(new ArrayList<>());
        }
        while(c >= grid.get(r).size()){
            grid.get(r).add(cell);
        }
        grid.get(r).set(c, cell);
    }
}
