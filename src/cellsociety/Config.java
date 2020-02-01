package cellsociety;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Reads an XML file and sets up the Grid.
 * @author Alex Xu aqx
 */
public class Config {
    //public static final String XML_CONFIG_FILEPATH = "GameofLife.xml"; //Delete Later.
    private Grid myGrid;

    private String myFilePath;
    private File xmlFile;
    private Document doc;

    private String myTitle;
    private String myAuthor;
    private String myWidth;
    private String myHeight;
    private Map<String, Color> myStates;
    private String defaultState;
    private int mySpeed;

    /**
     * Constructor for the Config object. Sets the filepath and sets up the documentBuilder.
     * @param filePath
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Config(String filePath) throws ParserConfigurationException, SAXException, IOException {
        myFilePath = filePath;
        setupDocument();
    }

    private void setupDocument() throws IOException, SAXException, ParserConfigurationException {
        xmlFile = new File(myFilePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();                                   //optional, might remove later.
    }

    public String getTitle(){
        return myTitle;
    }
    public String getAuthor(){
        return myAuthor;
    }
    public String getWidth(){
        return myWidth;
    }
    public String getHeight(){
        return myHeight;
    }
    public Map<String, Color> getStates(){
        return myStates;
    }
    public String getDefaultState(){
        return defaultState;
    }
    public int getSpeed(){
        return mySpeed;
    }


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