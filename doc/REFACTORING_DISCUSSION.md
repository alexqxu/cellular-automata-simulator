#Refactoring Lab

###Priorities:
 * Reorganize class hierarchy for the Main, Visualizer, and Config classes, and ensure that this is working
 * Exceptions handling
 * Move color handling from the Grid class to the Visualizer, and fix related dependencies.
 
###Problems Encountered:
1. **Loading a new file is not working**
   * Cause: Previously, the visualization created a config object that grabbed information from an xml file
   However, we wanted to refactor such that multiple types of visualizers.
   * Fix: 
   * Rationale / Tradeoffs Considered:
2. **Config set colors in Cell**
   * Cause: Cell previously held its colors for its states.
   * Fix: Move the color maps to the visualization code and have config set that map. Corresponding dependencies
   were resolved. This resulted in the creation of a method in Visualizer `getColorGrid` which iterates
   over the states returned from the Grid and creates a grid of corresponding colors, in order to
   preserve existing code workflow.
   * Rationale: The model should not have access or knowledge of any of the visualization, and thus
   Config should tell the visualizer about the colors, not the model.
   