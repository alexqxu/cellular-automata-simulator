package cellsociety;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Cell {
    protected int state;
    protected int nextState;
    protected Map<Integer, Color> colorMap = new HashMap<>();
    protected Map<String, Double> paramMap = new HashMap<>();
    protected int defaultEdge = -1;

    public Color getColor() {
        return colorMap.get(state);
    }

    abstract void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue);

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

    public void setParam(String param, double value) {
        paramMap.put(param, value);
    }

    public double getParam(String param) {
        Double ret = paramMap.get(param);
        if (ret == null) {
            throw new RuntimeException("param ("+param+") asked for but not set.");
        }
        return ret;
    }

    public boolean isEmpty() {
        return state == 0;
    }

    public int getState() {
        return state;
    }

    public void setState(int stat) {
        this.state = stat;
    }

    public void setNextState(int state) {
        nextState = state;
    }

    public int getDefaultEdge() {
        return defaultEdge;
    }

    @Override
    public String toString() {
        return ""+getState();
    }

}
