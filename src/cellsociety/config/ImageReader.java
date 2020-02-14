package cellsociety.config;

import cellsociety.exceptions.InvalidImageException;
import cellsociety.simulation.cell.Cell;
import cellsociety.simulation.cell.RPSCell;
import cellsociety.simulation.grid.Grid;
import cellsociety.simulation.grid.RectGrid;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *This "extra" feature reads Images and Creates a Grid based on Image (currently not fully functional)
 * @author Alex Xu, aqx
 */
public class ImageReader {
    private File myFile;
    private BufferedImage myImage;
    private int imageWidth;
    private int imageHeight;
    private Grid myGrid;
    private Map<Integer, Color> myStates;

    /**
     * Creates an Image
     * @param imageFile image file that is passed in
     * @throws IOException
     */
    public ImageReader(File imageFile) throws InvalidImageException{
        myFile = imageFile;
        try {
            myImage = ImageIO.read(myFile);
            imageWidth = myImage.getWidth();
            imageHeight = myImage.getHeight();
        }catch(IOException e){
            throw new InvalidImageException(e);
        }
    }

    /**
     * Creates a grid based on the image. Only works for RPSCell and Rectangular Grids.
     * @return the grid created
     */
    public Grid generateGrid(){
        myGrid = new RectGrid();
        myStates = new HashMap<>();

        for(int row = 0; row < imageHeight; row++){
            for(int column = 0; column < imageWidth; column++){
                int color = myImage.getRGB(column, row);
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16;

                int stateValue = color*-1;
                System.out.println(stateValue);
                //int stateValue = Integer.parseInt(hex, 16);
                //System.out.println(stateValue);

                Cell myCell = new RPSCell();
                myCell.setState(stateValue);
                myGrid.placeCell(row, column, myCell);

                Color myColor = Color.rgb(red, green, blue);
                myStates.put(stateValue, myColor);
            }
        }
        return myGrid;
    }

    /**
     * Returns a mapping of states/colors for the visualizer
     * @return Map representing the mapping of colors to cell states
     */
    public Map<Integer, Color> getStates(){
        return myStates;
    }
}