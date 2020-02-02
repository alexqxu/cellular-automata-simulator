package cellsociety;

import javafx.scene.paint.Color;

import java.util.LinkedList;

public class PercolationCell extends Cell {
    public PercolationCell() {
        super();
        setStateColor(0, Color.BLACK); //Blocked
        setStateColor(1, Color.WHITE); //Open
        setStateColor(2, Color.BLUE); //Percolated
        setStateColor(3, Color.ORANGE); //Edge, should never show on screen
        defaultEdge = 3;
    }

    @Override
    void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue) {
        if (neighbors[6].getState() == 3) neighbors[0].setState(2); //this is okay because cells with state 3 are dummy cells
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
