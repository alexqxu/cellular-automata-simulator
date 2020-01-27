package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Cell {
    protected int state;
    protected int nextState;
    protected HashMap<Integer, Color> colorMap;
    protected HashMap<String, Double> paramMap;

    public Color getColor() {
        return colorMap.get(state);
    }

    abstract void planUpdate(int[] neighbors);

    public void update() {
        if (nextState == -1) {
            throw new RuntimeException("cell state is -1 so something terrible has happened");
        }
        state = nextState;
        nextState = -1;
    }

    public void setStateColor(int state, Color color) {
        colorMap.put(state, color);
    }

    public void setParam(String param, double value){
        paramMap.put(param, value);
    }

    public double getParam(String param) {
        return paramMap.get(param);
    }

    public int getState() {
        return state;
    }

}
