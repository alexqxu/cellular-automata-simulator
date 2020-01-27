package cellsociety;

import javafx.scene.paint.Color;

import java.util.Queue;

public class ConwayCell extends Cell {

    /**
     * Creates a cell for Conway's game of life
     *
     * 0 - Dead, White
     * 1 - Alive, Black
     */
    public ConwayCell(){
        super();
        System.out.println("Conway is currently nonfunctional! please don't use.");
        defaultEdge = -1;
        setStateColor(0, Color.WHITE);
        setStateColor(1, Color.BLACK);
    }


    @Override
    void planUpdate(int[] neighbors, Queue<Cell> emptyQueue) {
        int sum = 0;
        for (int i = 0; i < neighbors.length; i++) {
            sum += neighbors[i];
        }
        if (getState() == 0){
            if (sum == 3) {
                nextState = 1;
            } else {
                nextState = 0;
            }
        } else {
            if (sum == 2 || sum ==3) {
                nextState = 1;
            } else {
                nextState = 0;
            }
        }
    }
}
