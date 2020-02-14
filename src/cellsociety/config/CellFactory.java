package cellsociety.config;

import cellsociety.exceptions.InvalidCellException;
import cellsociety.simulation.cell.Cell;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * This is a refactored utility class that returns cell objects
 */

public class CellFactory {

    private CellFactory(){}

     /**
     * Creates a cell and sets all relevant parameters to it from the config XML.
     * @param state the specific state of the particular cell
     * @return a cell made from given information
     * @throws InvalidCellException
     */
    public static Cell makeCell(int state, String cellPrefixName, String myTitle, Map<String, Double> myParameters, Set<Integer> states, int defaultState, int myBorderType, int[] myMask)
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
        if(states.contains(state)) {
            cell.setState(state);
        }
        else{
            cell.setState(defaultState);
            throw new InvalidCellException(new RuntimeException());
        }
        cell.setDefaultEdge(myBorderType);
        cell.setMask(myMask);
        return cell;
    }

}
