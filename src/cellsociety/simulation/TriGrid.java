package cellsociety.simulation;

public class TriGrid extends Grid {

  @Override
  public Cell[] getNeighbors(int r, int c) {
    //FIXME need to flip/flop neighborhoods
    int[] dr;
    int[] dc;
    if ((r+c)%2==0){
      dr = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
      dc = new int[]{0, 1, 1, 1, 0, -1, -1, -1};
    } else {
      dr = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
      dc = new int[]{0, 1, 1, 1, 0, -1, -1, -1};
    }
    throw new RuntimeException("NONFUNCTIONAL DO NOT USE!");
    //return getSpecificNeighbors(r, c, dr, dc);
  }
}
