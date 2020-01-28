package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Queue;

public class WaTorCell extends Cell {
    private int reproductionTimer;

    public WaTorCell() {
        super();
        setStateColor(0, Color.LIGHTBLUE); //Water
        setStateColor(1, Color.GREEN); //Fish
        setStateColor(2, Color.BLUE); //Shark
    }

    //FIXME many sharks will try to move into the same space (see below) - maybe move each on their own update?
    /*
    00000
    02220
    02120
    02220
    00000
    All sharks want to move into the fish space
     */

    @Override
    void planUpdate(Cell[] neighbors, Queue<Cell> emptyQueue) {
        ArrayList<Cell> open = new ArrayList<>();
        ArrayList<Cell> fish = new ArrayList<>();
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i].getState() == 0) open.add(neighbors[i]);
            if (neighbors[i].getState() == 1) fish.add(neighbors[i]);
        }
        if (getState() == 1) {

        }
    }
}
