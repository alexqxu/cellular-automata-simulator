package cellsociety;

import javafx.scene.paint.Color;

public class ConwayCell extends Cell {

    /**
     * Creates a cell for Conway's game of life
     *
     * 0 - Dead, Black
     * 1 - Alive, White
     */
    public ConwayCell(){
        super();
        setStateColor(0, Color.BLACK);
        setStateColor(1, Color.WHITE);
    }


    @Override
    void planUpdate(int[] neighbors) {
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
