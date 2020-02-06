package cellsociety.visualizer;

import cellsociety.Config;
import java.lang.reflect.InvocationTargetException;
import javafx.scene.Node;

public class HexVisualizer extends Visualizer {

  public HexVisualizer(Config config)
      throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    super(config);
  }

  @Override
  protected Node instantiateCellGrid() {
    return null;
  }
}
