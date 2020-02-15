# Simulation Design Final
### Names: Alex Oesterling, Alex Xu, Maverick Chung

## Team Roles and Responsibilities

 * Team Member #1: Alex Oesterling, axo
    * Responsibilities: Wrote SimulationRunner and SimulationApp classes which handled the overall
    running of the project as well as generating the UI. Wrote the Visualizer hierarchy which handles
    the creation of the set of shapes and their colors to be rendered in the scene based on data from
    the Grid class. 
    
 * Team Member #2: Alex Xu, aqx
    * Responsibilities: Create XML format used for configuration files, Config which handles parsing of these XML files
    and sets up the Grid based on it, XMLValidator which validates the structure of user-inputted XML files against an XSD
    schema, and XMLWriter which handles writing/saving user-created XML files via the GUI.

 * Team Member #3: Maverick Chung, mc608
    * Responsibilities: Writing the model for the simulation, namely writing the Grid and Cell classes that allow 
    for significant functionality, including multiple types of simulations, simulations that include cells that represent
    entities that move about to empty sites, simulations that use a rule table to propagate, simulations with cells of different shapes,
    and cells that depend on different neighborhoods.

## Design goals
#### What Features are Easy to Add
* This project was designed to make the addition of new shapes to the simulation as well as new models
for the simulation quick and easy.
* In order to add new shapes, one simply has to make a new extension of the Visualizer Class with a new corresponding
extension of the abstract ```instantiateCellGrid()``` method which will handle the orientation and
placement of the new shape. In addition, one has to create a new extension of the Grid Class which will handle
the new neighbor-checking aspect of the shape (rectangles have 4-8 neighbors while hexagons have 6).
* To make new simulations, one simply has to make a new class extending the abstract Cell class and implement the planUpdate
method to define each cell's behavior. If the cell requires information about other cells in the grid, the new class can
override the isEmpty() method to added cells to the queue that is passed into the planUpdate() method.
* In addition, the implementation of new simulations are quite easy. Simply create a new extension of the Cell
class with a new ruleset and have the XML config specify this new cell to create a simulation based on 
new rules. The UI and graphics will automatically handle this new update, even if it contains new parameters. No other
changes to resources or XML are required.
* Features such as different color-mappings to cell states are already built-in to the program so the user would only need
to edit the XML configuration files to include new colors.

## High-level Design
#### Core Classes
* The class which begins the entire program is the SimulationRunner Class. This class simply
creates the first instance of the SimulationApp in a new stage (window) and then "passes the reigns" 
to this App object, which then has access to the ability to load new files, make new App objects (new windows of simulation),
and exit out of the App.

* The SimulationApp handles the UI and rendering of each individual window. It creates and responds to UI inputs such
as the commands to load files, play/pause the simulation, and more. This app also is responsible for creating a Config to read
the specified XML file (specified at instantiation in the constructor), and using this data to create the 
correct Visualizer object as well. It then takes Visualizer data to create the cell grid, graph, and simulation-specific
parameter tunings at the bottom of the scene.

* The Visualizer class handles the rendering of the grid of cells, the graph, and the simulation-specific parameters.
It takes data from the Grid object such as color of an individual cell (passing to the Grid just a row/column index) and 
then uses this data to render the grid. It also uses this color data to create a color-coded graph, 
and uses the Grid's ```getParameters()``` method to create the parameter TextFields at the bottom.
It sends this group of Nodes as a Pane back up to the SimulationApp for rendering in the window with the UI controls.

* The Config class takes a File (XML) object as a parameter, and is responsible for parsing through the XML file
and creating a Grid object based off of the contents of the XML (with parameters, shapes, locations, colors, etc.). The Grid object is shape-specific,
and contains Cell objects that are simulation-specific. This created Grid is then handed off to the Visualizer. Before parsing the
XML file, the Config class calls the static method ```validateXMLStructure``` of the XMLValidator class to check for structure
errors in the XML. Actual parsing of the XML file is based off of the DOM structure, which stores the elements of the XML file
as a data tree to mimic the nesting structure of the XML document.

* The XMLValidator class is a utility/static class whose sole purpose is to "validate" a user-inputted XML file against
a defined XSD file (XML Schema file). The XSD file contains information regarding what element tags and fields are valid.
If there is an issue, it will return an exception with an error message that is describes the issue that is found to help
the user debug their XML. The schema file used in the final version of our program is called "schema_v2.xsd", which is included
in the config package (not in the resources folder by design as we do not want this schema file to be modified by the end user).

* The XMLWriter class is responsible for saving a user-created grid from the GUI (either by generating
a random grid, or editing the grid by interacting with/clicking it). It takes a Grid object and Config object, and handles the
"copying over" of data into a new written XML file. Like the Config class, this is again based off of the DOM structure, but in
reverse (converting the nodes of a tree to a written XML file).

