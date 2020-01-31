package cellsociety;

import java.io.FileNotFoundException;

/**
 * Reads an XML file and sets up the Grid.
 * @author Alex Xu aqx
 */
public class Config {
    //public static final String XML_CONFIG_FILEPATH = "GameofLife.xml";

    //private Grid myGrid;

    public Grid loadFile(String path){
        Grid newGrid = new Grid();
        return newGrid;
    }

    //Take file input in
    //private void readFile throws FileNotFoundException(String path){

    //}

    private Grid createGrid(){
        Grid newGrid = new Grid();
        return newGrid;
    }
}