package cellsociety.config;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.simulation.cell.Cell;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * This is a refactored class that handles cell-making and returns cell objects.
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
        try {
            cellClass = Class.forName(myCellPrefixName + myTitle);
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
