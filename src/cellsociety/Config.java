package cellsociety;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Reads an XML file and sets up the Grid.
 * @author Alex Xu aqx
 */
public class Config {
    //public static final String XML_CONFIG_FILEPATH = "GameofLife.xml";
    //private Grid myGrid;

    private String myFilePath;
    private File xmlFile;
    private Document doc;


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