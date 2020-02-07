package config;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.simulation.Cell;
import cellsociety.simulation.Grid;
import cellsociety.simulation.HexGrid;
import cellsociety.simulation.RectGrid;
import cellsociety.simulation.TriGrid;
import cellsociety.visualizer.HexVisualizer;
import cellsociety.visualizer.RectVisualizer;
import cellsociety.visualizer.TriVisualizer;
import cellsociety.visualizer.Visualizer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
 * Reads an XML file and sets up the Grid.
 *
 * @author Alex Xu aqx
 */
public class Config {
  private static final String INVALID_CELL = "Invalid Cell Thrown";
  private static final String INVALID_FILE = "Invalid File Requested";

  private String packagePrefixName = "cellsociety.simulation.";

  private String configNodeName = "ConfigInfo";
  private String titleNodeName = "Title";
  private String authorNodeName = "Author";
  private String parametersNodeName = "SpecialParameters";
  private String statesNodeName = "States";
  private String singleParameterNodeName = "Parameter";
  private String singleStateNodeName = "State";
  private String parameterNameAttributeName = "name";
  private String rowNodeName = "Row";
  private String stateIDNodeName = "ID";
  private String colorNodeName = "Color";
  private String dimensionsNodeName = "Dimensions";
  private String speedNodeName = "Speed";
  private String widthNodeName = "Width";
  private String heightNodeName = "Height";
  private String cellNodeName = "Cell";
  private String shapeNodeName = "Shape";

  private String docSetUpConfirmationMessage = "Document Setup Complete";
  private String configSetUpConfirmationMessage = "Config Info Load Complete";
  private String gridConfirmationMessage = "Grid Created";

  private File myFile;
  private Document doc;

  private Grid myGrid;
  private String myTitle;
  private String myAuthor;
  private String myShape;
  private double mySpeed;
  private int myWidth;
  private int myHeight;
  private Map<Integer, Color> myStates;
  private Map<String, Double> myParameters;
  private int defaultState = 0;

  private double[] randomGridVariables = new double[]{.2, .7, 0};

  /**
   * Constructor for the Config object. Sets the filepath and sets up the documentBuilder.
   *
   * @param xmlFile File object passed in, in XML format
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public Config(File xmlFile)
      throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    myFile = xmlFile;
    setupDocument();
    System.out.println(docSetUpConfirmationMessage);
    loadFile();
  }

  /**
   * Create and set up the Grid based on stored information, and then return it.
   *
   * @return
   */
  public Grid loadFile() {
    extractConfigInfo();
    System.out.println(configSetUpConfirmationMessage);
    createGrid();
    System.out.println(gridConfirmationMessage);
    return myGrid;
  }

  /**
   * Returns the update speed of the simulation, as defined within the initial config XML document.
   *
   * @return speed of the simulation
   */
  public double getSpeed() {
    return mySpeed;
  }

