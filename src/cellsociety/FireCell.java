package cellsociety;

import javafx.scene.paint.Color;

import java.util.Random;

public class FireCell extends Cell {
    public FireCell() {
        super();
        defaultEdge = 0;
        setStateColor(0, Color.YELLOW); //Empty
        setStateColor(1, Color.GREEN); //Tree
        setStateColor(2, Color.ORANGE); //Burning
    }

    public FireCell(double probCatch) {
        this();
        setParam("probCatch", probCatch);
    }

    @Override
    void planUpdate(int[] neighbors) {
        if (getState() == 0) {
            nextState = 0;
        }
        if (getState() == 2) {
            nextState = 0;
        }
        if (getState() == 1) {
            boolean canBurn = false;
            for (int i : neighbors) {
                if (i == 2) canBurn = true;
            }
            Random rand = new Random();
            if (canBurn && rand.nextDouble()<getParam("probCatch")) {
                nextState = 2;
            } else {
                nextState = 0;
            }
        }
    }
}
