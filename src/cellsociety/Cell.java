package cellsociety;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Queue;

public abstract class Cell {
    protected int state;
    protected int nextState;
    protected HashMap<Integer, Color> colorMap;
    protected HashMap<String, Double> paramMap;
    protected int defaultEdge = -1;

    public Color getColor() {
        return colorMap.get(state);
    }

    abstract void planUpdate(int[] neighbors, Queue<Cell> emptyQueue);

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

    public void setState(int stat) {
        state = stat;
    }

    public int getDefaultEdge(){
        return defaultEdge;
    }

}
