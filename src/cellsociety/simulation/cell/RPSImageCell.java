package cellsociety.simulation.cell;

import java.util.Queue;
import java.util.Random;

/**
 * @author Maverick Chung, mc608
 *
 * Purpose: Currently unused. Models rock-paper-scissors with an image. States are the decimal representations
 * of hex RGB values of pixels. If a random neighbor has a higher winning value, that value is absorbed by this cell.
 *
 * Assumptions: See Cell
 *
 * Dependencies: Cell
 */
public class RPSImageCell extends Cell {

  @Override
  protected void planUpdate(Cell[] neighbors, Queue<Cell> cellQueue) {
    Random rand = new Random();
    Cell cell = neighbors[rand.nextInt(neighbors.length)];
    int[] myColors = getColors(state);
    int[] otherColors = getColors(cell.state);
    int[] myHighs = findHighColor(myColors);
    int[] otherHighs = findLowColor(otherColors);
    if (RPSCell.rps(otherHighs[0], myHighs[0], 3)){
      myColors[myHighs[0]] = otherColors[otherHighs[1]];
      nextState = combineRGB(myColors);
    } else {
      nextState = state;
    }

  }

  private int[] getColors(int st){
    int r = (st/256)/256;
    int b = (st/256)%256;
    int g = st%256;
    return new int[]{r,g,b};
  }

  private int[] findHighColor(int[] colors) {
    int max = colors[0];
    int index = 0;
    for (int i = 1; i < colors.length; i++) {
      if (colors[i]>max) {
        max = colors[i];
        index = i;
      }
    }
    return new int[]{index, max};
  }

  private int[] findLowColor(int[] colors) {
    int min = colors[0];
    int index = 0;
    for (int i = 1; i < colors.length; i++) {
      if (colors[i]<min) {
        min = colors[i];
        index = i;
      }
    }
    return new int[]{index, min};
  }

  private int combineRGB(int[] rgb) {
    return rgb[0]*256*256+rgb[1]*256+rgb[2];
  }
}
