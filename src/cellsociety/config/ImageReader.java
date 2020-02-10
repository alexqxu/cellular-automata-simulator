package cellsociety.config;

import cellsociety.simulation.cell.Cell;
import cellsociety.simulation.cell.RPSCell;
import cellsociety.simulation.grid.Grid;
import cellsociety.simulation.grid.RectGrid;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *Reads Images and Creates a Grid based on Image.
 */
public class ImageReader {
    private File myFile;
    private BufferedImage myImage;
    private int imageWidth;
    private int imageHeight;
    private Grid myGrid;

    /**
     * Creates an Image
     * @param imageFile
     * @throws IOException
     */
    public ImageReader(File imageFile) throws IOException {
        myFile = imageFile;
        myImage = ImageIO.read(myFile);
        imageWidth = myImage.getWidth();
        imageHeight = myImage.getHeight();
    }

    /**
     * Creates a grid based on the image. Only works for RPSCell and Rectangular Grids.
     * @return the grid created
     */
    public Grid generateGrid(){
        myGrid = new RectGrid();

        for(int row = 0; row < imageHeight; row++){
            for(int column = 0; column < imageWidth; column++){
                int color = myImage.getRGB(column, row);
                Cell myCell = new RPSCell();
                myCell.setState(color);
            }
        }
        return myGrid;
    }
}
