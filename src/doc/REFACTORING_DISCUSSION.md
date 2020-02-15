#Refactoring Lab

###Priorities:
 * Reorganize class hierarchy for the Main, Visualizer, and Config classes, and ensure that this is working
 * Exceptions handling
 * Move color handling from the Grid class to the Visualizer, and fix related dependencies.
 
###Problems Encountered:
1. **Loading a new file is not working**
   * Cause: Previously, the visualization created a config object that grabbed information from an xml file
   However, we wanted to refactor such that multiple types of visualizers could be created to handle different
   types of shapes. We also felt it did not make sense for Main to create Visualizer which would create the Config; 
   rather, the Config should create the Model and Visualizer based on information in reads from the XML file.
   In order to do this, however, we lost functionality in our "loadfile" button and "reset" button, as these objects 
   would call the visualizer to create a new config and therefore model based on a new file or the existing file,
   respectively. In order to avoid this, we tried a multitude of strategies: static methods in main allowing for the
   reloading of files, or even creating a loop of visualizers creating configs creating visualizers.
   * Fix:  In the end, we decided to put the UI controls in Main, allowing us to control the simulation 
   from a higher-order class creating the different specific types of visualizers. This way, the load/reset buttons
   can create new configs and visualizers and port them into the existing stage and UI toolbar.
   * Rationale / Tradeoffs Considered: The tradeoffs of this are that the UI is separated from the Graphics, meaning
   the front end of the program is split into two classes. However, this split is pretty rational (UI in Application,
   graphics in Visualizer) and so I do not think it is that large of a problem. However, this does create an issue with
   model-specific parameters, such as sliders to control "probability of catching on fire" in the fire simulation.
 
2. **Config set colors in Cell**
   * Cause: Cell previously held its colors for its states.
   * Fix: Move the color maps to the visualization code and have config set that map. Corresponding dependencies
   were resolved. This resulted in the creation of a method in Visualizer `getColorGrid` which iterates
   over the states returned from the Grid and creates a grid of corresponding colors, in order to
   preserve existing code workflow.
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