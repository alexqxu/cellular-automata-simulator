#Refactoring Lab

###Priorities:
 * Reorganize class hierarchy for the Main, Visualizer, and Config classes, and ensure that this is working
 * Exceptions handling
 * Move color handling from the Grid class to the Visualizer, and fix related dependencies.
 
###Problems Encountered:
1. **Loading a new file is not working**
   * Cause:
   * Fix: 
   * Rationale / Tradeoffs Considered:
2. **Exceptions Handling**
   *
   *
   *
3. **Config set colors in Cell**
   * Cause: Cell previously held its colors for its states.
   * Fix: Move the color maps to the visualization code and have config set that map
   * Rationale: The model should not have access or knowledge of any of the visualization, and thus
   Config should tell the visualizer about the colors, not the model.
   