package cellsociety.config;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.simulation.cell.Cell;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * This is a refactored class for the Code Masterpiece that handles cell-making and returns cell objects.
 * Depends on valid values passed into the constructor from the Caller method.
 * Example: GridFactory uses CellFactory to populate the Grid with cells.
 * @author Alex Xu
 */

public class CellFactory {
    private String packagePrefixName = "cellsociety.simulation.";
    private String myCellPrefixName = packagePrefixName+"cell.";
    private String myTitle;
    private Map<String, Double> myParameters;
    private Set<Integer> myStates;
    private int myDefaultState;
    private int myBorderType;
    private int[] myMask;

    /**
     * Constructor for the CellFactory object, which is responsible for creating a Cell based in defined parameters,
     * states, Cell Types, borders, and neighbors.
     * @param title The type of Cell
     * @param parameters A map with parameter names and their values
     * @param states A set representing all of the possible states a cell can take
     * @param defaultState An integer representing the default state if there is no cell specified at a location
     * @param borderType An integer representing the border type (Regular, Toroidal, Infinite) See ReadMe for more info
     * @param mask A int array representing a logical mask to be applied to the Cell (which controls neighbor checking)
     */
    public CellFactory(String title, Map<String, Double> parameters, Set<Integer> states, int defaultState, int borderType, int[] mask){
        myTitle = title;
        myParameters = parameters;
        myStates = states;
        myDefaultState = defaultState;
        myBorderType = borderType;
        myMask = mask;
    }

     /**
     * Creates a cell and sets all relevant parameters to it from the config XML.
     * @param state the specific state of the particular cell
     * @return a cell made from given information
     * @throws InvalidCellException
     */
    public Cell makeCell(int state)
            throws InvalidCellException {
        Class cellClass = null;
        Cell cell = null;
        try {
            cellClass = Class.forName(myCellPrefixName + myTitle);
            cell = (Cell) (cellClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new InvalidCellException(e);
        }
        for (Map.Entry<String, Double> parameterEntry : myParameters.entrySet()) {
            cell.setParam(parameterEntry.getKey(), parameterEntry.getValue());
        }
        if(myStates.contains(state)) {
            cell.setState(state);
        }
        else{
            cell.setState(myDefaultState);
            throw new InvalidCellException(new RuntimeException());
        }
        cell.setDefaultEdge(myBorderType);
        cell.setMask(myMask);
        return cell;
    }
}