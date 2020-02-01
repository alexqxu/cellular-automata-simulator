package cellsociety;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class WaTorCell extends Cell {
    private double reproductionTimer;
    private double energy;
    private WaTorCell movedTo;

    public WaTorCell() {
        super();
        setStateColor(0, Color.BLUE); //Water
        setStateColor(1, Color.GREEN); //Fish
        setStateColor(2, Color.YELLOW); //Shark
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
    void planUpdate(Cell[] neighbors, LinkedList<Cell> emptyQueue) {
        if (!emptyQueue.contains(this)) return;
        emptyQueue.remove(this);
        ArrayList<Cell> open = new ArrayList<>();
        ArrayList<Cell> fish = new ArrayList<>();
        for (int i = 0; i < neighbors.length; i+=2) {
            if (neighbors[i].getState() == 0) open.add(neighbors[i]);
            if (neighbors[i].getState() == 1) fish.add(neighbors[i]);
        }
        reproductionTimer--;
        if (state == 1) {
            fishPlanUpdate(emptyQueue, open);
        }
        if (state == 2) {
            if (--energy <= 0) {
                nextState = 0;
                return;
            }
            sharkPlanUpdate(emptyQueue, open, fish);
        }
        if (state == 0 && nextState == -1) nextState = 0;
    }

    private void sharkPlanUpdate(LinkedList<Cell> emptyQueue, ArrayList<Cell> open, ArrayList<Cell> fish) {
        Random rand = new Random();
        if (fish.size() > 0 || open.size()>0) {
            if (fish.size() > 0) {
                int nextLoc = rand.nextInt(fish.size());
                movedTo = (WaTorCell) fish.get(nextLoc);
            } else {
                int nextLoc = rand.nextInt(open.size());
                movedTo = (WaTorCell) open.get(nextLoc);
            }
            emptyQueue.remove(movedTo);
            if (movedTo.nextState > 1) {
                nextState = state;
                return;
            }
            if (movedTo.state == 1) {
                energy += getParam("fishFeedEnergy");
            }
            swap(this, movedTo);
            this.nextState = 0;
            if (movedTo.reproductionTimer <= 0) {
                nextState = 2;
                energy = getParam("sharkStartEnergy");
                reproductionTimer = getParam("sharkBreedTime");
                movedTo.reproductionTimer = getParam("sharkBreedTime");
            }
            return;
        }
        nextState = 2;
    }

    private void fishPlanUpdate(LinkedList<Cell> emptyQueue, ArrayList<Cell> open) {
        Random rand = new Random();
        if (open.size() > 0) {
            int nextLoc = rand.nextInt(open.size());
            movedTo = (WaTorCell) open.get(nextLoc);
            emptyQueue.remove(movedTo);
            if (movedTo.nextState > 0) {
                nextState = state;
                return;
            }
            swap(movedTo, this);
            if (movedTo.reproductionTimer <= 0) {
                nextState = 1;
                movedTo.nextState = 1;
                reproductionTimer = getParam("fishBreedTime");
                movedTo.reproductionTimer = getParam("fishBreedTime");
            }
            return;
        }
        nextState = 1;
    }

    @Override
    public void update() {
        super.update();
        movedTo = null;
        nextState = -1;
    }

    private void swap(WaTorCell a, WaTorCell b) {
        WaTorCell temp = new WaTorCell();
        temp.reproductionTimer = a.reproductionTimer;
        temp.energy = a.energy;
        a.reproductionTimer = b.reproductionTimer;
        a.energy = b.energy;
        b.reproductionTimer = temp.reproductionTimer;
        b.energy = temp.energy;

        a.nextState = b.state;
        b.nextState = a.state;
    }

    @Override
    public boolean isEmpty() {
        return true;
        //return state == 0 || state == 1;
    }

    @Override
    public void setState(int stat) {
        super.setState(stat);
        if (stat == 1) {
            reproductionTimer = getParam("fishBreedTime");
        }
        if (stat == 2) {
            energy = getParam("sharkStartEnergy");
            reproductionTimer = getParam("sharkBreedTime");
        }
    }
}
