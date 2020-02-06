package cellsociety.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import javafx.scene.paint.Color;

public class WaTorCell extends Cell {

  public static final String FISH_BREED_TIME = "fishBreedTime";
  public static final String SHARK_BREED_TIME = "sharkBreedTime";
  public static final String FISH_FEED_ENERGY = "fishFeedEnergy";
  public static final String SHARK_START_ENERGY = "sharkStartEnergy";
  private static final String reproductionTimer = "reproductionTimer";
  private static final String energy = "energy";

  public WaTorCell() {
    super();
    addStates(new int[]{0,1,2});
  }

  @Override
  protected void setParams() {
    params = new String[]{FISH_BREED_TIME, SHARK_BREED_TIME, FISH_FEED_ENERGY, SHARK_START_ENERGY};
  }

  @Override
  void planUpdate(Cell[] neighbors, LinkedList<Cell> updatedQueue) {
    if (!updatedQueue.contains(this)) {
      return;
    }
    updatedQueue.remove(this);
    ArrayList<Cell> open = new ArrayList<>();
    ArrayList<Cell> fish = new ArrayList<>();
    for (int i = 0; i < neighbors.length; i += 2) {
      if (neighbors[i].getState() == 0) {
        open.add(neighbors[i]);
      }
      if (neighbors[i].getState() == 1) {
        fish.add(neighbors[i]);
      }
    }
    setParam(reproductionTimer, getParam(reproductionTimer) - 1);
    if (state == 1) {
      fishPlanUpdate(updatedQueue, open);
    }
    if (state == 2) {
      setParam(energy, getParam(energy) - 1);
      if (getParam(energy) <= 0) {
        nextState = 0;
        return;
      }
      sharkPlanUpdate(updatedQueue, open, fish);
    }
    if (state == 0 && nextState == -1) {
      nextState = 0;
    }
  }

  private void sharkPlanUpdate(LinkedList<Cell> updatedQueue, ArrayList<Cell> open,
      ArrayList<Cell> fish) {
    Random rand = new Random();
    WaTorCell movedTo = null;
    if (fish.size() > 0 || open.size() > 0) {
      if (fish.size() > 0) {
        int nextLoc = rand.nextInt(fish.size());
        movedTo = (WaTorCell) fish.get(nextLoc);
      } else {
        int nextLoc = rand.nextInt(open.size());
        movedTo = (WaTorCell) open.get(nextLoc);
      }
      updatedQueue.remove(movedTo);
      if (movedTo.nextState > 1) {
        nextState = state;
        return;
      }
      if (movedTo.state == 1) {
        setParam(energy, getParam(energy) + getParam(FISH_FEED_ENERGY));
      }
      swap(movedTo);
      this.nextState = 0;
      if (movedTo.getParam(reproductionTimer) <= 0) {
        nextState = 2;
        setParam(energy, getParam(SHARK_START_ENERGY));
        setParam(reproductionTimer, getParam(SHARK_BREED_TIME));
        movedTo.setParam(reproductionTimer, getParam(SHARK_START_ENERGY));
      }
      return;
    }
    nextState = 2;
  }

  private void fishPlanUpdate(LinkedList<Cell> updatedQueue, ArrayList<Cell> open) {
    Random rand = new Random();
    WaTorCell movedTo = null;
    if (open.size() > 0) {
      int nextLoc = rand.nextInt(open.size());
      movedTo = (WaTorCell) open.get(nextLoc);
      updatedQueue.remove(movedTo);
      if (movedTo.nextState > 0) {
        nextState = state;
        return;
      }
      swap(movedTo);
      if (movedTo.getParam(reproductionTimer) <= 0) {
        nextState = 1;
        movedTo.nextState = 1;
        setParam(reproductionTimer, getParam(FISH_BREED_TIME));
        movedTo.setParam(reproductionTimer, getParam(FISH_BREED_TIME));
      }
      return;
    }
    nextState = 1;
  }

  @Override
  public void update() {
    super.update();
    nextState = -1;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public void setState(int stat) {
    super.setState(stat);
    setParam(reproductionTimer, 0);
    setParam(energy, 0);
    if (stat == 1) {
      setParam(reproductionTimer, getParam(FISH_BREED_TIME));
    }
    if (stat == 2) {
      setParam(energy, getParam(SHARK_START_ENERGY));
      setParam(reproductionTimer, getParam(SHARK_BREED_TIME));
    }
  }
}
