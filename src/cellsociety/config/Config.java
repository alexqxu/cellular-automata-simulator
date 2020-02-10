package cellsociety.config;

import cellsociety.exceptions.*;
import cellsociety.simulation.cell.Cell;
import cellsociety.simulation.grid.Grid;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads an XML file and sets up the Grid.
 *
 * @author Alex Xu aqx
 */
public class Config {
  public static final double RANDOM_GRID_VARIABLE_VALUE = 0.5;
  public static final double FIRST_RANDOM_GRID_VARIABLE_VALUE = 0.25;

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

  private String packagePrefixName = "cellsociety.simulation.";
  private String gridPrefixName = packagePrefixName+"grid.";
  private String cellPrefixName = packagePrefixName+"cell.";
  private String gridSuffix = "Grid";
  private String visualizerSuffix = "Visualizer";

  private String docSetUpConfirmationMessage = "---Document Setup Complete---";
  private String configSetUpConfirmationMessage = "---Config Info Load Complete---";
  private String gridConfirmationMessage = "---Grid Created---";

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
  private double[] randomGridVariables;
  private int[] myMask;

  /**
   * Constructor for the Config object. Sets the file and sets up the documentBuilder. Then loads the file content.
   *
   * @param file File object passed in, in XML format or Image
   */
  public Config(File file) throws InvalidShapeException, InvalidGridException, InvalidCellException, InvalidFileException, InvalidXMLStructureException, InvalidImageException{
    if(isImageFile(file)){
      ImageReader imageReader = new ImageReader(file);
      myGrid = imageReader.generateGrid();
    }
    else if(XMLValidator.validateXMLStructure(file)){
      myFile = file;
      setupDocument();
      System.out.println(docSetUpConfirmationMessage);
      loadFile();
    }
  }

  /**
   * Returns the update speed of the simulation, as defined within the initial config XML document.
   *
   * @return speed of the simulation
   */
  public double getSpeed() {
    return mySpeed;
  }

  /**
   * Returns the Grid created
   *
   * @return
   */
  public Grid getGrid() {
    return myGrid;
  }

  /**
   * Returns a string representing the type of shape/visualizer
   *
   * @return String, representing visualizer class name
   */
  public String getVisualizer() {
    return myShape + visualizerSuffix;
  }

  /**
   * Returns color/state mappings.
   * @return
   */
  public Map<Integer, Color> getStates() {
    return myStates;
  }

  public String getTitle(){
    return myTitle;
  }
  public String getAuthor(){
    return myAuthor;
  }
  public String getShape(){
    return myShape;
  }
  public String getDefaultState(){
    return ""+defaultState;
  }
  public String getBorderType(){
    return ""+myBorderType;
  }
  public int[] getMask(){
      return myMask;
  }

  /**
   * Based on the parameters set, creates a grid with a randomized configuration of CELLS
   * @throws InvalidCellException
   * @throws InvalidGridException
   * @throws InvalidShapeException
   */
  public void createRandomGrid(int width, int height)  throws InvalidCellException, InvalidGridException{
    Class gridClass = null;
    try {
      gridClass = Class.forName(gridPrefixName + myShape + gridSuffix);
    } catch (ClassNotFoundException e) {
      throw new InvalidShapeException(e);
    }
    try {
      myGrid = (Grid) (gridClass.getConstructor().newInstance());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidGridException(e);
    }
    try {
      myGrid.setRandomGrid(myTitle, myParameters, randomGridVariables, myBorderType, myMask, width, height);
    } catch (ClassNotFoundException e) {
      throw new InvalidCellException(e);
    }
    for (int i: myStates.keySet()) {
      myGrid.addState(i);
    }
  }

  /**
   * Based on the parameters set, creates a grid with a randomized configuration of CELLS (with XML read dimensions)
   * @throws InvalidCellException
   * @throws InvalidGridException
   * @throws InvalidShapeException
   */
  public void createRandomGrid() throws InvalidCellException, InvalidGridException{
    createRandomGrid(myWidth, myHeight);
  }

  /**
   * Create and set up the Grid based on stored information, and then return it.
   */
  private void loadFile() throws InvalidShapeException, InvalidGridException, InvalidCellException{
    extractConfigInfo();
    System.out.println(configSetUpConfirmationMessage);
    setRandomVariables();
    if(customRequested) {
      createGrid();
    }
    else{
      createRandomGrid();
    }
    System.out.println(gridConfirmationMessage);
  }

  private void setRandomVariables() {
    randomGridVariables = new double[myStates.size()];
    randomGridVariables[0] = FIRST_RANDOM_GRID_VARIABLE_VALUE;
    for(int i = 1; i<myStates.size(); i++){
      randomGridVariables[i] = RANDOM_GRID_VARIABLE_VALUE;
    }
  }


