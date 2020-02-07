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
   * Fix: Move the color maps to the visualization code and have config set that map
   * Rationale: The model should not have access or knowledge of any of the visualization, and thus
   Config should tell the visualizer about the colors, not the model.
   
3. **Exceptions Handling**
    * Cause: Most of the exceptions being thrown in our code arises from XML reading/parsing, and file handling. The
    remaining exceptions being thrown arises from using an inputted string to create instances of classes (e.g. InvalidClassException)
    
    * Fix: Created new custom exceptions, InvalidCellException, InvalidFileException, and InvalidGridException, which reduces the
    number of exceptions down to these three exceptions that describe the actual reason/source of exceptions that arise.
    Furthermore, these thrown exceptions were thrown to the main class, and finally handled there where the GUI prompts
    the user to choose a new, valid file to load. These three exceptions are three of the more extreme exceptions that can
    not be corrected for.
    
    * Tradeoffs Considered: Some of the exceptions that ended up in the Main class before refactoring arose from the Grid
    class, which were passed to the Config class where more document-related exceptions were thrown also. So, the
    decision of where (in what class) to throw the new custom exceptions needed to be made. As a team we decided that the
    best course of action would be to catch the old exceptions and throw the new exceptions in the Config class. Then, these
    exceptions will be resolved / caught again in the main class, which would prompt the user via the GUI to select a new file.
    This is a cleaner, more unified way of doing exceptions handling.