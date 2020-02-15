package cellsociety.config;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.exceptions.InvalidGridException;
import cellsociety.simulation.cell.Cell;
import cellsociety.simulation.grid.Grid;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * This is a refactored class for the Code Masterpiece. The purpose of this class is to handle grid-making, which is now separated
 * from the parsing portion of the configuration component.
 * Depends on CellFactory returning a Cell of a given state properly, and valid values passed into the constructor from the Caller method.
 * Example: Used on the Config class to create Grid objects.
 * @author Alex Xu, aqx
 */
public class GridFactory {
    public static final double RANDOM_GRID_VARIABLE_VALUE = 0.5;
    public static final double FIRST_RANDOM_GRID_VARIABLE_VALUE = 0.25;

    private Grid myGrid;
    private Document myDoc;
    private String packagePrefixName = "cellsociety.simulation.";
    private String gridPrefixName = packagePrefixName+"grid.";
    private String gridSuffix = "Grid";
    private String myTitle;
    private String myShape;
    private Set<Integer> myStatesKeys;
    private Map<String, Double> myParameters;
    private int myWidth;
    private int myHeight;
    private int defaultState = 0;
    private int myBorderType = 0;
    private double[] myRandomGridVariables;
    private int[] myMask;
    private boolean myCustom;
    private CellFactory myCellFactory;

    /**
     * Constructor for a GridFactory object, which is responsible for creating a grid based on inputs (acquired from an XML, presumably).
     * @param doc Document object
     * @param title String representing the Title/type of simulation
     * @param statesKeys A set representing all of the states possible
     * @param parameters A Map that represents the special parameters inputted
     * @param shape A string that represents the type of Shape requested
     * @param mask A logical mask in the form of an int[] that defines neighborhoods
     * @param height A int representing the height of the Grid
     * @param width A int representing the width of the Grid
     * @param custom A boolean that stores if the user wants a random grid or a user defined one
     */
    public GridFactory(Document doc, String title, Set<Integer> statesKeys, Map<String, Double> parameters, String shape, int[] mask, int height, int width, boolean custom){
        myDoc = doc;
        myTitle = title;
        myStatesKeys = statesKeys;
        myParameters = parameters;
        myShape = shape;
        myHeight = height;
        myWidth = width;
        myCustom = custom;
        myMask = mask;
        myRandomGridVariables = setRandomVariables();
        myCellFactory = new CellFactory(myTitle, myParameters, myStatesKeys, defaultState, myBorderType, myMask);
    }

    /**
     * Returns a grid, either defined or random, based on the value of myCustom, which represents if the user wishes to make a custom grid.
     * @return Grid
     */
    public Grid createGrid(){
        if(myCustom){
            myGrid = createDefinedGrid();
        }
        else{
            myGrid = createRandomGrid(myWidth, myHeight);
        }
        return myGrid;
    }

    /**
     * Based on the parameters set, creates a grid with a randomized configuration of CELLS
     * @param width the width of the grid
     * @param height the height of the grid
     * @throws InvalidCellException
     * @throws InvalidGridException
     */
    public Grid createRandomGrid(int width, int height)  throws InvalidCellException, InvalidGridException{
        Class gridClass = null;
        Grid myRandomGrid;
        try {
            gridClass = Class.forName(gridPrefixName + myShape + gridSuffix);
            myRandomGrid = (Grid) (gridClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new InvalidGridException(e);
        }
        try {
            myRandomGrid.setRandomGrid(myTitle, myParameters, myRandomGridVariables, myBorderType, myMask, width, height);
        } catch (ClassNotFoundException e) {
            throw new InvalidCellException(e);
        }
        for (int i: myStatesKeys) {
            myRandomGrid.addState(i);
        }
        return myRandomGrid;
    }

    /**
     * Based on parameters AND Cell configuration, creates a grid.
     * @throws InvalidGridException
     */
    private Grid createDefinedGrid()
            throws InvalidGridException {
        Grid myDefinedGrid;
        Class gridClass = null;
        try {
            gridClass = Class.forName(gridPrefixName + myShape + gridSuffix);
            myDefinedGrid = (Grid) (gridClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new InvalidGridException(e);
        }
        int row = 0;
        NodeList rowNodeList = myDoc.getElementsByTagName(Config.ROW_NODE_NAME);
        for (int i = 0; i < rowNodeList.getLength(); i++) {
            if(i < myHeight) {
                int col = 0;
                Node singleRowNode = rowNodeList.item(i);
                Element singleRowElement = (Element) singleRowNode;
                NodeList cellsNodeList = singleRowElement.getElementsByTagName(Config.CELL_NODE_NAME);

                for (int k = 0; k < cellsNodeList.getLength(); k++) {
                    if (k < myWidth) {
                        Node singleCellNode = cellsNodeList.item(k);
                        Integer cellState = Integer.valueOf(singleCellNode.getTextContent());
                        Cell myCell = myCellFactory.makeCell(cellState);
                        myDefinedGrid.placeCell(col, row, myCell);
                        col++;
                    }
                }
                fillRow(col, row, myDefinedGrid);
                row++;
            }
        }
        fillRemainingRows(row, myDefinedGrid);
        for (int i: myStatesKeys) {
            myDefinedGrid.addState(i);
        }
        return myDefinedGrid;
    }

    /**
     * Fills the remaining row of cells with cells of the default state, if the XML file does not
     * specify enough cells for a particular row.
     *
     * @param col the starting location in the row
     * @param row the row to be filled
     */
    private void fillRow(int col, int row, Grid grid) {
        while (col < myWidth) {
            Cell myCell = myCellFactory.makeCell(defaultState);
            grid.placeCell(col, row, myCell);
            col++;
        }
    }

    private void fillRemainingRows(int row, Grid grid){
        while(row < myHeight){
            fillRow(0, row, grid);
            row++;
        }
    }

    private double[] setRandomVariables() {
        double [] randomVariables = new double[myStatesKeys.size()];
        randomVariables[0] = FIRST_RANDOM_GRID_VARIABLE_VALUE;
        for(int i = 1; i<myStatesKeys.size(); i++){
            randomVariables[i] = RANDOM_GRID_VARIABLE_VALUE;
        }
        return randomVariables;
    }
}