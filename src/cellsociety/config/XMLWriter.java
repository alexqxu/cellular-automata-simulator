package cellsociety.config;

import cellsociety.simulation.grid.Grid;
import javafx.scene.paint.Color;
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
    private static final String ROW_NUMBER_NAME = "numbr";

    private Grid myGrid;
    private Config myConfig;
    private Document myDocument;
    private String custom = "true";

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
        configInfo.appendChild(createEndNode(Config.BORDER_TYPE_NODE, ""+myConfig.getBorderType()));
        configInfo.appendChild(getDimensionsInfo());
        configInfo.appendChild(getSpecialParametersInfo());
        configInfo.appendChild(getStatesInfo());
        configInfo.appendChild(createEndNode(Config.CUSTOM_NODE_NAME, custom));
        return configInfo;
    }

    private Node getCellsInfo(){
        Element cellsInfo = myDocument.createElement(Config.CELLS_NODE_NAME);
        for(int r = 0; r<myGrid.getHeight(); r++){
            cellsInfo.appendChild(getRowNodes(r));
        }
        return cellsInfo;
    }

    private Node getRowNodes(int row){
        Element rowNode = myDocument.createElement(Config.ROW_NODE_NAME);
        rowNode.setAttribute(ROW_NUMBER_NAME, ""+row);
        for(int c = 0; c<myGrid.getWidth(); c++){
            rowNode.appendChild(createEndNode(Config.CELLS_NODE_NAME, ""+myGrid.getState(row, c)));
        }
        return rowNode;
    }

    private Node getDimensionsInfo(){
        Element dimensionsInfo = myDocument.createElement(Config.DIMENSIONS_NODE_NAME);
        dimensionsInfo.appendChild(createEndNode(Config.WIDTH_NODE_NAME, ""+myGrid.getWidth()));
        dimensionsInfo.appendChild(createEndNode(Config.HEIGHT_NODE_NAME, ""+myGrid.getHeight()));
        dimensionsInfo.appendChild(createEndNode(Config.SPEED_NODE_NAME, ""+myConfig.getSpeed()));
        return dimensionsInfo;
    }

    private Node getSpecialParametersInfo(){
        Element specialParametersNode = myDocument.createElement(Config.PARAMETERS_NODE_NAME);
        String[] parameters = myGrid.getParams();
        for(String p : parameters){
            specialParametersNode.appendChild(createEndNode(Config.SINGLE_PARAMETER_NODE_NAME, ""+myGrid.getParam(p), Config.PARAMETER_NAME_ATTRIBUTE_NAME, p));
        }
        return specialParametersNode;
    }

    private Node getStatesInfo(){
        Element statesInfoNode = myDocument.createElement(Config.STATES_NODE_NAME);
        statesInfoNode.appendChild(createEndNode(Config.DEFAULT_STATE_NODE_NAME, ""+myConfig.getDefaultState()));

        Map<Integer, Color> statesMap = myConfig.getStates();
        for(Integer state : statesMap.keySet()){
            statesInfoNode.appendChild(createStateNode(state, statesMap.get(state)));
        }
        return statesInfoNode;
    }

    private Node createStateNode(Integer id, Color color){
        Element stateNode = myDocument.createElement(Config.SINGLE_STATE_NODE_NAME);
        stateNode.appendChild(createEndNode(Config.STATE_ID_NODE_NAME, ""+id));
        stateNode.appendChild(createEndNode(Config.COLOR_NODE_NAME, ""+color));
        return stateNode;
    }

    private Node createEndNode(String name, String value){
        Element node = myDocument.createElement(name);
        node.appendChild(myDocument.createTextNode(value));
        return node;
    }

    //FIXME: May need to refactor later to call the other constructor instead of duplicate code
    private Node createEndNode(String name, String value, String attributeName, String attributeValue){
        Element node = myDocument.createElement(name);
        node.setAttribute(attributeName, attributeValue);
        node.appendChild(myDocument.createTextNode(value));
        return node;
    }
}