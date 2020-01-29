package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class PercolationCell extends Cell {
    public PercolationCell() {
        super();
        setStateColor(0, Color.BLACK); //Blocked
        setStateColor(1, Color.WHITE); //Open
        setStateColor(2, Color.BLUE); //Percolated
        defaultEdge = 0;
    }

    @Override
    void planUpdate(Cell[] neighbors, Queue<Cell> emptyQueue) {
        if (getState() == 1) {
            for (int i = 0; i < neighbors.length; i += 2) {
                if (neighbors[i].getState() == 2) {
                    nextState = 2;
                    return;
                }
            }
            nextState = 1;
        } else {
            if (state == 0) {
                nextState = 0;
            }
            if (state == 2) {
                nextState = 2;
            }
        }
    }
}
