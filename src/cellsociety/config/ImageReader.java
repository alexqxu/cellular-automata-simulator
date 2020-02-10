package cellsociety.config;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;

/**
 *Reads Images and Creates a Grid based on Image.
 */
public class ImageReader {
    private File myFile;
    private BufferedImage myImage;

    public ImageReader(File imageFile) throws IOException {
        myFile = imageFile;
        myImage = ImageIO.read(myFile);
    }


}
