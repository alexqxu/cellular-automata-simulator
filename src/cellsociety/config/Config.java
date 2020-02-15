package cellsociety.config;

import cellsociety.exceptions.*;
import cellsociety.simulation.grid.Grid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads an XML file and sets up the Grid based on it
 * Will throw exceptions if the file is not valid (not an XML file or not of the correct structure)
 * Depends on the XMLValidator working properly to validate XML Structure, and that GridFactory returns a Grid properly.
 * Example: The Visualizer creates a Config object with a File and retrieves a Grid object from the class.
 * @author Alex Xu aqx
 */
public class Config {

  public static final int INVALID_DIMENSION_THRESHOLD = 1;
  public static final String INVALID_DIMENSION_MESSAGE = "You have requested an invalid Simulation Size in XML: ";

  public static final String CONFIG_NODE_NAME = "ConfigInfo";
  public static final String CELLS_NODE_NAME = "Cells";
  public static final String TITLE_NODE_NAME = "Title";
  public static final String AUTHOR_NODE_NAME = "Author";
  public static final String PARAMETERS_NODE_NAME = "SpecialParameters";
  public static final String STATES_NODE_NAME = "States";
  public static final String SINGLE_PARAMETER_NODE_NAME = "Parameter";
  public static final String SINGLE_STATE_NODE_NAME = "State";
  public static final String PARAMETER_NAME_ATTRIBUTE_NAME = "name";
  public static final String ROW_NODE_NAME = "Row";
  public static final String STATE_ID_NODE_NAME = "ID";
  public static final String COLOR_NODE_NAME = "Color";
  public static final String DIMENSIONS_NODE_NAME = "Dimensions";
  public static final String SPEED_NODE_NAME = "Speed";
  public static final String WIDTH_NODE_NAME = "Width";
  public static final String HEIGHT_NODE_NAME = "Height";
  public static final String CELL_NODE_NAME = "Cell";
  public static final String SHAPE_NODE_NAME = "Shape";
  public static final String DEFAULT_STATE_NODE_NAME = "Default";
  public static final String CUSTOM_NODE_NAME = "Custom";
  public static final String BORDER_TYPE_NODE = "BorderType";
  public static final String MASK_NODE_NAME = "Mask";

  private String visualizerSuffix = "Visualizer";

  private File myFile;
  private Document doc;

  private Grid myGrid;
  private String myTitle;
  private String myAuthor;
  private String myShape;
  private boolean customRequested = false;
  private double mySpeed;
  private int myWidth;
  private int myHeight;
  private Map<Integer, Color> myStates;
  private Map<String, Double> myParameters;
  private int defaultState = 0;
  private int myBorderType = 0;
  private int[] myMask;

  private GridFactory myGridFactory;

  /**
   * Constructor for the Config object. Sets the file and sets up the documentBuilder. Then loads the file content.
   * @param file File object passed in, in XML format
   */
  public Config(File file) throws InvalidShapeException, InvalidGridException, InvalidCellException, InvalidFileException, InvalidXMLStructureException, InvalidImageException, InvalidDimensionsException{
    if(XMLValidator.validateXMLStructure(file)){
      myFile = file;
      setupDocument();
      loadFile();
    }
  }

  /**
   * Returns the update speed of the simulation, as defined within the initial config XML document.
   * @return speed of the simulation
   */
  public double getSpeed() {
    return mySpeed;
  }

  /**
   * Returns the Grid created
   * @return a grid object
   */
  public Grid getGrid() {
    return myGrid;
  }

  /**
   * Returns a string representing the type of shape/visualizer
   * @return String, representing visualizer class name
   */
  public String getVisualizer() {
    return myShape + visualizerSuffix;
  }

  /**
   * Returns color/state mappings.
   * @return Map with the color mappings
   */
  public Map<Integer, Color> getStates() {
    return myStates;
  }

  /**
   * Returns the title of the simulation
   * @return String, which represents the title.
   */
  public String getTitle(){
    return myTitle;
  }

  /**
   * Returns the author of the simulation
   * @return String, which represents the author of the simulation
   */
  public String getAuthor(){
    return myAuthor;
  }

  /**
   * Returns the shape of the simulation
   * @return String, which represents the shape requested in the XML
   */
  public String getShape(){
    return myShape;
  }

  /**
   * Returns the default state of the simulation requested
   * String, which represents the default state requested (a number)
   * @return
   */
  public String getDefaultState(){
    return ""+defaultState;
  }

  /**
   * Returns the border type of the simulation
   * @return String, which represents the borderType requested (a number)
   */
  public String getBorderType(){
    return ""+myBorderType;
  }

  /**
   * Returns the mask/neighborhood type requested of the simulation.
   * @return an integer array that represents the mask.
   */
  public int[] getMask(){
    return myMask;
  }

  /**
   * Based on the parameters set, creates a grid with a randomized configuration of CELLS (with XML read dimensions)
   * @throws InvalidCellException
   * @throws InvalidGridException
   */
  public void createRandomGrid(int width, int height) throws InvalidCellException, InvalidGridException{
    myGrid = myGridFactory.createRandomGrid(width, height);
  }

  /**
   * Create and set up the Grid based on stored information, and then return it.
   */
  private void loadFile() throws InvalidShapeException, InvalidGridException, InvalidCellException, InvalidDimensionsException{
    extractConfigInfo();
    myGridFactory = new GridFactory(doc, myTitle, myStates.keySet(), myParameters, myShape, myMask, myHeight, myWidth, customRequested, myBorderType);
    myGrid = myGridFactory.createGrid();
  }