* The Grid class is a class responsible for storing the locations of the cells such that they may interact with each other.
In all current versions of the grid, these cells are stored as a two-dimensional ArrayList. Rectangular grids and triangles fit 
this without modification, and hexagonal grids assume that even number columns (0-indexed) are moved downwards slightly to allow for 
the hexagonal shape. Different shapes are supported by each subclass' getNeighbors() method, allowing for different
changes in row/column to define different sets of neighbors. Thus, supporting any type of shape is reduced to creating two small
int[].
* The Cell class is a class responsible for storing the state of each cell and defining its interactions with other cells.
Cells have an integer state that update on each timestep based on the planUpdate() method. Once the next state of the cell has
been planned, all cells assume their new state simultaneously. Cell parameters are stored in a map, and new parameters can be added
and accessed through methods. New simulations can be created by extending the Cell class and overriding the planUpdate() method 
according to the rules of the simulation. This is especially easy in the case of simulations that have a String rule table, as
the rule table just needs to be copied and pasted into the class for the rules to take effect.

## Assumptions that Affect the Design
* One assumption made in the design of the UI is that there is no need for a global play/pause button.
This assumption was made to simplify the design because adding a global play/pause button would
require each SimulationApp to have access to all the other SimulationApps. This would either require
each app to have access to a List of all the other Apps, or require the play/pause button to be in the 
more overarching SimulationRunner class. The first option would significantly increase the complexity of the 
project for little benefit for the user, and the second option would reduce the closedness of the program
by putting half of the UI into a different class.

* Another assumption made was that all simulations only check immediate neighbors (i.e. a cell on one side
of the grid has no effect on a cell on the other side.) All simulations implemented abide by this assumption.

* Another assumption made was that all numbered states were contiguous (e.g. no simulation has states 0, 1, and 3,
but not 2).

#### Features Affected by Assumptions

* Because of the assumption that rules for simulations should only check immediate neighbors, creating an
infinitely scaling grid is as simple as creating a row or column of empty cells if a non-empty cell touches the 
border of the grid.
* Assuming that all numbered states are contiguous allows us to allow the user to change the state of a cell
by clicking on it, as we can simply increment the state of the cell without fear of running into undefined behavior
due to undefined states.

## New Features HowTo
* How to add additional shapes
    * Visualizer package-side: Create a new class extending Visualizer. Implement the default constructor and 
    create the ```instantiateCellGrid()``` method (will be prompted to do so by most IDE's). Then using similar syntax to the code inside the other
    child visualizers, fill in the method to read color data from the ```getColorGrid()``` method
    and use this data to render a series of the desired shape with the correct color
    * Model package-side: Create a new class that extends Grid, and override the getNeighbors method. This method should
    create two int arrays that contain the row and column changes for the cell's neighbors, and then call super.getSpecificNeighbors()
    with the two arguments passed into getNeighbors and the two arrays. These arrays should start with the northmost neighbor
    of the cell, and rotate clockwise from there. As an example, the northmost neighbor of a square is the cell directly above
    it, and as such the first entry in the row change array would be -1, and the first entry in the column change array would be 0.
    If the absolute value of any of the numbers in either of the arrays is 2 or larger, you must also override the padGrid() method
    in order to have infinite grids be functional. This should call super.padGrid(), which adds an empty row/column to every side
    of the grid that has a non-empty cell. It should then add rows or columns to allow for a cell on the edge to be moved towards
    the center such that all of its neighbors are defined.
* How to add additional simulations:
    * Create a class extending the Cell class and overriding the planUpdate() method according to the rules of the simulation.
    The planUpdate method should put the next state of the cell in the nextstate instance variable.
     This is especially easy in the case of simulations that have a String rule table, as
     the rule table just needs to be copied and pasted into the class for the rules to take effect.
    
#### Easy to Add Features
* New simulations that have a rule table with a von Neumann neighborhood and rotational symmetry are exceptionally easy
to implement, simply by extending the RuleTableCell class and pasting the String rule table into the final instance variable
RULE_TABLE.
#### Other Features not yet Done
* Loading an Image to initialize the Grid
    * The purpose of this feature is to allow users to select image files to initialize the grid, as an alternative to regular
    XML files. The work-in-progress ImageReader class is used for this purpose, which reads pixel information as cell states
    and creates a grid based off of that information. 
    * This is a work-in-progress feature that is not yet complete, as we decided to prioritize refining/debugging our program's
    core features before the submission deadline.
    * However, the other classes are already set up to "accept" this new feature. Once the ImageReader class is complete and
    functional, all one has to do is change the type of file extension the File selector allows (to also allow image file extensions).
    The main Config class has already implemented a file extension checker that looks for image file extensions.