  private void setupDocument() throws IOException, SAXException, ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    doc = builder.parse(myFile);
    doc.getDocumentElement().normalize();
  }

  /**
   * Extracts all information in the XML Document that lies within <ConfigInfo>.
   */
  private void extractConfigInfo() {
    NodeList configNodeList = doc.getElementsByTagName(configNodeName);
    Node configNode = configNodeList.item(0);

    if (configNode.getNodeType() == Node.ELEMENT_NODE) {
      Element configElement = (Element) configNode;
      extractTitle(configElement);
      printTitle();
      extractAuthor(configElement);
      printAuthor();
      extractDimensions(configElement);
      extractStates(configElement);
      extractParameters(configElement);
    }
  }

  private void extractParameters(Element configElement) {
    myParameters = new HashMap<>();

    Node parametersNode = configElement.getElementsByTagName(parametersNodeName).item(0);
    if (parametersNode.getNodeType() == Node.ELEMENT_NODE) {
      Element parametersElement = (Element) parametersNode;

      NodeList parametersNodeList = parametersElement.getElementsByTagName(singleParameterNodeName);

      for (int i = 0; i < parametersNodeList.getLength(); i++) {
        Node singleParameterNode = parametersNodeList.item(i);
        if (singleParameterNode.getNodeType() == Node.ELEMENT_NODE) {
          Element singleParameterElement = (Element) singleParameterNode;
          String parameterName = singleParameterElement.getAttribute(parameterNameAttributeName);
          Double parameterValue = Double.valueOf(singleParameterElement.getTextContent());
          myParameters.put(parameterName, parameterValue);
        }
      }
    }
    printParameters();
  }

  private void extractStates(Element startingElement) {
    myStates = new HashMap<>();

    Node statesNode = startingElement.getElementsByTagName(statesNodeName).item(0);
    if (statesNode.getNodeType() == Node.ELEMENT_NODE) {
      Element statesElement = (Element) statesNode;

      NodeList statesNodeList = statesElement.getElementsByTagName(singleStateNodeName);

      for (int i = 0; i < statesNodeList.getLength(); i++) {
        Node singleStateNode = statesNodeList.item(i);
        if (singleStateNode.getNodeType() == Node.ELEMENT_NODE) {
          Element singleStateElement = (Element) singleStateNode;
          Integer stateID = Integer.valueOf(
              singleStateElement.getElementsByTagName(stateIDNodeName).item(0).getTextContent());
          String stateColor = singleStateElement.getElementsByTagName(colorNodeName).item(0)
              .getTextContent();
          myStates.put(stateID, Color.web(stateColor));
        }
      }
    }
    printStates();
  }

  private void extractDimensions(Element startingElement) {
    Node dimensionsNode = startingElement.getElementsByTagName(dimensionsNodeName).item(0);
    if (dimensionsNode.getNodeType() == Node.ELEMENT_NODE) {
      Element dimensionsElement = (Element) dimensionsNode;
      extractHeight(dimensionsElement);
      extractWidth(dimensionsElement);
      extractSpeed(dimensionsElement);
      printDimensions();
    }
  }

  private void extractTitle(Element startingElement) {
    myTitle = extractElementValue(startingElement, titleNodeName);
  }

  private void extractAuthor(Element startingElement) {
    myAuthor = extractElementValue(startingElement, authorNodeName);
  }

  private void extractSpeed(Element dimensionsElement) {
    mySpeed = Double.parseDouble(extractElementValue(dimensionsElement, speedNodeName).trim());
  }

  private void extractWidth(Element dimensionsElement) {
    myWidth = Integer.parseInt(extractElementValue(dimensionsElement, widthNodeName).trim());
  }

  private void extractHeight(Element dimensionsElement) {
    myHeight = Integer.parseInt(extractElementValue(dimensionsElement, heightNodeName).trim());
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

  /**
   * Based on the parameters set, creates a grid with a randomized configuration of CELLS
   */
  private void createRandomGrid() {
    myGrid = new RectGrid(); //FIXME temp fix by Maverick after making Grid abstract
    myGrid.setRandomGrid(myTitle, myParameters, randomGridVariables, myWidth, myHeight);
  }

  /**
   * Based on parameters AND Cell configuration, creates a grid.
   *
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws InstantiationException
   */
  private void createGrid() {
    myGrid = new TriGrid(); //FIXME temp fix by Maverick after making Grid abstract
    int row = 0;
    NodeList rowNodeList = doc.getElementsByTagName(rowNodeName);

    for (int i = 0; i < rowNodeList.getLength(); i++) {
      int col = 0;
      Node singleRowNode = rowNodeList.item(i);
      Element singleRowElement = (Element) singleRowNode;
      NodeList cellsNodeList = singleRowElement.getElementsByTagName(cellNodeName);

      for (int k = 0; k < cellsNodeList.getLength(); k++) {
        if (k < myWidth) {
          Node singleCellNode = cellsNodeList.item(k);
          Integer cellState = Integer.valueOf(singleCellNode.getTextContent());
          Cell myCell = makeCell(cellState);
          myGrid.placeCell(col, row, myCell);
          col++;
        }
      }
      fillRemainingRow(col, row);
      row++;
    }
  }

  /**
   * Fills the remaining row of cells with cells of the default state, if the XML file does not
   * specify enough cells for a particular row.
   *
   * @param col the starting location in the row
   * @param row the row to be filled
   */
  private void fillRemainingRow(int col, int row) {
      while (col < myWidth) {
      Cell myCell = makeCell(defaultState);
      myGrid.placeCell(col, row, myCell);
      col++;
    }
  }

  /**
   * Creates a cell and sets all relevant parameters to it from the config XML.
   *
   * @param state the specific state of the particular cell
   * @return
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   */
  private Cell makeCell(int state)
      throws InvalidCellException {
    Class cellClass = null;
    try {
      cellClass = Class.forName(packagePrefixName + myTitle);
    } catch (ClassNotFoundException e) {
      throw new InvalidCellException(e);
    }
    Cell cell = null;
    try {
      cell = (Cell) (cellClass.getConstructor().newInstance());
    } catch (InstantiationException e) {
      throw new InvalidCellException(e);
    } catch (IllegalAccessException e) {
      throw new InvalidCellException(e);
    } catch (InvocationTargetException e) {
      throw new InvalidCellException(e);
    } catch (NoSuchMethodException e) {
      throw new InvalidCellException(e);
    }
    for (Map.Entry<String, Double> parameterEntry : myParameters.entrySet()) {
      cell.setParam(parameterEntry.getKey(), parameterEntry.getValue());
    }
    for (Map.Entry<Integer, Color> stateEntry : myStates.entrySet()) {
      myStates.put(stateEntry.getKey(), stateEntry.getValue());
    }
    cell.setState(state);
    return cell;
  }
//fixme added by alex
  public Grid getGrid() {
    return myGrid;
  }
  public Map<Integer, Color> getStates(){return myStates;}
}