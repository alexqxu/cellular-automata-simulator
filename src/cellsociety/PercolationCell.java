package cellsociety;

import javafx.scene.paint.Color;

import java.util.Queue;

public class PercolationCell extends Cell {
    public PercolationCell() {
        super();
        setStateColor(0, Color.BLACK); //Blocked
        setStateColor(1, Color.WHITE); //Open
        setStateColor(2, Color.BLUE); //Percolated
    }

    @Override
    void planUpdate(int[] neighbors, Queue<Cell> emptyQueue) {
        if (getState() == 1) {
            for (int i = 0; i < neighbors.length; i += 2) {
                if (neighbors[i] == 2) {
                    nextState = 2;
                }
            }
        }
        if (nextState == -1) {
            nextState = getState();
        }
    }
}
