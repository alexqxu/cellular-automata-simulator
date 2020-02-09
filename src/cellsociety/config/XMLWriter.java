package cellsociety.config;

import cellsociety.simulation.grid.Grid;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

/**
 * Responsible for Saving a user-created Simulation into an XML Config file
 * @Author Alex Xu, aqx
 */
public class XMLWriter {
    private static final String ROOT_NODE_NAME = "Simulation";

    private Grid myGrid;
    private Config myConfig;
    private Document myDocument;

    /**
     * XMLWriter constructor. Takes a Grid object and a Config object as parameters.
     * @param config
     * @param grid
     */
    public XMLWriter(Config config, Grid grid) throws ParserConfigurationException {
        myGrid = grid;
        myConfig = config;
        setupDocument();
    }

    /**
     * Saves an XML file at the given filepath
     * @param filepath
     */
    public void saveXML(String filepath) throws TransformerException {
        addNodes();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(myDocument);
        StreamResult streamResult = new StreamResult(new File(filepath));
        transformer.transform(domSource, streamResult);
    }

    private void setupDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        myDocument = documentBuilder.newDocument();
    }

    private void addNodes(){
        Element root = myDocument.createElement(ROOT_NODE_NAME);
        myDocument.appendChild(root);
        root.appendChild(getConfigInfo());
        root.appendChild(getCellsInfo());
    }

    private Node getConfigInfo(){
        Element configInfo = myDocument.createElement(Config.CONFIG_NODE_NAME);
        configInfo.appendChild(createEndNode(Config.TITLE_NODE_NAME, myConfig.getTitle()));
        configInfo.appendChild(createEndNode(Config.AUTHOR_NODE_NAME, myConfig.getAuthor()));
        configInfo.appendChild(createEndNode(Config.SHAPE_NODE_NAME, myConfig.getShape()));
        configInfo.appendChild(getDimensionsInfo());
        configInfo.appendChild(getSpecialParametersInfo());
        return configInfo;
    }

    private Node getCellsInfo(){
        Element cellsInfo = myDocument.createElement(Config.CELLS_NODE_NAME);

        return cellsInfo;
    }

    private Node getDimensionsInfo(){
        Element dimensionsInfo = myDocument.createElement(Config.DIMENSIONS_NODE_NAME);
        dimensionsInfo.appendChild(createEndNode(Config.WIDTH_NODE_NAME, ""+myConfig.getWidth()));
        dimensionsInfo.appendChild(createEndNode(Config.HEIGHT_NODE_NAME, ""+myConfig.getHeight()));
        dimensionsInfo.appendChild(createEndNode(Config.SPEED_NODE_NAME, ""+myConfig.getSpeed()));
        return dimensionsInfo;
    }

    //FIXME: Import global variables?
    private Node getSpecialParametersInfo(){
        Element specialParametersNode = myDocument.createElement(Config.PARAMETERS_NODE_NAME);
        //FIXME: Loop through the Parameter Map
        //specialParametersNode.appendChild(createEndNode(Config.SINGLE_PARAMETER_NODE_NAME, myGrid.getParam(), Config.PARAMETER_NAME_ATTRIBUTE_NAME, cellsociety.simulation.cell.FireCell.PROB_CATCH));

        return specialParametersNode;
    }

    private Node createEndNode(String name, String value){
        Element node = myDocument.createElement(name);
        node.appendChild(myDocument.createTextNode(value));
        return node;
    }

    //FIXME: May need to refactor later to call the other constructor instead of duplicate code;
    private Node createEndNode(String name, String value, String attributeName, String attributeValue){
        Element node = myDocument.createElement(name);
        node.setAttribute(attributeName, attributeValue);
        node.appendChild(myDocument.createTextNode(value));
        return node;
    }
}