  private void setupDocument()
      throws InvalidFileException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new InvalidFileException(e);
    }
    try {
      doc = builder.parse(myFile);
    } catch (SAXException | IOException e) {
      throw new InvalidFileException(e);
    }
    doc.getDocumentElement().normalize();
  }

  /**
   * Extracts all information in the XML Document that lies within <ConfigInfo>.
   */
  private void extractConfigInfo() {
    NodeList configNodeList = doc.getElementsByTagName(CONFIG_NODE_NAME);
    Node configNode = configNodeList.item(0);

    if (configNode.getNodeType() == Node.ELEMENT_NODE) {
      Element configElement = (Element) configNode;
      extractTitle(configElement);
      printTitle();
      extractAuthor(configElement);
      printAuthor();
      extractShape(configElement);
      printShape();
      extractBorderType(configElement);
      printBorderType();
      extractMask(configElement);
      printMask();
      extractDimensions(configElement);
      extractStates(configElement);
      extractParameters(configElement);
      extractCustom(configElement);
      printCustom();
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
    printParameters();
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
    printStates();
  }

  private void extractDimensions(Element startingElement) {
    Node dimensionsNode = startingElement.getElementsByTagName(DIMENSIONS_NODE_NAME).item(0);
    if (dimensionsNode.getNodeType() == Node.ELEMENT_NODE) {
      Element dimensionsElement = (Element) dimensionsNode;
      extractHeight(dimensionsElement);
      extractWidth(dimensionsElement);
      extractSpeed(dimensionsElement);
      printDimensions();
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
    myBorderType = Integer.parseInt(extractElementValue(startingElement, BORDER_TYPE_NODE));
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

  private void printParameters() {
    System.out.println("All Parameters Set (Debug):");
    for (Map.Entry name : myParameters.entrySet()) {
      System.out.println("Name: " + name.getKey() + " & Value: " + name.getValue());
    }
  }

  private void printStates() {
    System.out.println("All States (Debug):");
    for (Map.Entry stateID : myStates.entrySet()) {
      System.out.println("State: " + stateID.getKey() + " & Value: " + stateID.getValue());
    }
    System.out.println("Default State: " + defaultState);
  }

  private void printDimensions() {
    System.out.println("Height:" + myHeight);
    System.out.println("Width:" + myWidth);
    System.out.println("Speed:" + mySpeed);
  }

  private void printTitle() {
    System.out.println("Simulation Name: " + myTitle);
  }

  private void printAuthor() {
    System.out.println("Author: " + myAuthor);
  }

  private void printShape() {
    System.out.println("Cell Shape Requested: " + myShape);
  }

  private void printCustom(){
    System.out.println("Custom cell locations? " + customRequested);
  }

  private void printBorderType(){
    System.out.println("Border Type: " + myBorderType);
  }

  private void printMask(){
      System.out.print("Mask applied: ");
      for(int i : myMask){
          System.out.print(i + " ");
      }
      System.out.println();
  }

   /**
   * Based on parameters AND Cell configuration, creates a grid.
   * @throws InvalidGridException
   * @throws InvalidShapeException
   */
  private void createGrid()
      throws InvalidGridException {
    Class gridClass = null;
    try {
      gridClass = Class.forName(gridPrefixName + myShape + gridSuffix);
    } catch (ClassNotFoundException e) {
      throw new InvalidShapeException(e);
    }
    try {
      myGrid = (Grid) (gridClass.getConstructor().newInstance());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidGridException(e);
    }
    int row = 0;
    NodeList rowNodeList = doc.getElementsByTagName(ROW_NODE_NAME);
    for (int i = 0; i < rowNodeList.getLength(); i++) {
      if(i < myHeight) {
        int col = 0;
        Node singleRowNode = rowNodeList.item(i);
        Element singleRowElement = (Element) singleRowNode;
        NodeList cellsNodeList = singleRowElement.getElementsByTagName(CELL_NODE_NAME);

        for (int k = 0; k < cellsNodeList.getLength(); k++) {
          if (k < myWidth) {
            Node singleCellNode = cellsNodeList.item(k);
            Integer cellState = Integer.valueOf(singleCellNode.getTextContent());
            Cell myCell = makeCell(cellState);
            myGrid.placeCell(col, row, myCell);
            col++;
          }
        }
        fillRow(col, row);
        row++;
      }
    }
    fillRemainingRows(row);
    for (int i: myStates.keySet()) {
      myGrid.addState(i);
    }
  }

  /**
   * Fills the remaining row of cells with cells of the default state, if the XML file does not
   * specify enough cells for a particular row.
   *
   * @param col the starting location in the row
   * @param row the row to be filled
   */
  private void fillRow(int col, int row) {
    while (col < myWidth) {
      Cell myCell = makeCell(defaultState);
      myGrid.placeCell(col, row, myCell);
      col++;
    }
  }

  private void fillRemainingRows(int row){
    while(row < myHeight){
      fillRow(0, row);
      row++;
    }
  }

  /**
   * Creates a cell and sets all relevant parameters to it from the config XML.
   *
   * @param state the specific state of the particular cell
   * @return
   * @throws InvalidCellException
   */
  private Cell makeCell(int state)
      throws InvalidCellException {
    Class cellClass = null;
    try {
      cellClass = Class.forName(cellPrefixName + myTitle);
    } catch (ClassNotFoundException e) {
      throw new InvalidCellException(e);
    }
    Cell cell = null;
    try {
      cell = (Cell) (cellClass.getConstructor().newInstance());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidCellException(e);
    }
    for (Map.Entry<String, Double> parameterEntry : myParameters.entrySet()) {
      cell.setParam(parameterEntry.getKey(), parameterEntry.getValue());
    }
    for (Map.Entry<Integer, Color> stateEntry : myStates.entrySet()) {
      myStates.put(stateEntry.getKey(), stateEntry.getValue());
    }
    if(myStates.keySet().contains(state)) {
      cell.setState(state);
    }
    else{
      cell.setState(defaultState);
      throw new InvalidCellException(new RuntimeException()); //FIXME added by maverick
    }
    cell.setDefaultEdge(myBorderType);
    cell.setMask(myMask);
    return cell;
  }

  private String getFileExtension(File file) {
    String fileName = file.getName();
    if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
      return fileName.substring(fileName.lastIndexOf(".")+1);
    else return "";
  }

  private boolean isImageFile(File file){
    String fileExtension = getFileExtension(file);
    for(String extension : ImageIO.getReaderFileSuffixes()){
      if(!fileExtension.equals(extension));
      return false;
    }
    return true;
  }
}