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

 * Team Member #3

## Design goals
#### What Features are Easy to Add
* This project was designed to make the addition of new shapes to the simulation as well as new models
for the simulation quick and easy.
* In order to add new shapes, one simply has to make a new extension of the Visualizer Class with a new corresponding
extension of the abstract ```instantiateCellGrid()``` method which will handle the orientation and
placement of the new shape. In addition, one has to create a new extension of the Grid Class which will handle
the new neighbor-checking aspect of the shape (rectangles have 4-8 neighbors while hexagons have 6).
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

* The Grid (Maverick)
* The Cell (Maverick)

## Assumptions that Affect the Design
* One assumption made in the design of the UI is that there is no need for a global play/pause button.
This assumption was made to simplify the design because adding a global play/pause button would
require each SimulationApp to have access to all the other SimulationApps. This would either require
each app to have access to a List of all the other Apps, or require the play/pause button to be in the 
more overarching SimulationRunner class. The first option would significantly increase the complexity of the 
project for little benefit for the user, and the second option would reduce the closedness of the program
by putting half of the UI into a different class. 
#### Features Affected by Assumptions


## New Features HowTo
* How to add additional shapes
    * Visualizer package-side: Create a new class extending Visualizer. Implement the default constructor and 
    create the ```instantiateCellGrid()``` method (will be prompted to do so by most IDE's). Then using similar syntax to the code inside the other
    child visualizers, fill in the method to read color data from the ```getColorGrid()``` method
    and use this data to render a series of the desired shape with the correct color
    * Model package-side: (Maverick)
#### Easy to Add Features
* New loops (Maverick)
#### Other Features not yet Done
* Loading an image (Alex X)