  private void setupDocument()
          throws InvalidFileException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
      doc = builder.parse(myFile);
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new InvalidFileException(e);
    }
    doc.getDocumentElement().normalize();
  }

  /**
   * Extracts all information in the XML Document that lies within <ConfigInfo>.
   */
  private void extractConfigInfo() throws InvalidDimensionsException{
    NodeList configNodeList = doc.getElementsByTagName(CONFIG_NODE_NAME);
    Node configNode = configNodeList.item(0);

    if (configNode.getNodeType() == Node.ELEMENT_NODE) {
      Element configElement = (Element) configNode;
      extractTitle(configElement);
      extractAuthor(configElement);
      extractShape(configElement);
      extractBorderType(configElement);
      extractMask(configElement);
      extractDimensions(configElement);
      extractStates(configElement);
      extractParameters(configElement);
      extractCustom(configElement);
    }
  }

  private void extractParameters(Element configElement) {
    myParameters = new HashMap<>();

    Node parametersNode = configElement.getElementsByTagName(PARAMETERS_NODE_NAME).item(0);
    if (parametersNode.getNodeType() == Node.ELEMENT_NODE) {
      Element parametersElement = (Element) parametersNode;

      NodeList parametersNodeList = parametersElement.getElementsByTagName(SINGLE_PARAMETER_NODE_NAME);

      for (int i = 0; i < parametersNodeList.getLength(); i++) {
        Node singleParameterNode = parametersNodeList.item(i);
        if (singleParameterNode.getNodeType() == Node.ELEMENT_NODE) {
          Element singleParameterElement = (Element) singleParameterNode;
          String parameterName = singleParameterElement.getAttribute(PARAMETER_NAME_ATTRIBUTE_NAME);
          Double parameterValue = Double.valueOf(singleParameterElement.getTextContent());
          myParameters.put(parameterName, parameterValue);
        }
      }
    }
  }

  private void extractStates(Element startingElement) {
    myStates = new HashMap<>();

    Node statesNode = startingElement.getElementsByTagName(STATES_NODE_NAME).item(0);
    if (statesNode.getNodeType() == Node.ELEMENT_NODE) {
      Element statesElement = (Element) statesNode;
      extractDefaultState(statesElement);
      NodeList statesNodeList = statesElement.getElementsByTagName(SINGLE_STATE_NODE_NAME);

      for (int i = 0; i < statesNodeList.getLength(); i++) {
        Node singleStateNode = statesNodeList.item(i);
        if (singleStateNode.getNodeType() == Node.ELEMENT_NODE) {
          Element singleStateElement = (Element) singleStateNode;
          Integer stateID = Integer.valueOf(
                  singleStateElement.getElementsByTagName(STATE_ID_NODE_NAME).item(0).getTextContent());
          String stateColor = singleStateElement.getElementsByTagName(COLOR_NODE_NAME).item(0)
                  .getTextContent();
          myStates.put(stateID, Color.web(stateColor));
        }
      }
    }
  }

  private void extractDimensions(Element startingElement) throws InvalidDimensionsException {
    Node dimensionsNode = startingElement.getElementsByTagName(DIMENSIONS_NODE_NAME).item(0);
    if (dimensionsNode.getNodeType() == Node.ELEMENT_NODE) {
      Element dimensionsElement = (Element) dimensionsNode;
      extractHeight(dimensionsElement);
      extractWidth(dimensionsElement);
      extractSpeed(dimensionsElement);
      if(myHeight < INVALID_DIMENSION_THRESHOLD){
        throw new InvalidDimensionsException(INVALID_DIMENSION_MESSAGE, myHeight);
      }
      if(myWidth < INVALID_DIMENSION_THRESHOLD){
        throw new InvalidDimensionsException(INVALID_DIMENSION_MESSAGE, myWidth);
      }
    }
  }

  private void extractDefaultState(Element statesElement){
    defaultState = Integer.parseInt(extractElementValue(statesElement, DEFAULT_STATE_NODE_NAME));
  }

  private void extractTitle(Element startingElement) {
    myTitle = extractElementValue(startingElement, TITLE_NODE_NAME);
  }

  private void extractAuthor(Element startingElement) {
    myAuthor = extractElementValue(startingElement, AUTHOR_NODE_NAME);
  }

  private void extractShape(Element startingElement) {
    myShape = extractElementValue(startingElement, SHAPE_NODE_NAME);
  }

  private void extractBorderType(Element startingElement){
    myBorderType = Integer.parseInt(extractElementValue(startingElement, BORDER_TYPE_NODE).trim());
  }

  private void extractMask(Element startingElement){
    String[] maskStrings = extractElementValue(startingElement, MASK_NODE_NAME).split(" ");
    myMask = new int[maskStrings.length];
    for(int i = 0; i<maskStrings.length; i++){
      myMask[i] = Integer.parseInt(maskStrings[i]);
    }
  }

  private void extractCustom(Element startingElement){
    customRequested = Boolean.parseBoolean(extractElementValue(startingElement, CUSTOM_NODE_NAME));
  }

  private void extractSpeed(Element dimensionsElement) {
    mySpeed = Double.parseDouble(extractElementValue(dimensionsElement, SPEED_NODE_NAME).trim());
  }

  private void extractWidth(Element dimensionsElement) {
    myWidth = Integer.parseInt(extractElementValue(dimensionsElement, WIDTH_NODE_NAME).trim());
  }

  private void extractHeight(Element dimensionsElement) {
    myHeight = Integer.parseInt(extractElementValue(dimensionsElement, HEIGHT_NODE_NAME).trim());
  }

  private String extractElementValue(Element parentElement, String nodeName) {
    return parentElement.getElementsByTagName(nodeName).item(0).getTextContent();
  }
}