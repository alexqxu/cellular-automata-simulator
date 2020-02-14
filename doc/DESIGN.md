# Simulation Design Final
### Names

## Team Roles and Responsibilities

 * Team Member #1: Alex Oesterling, axo
    * Responsibilities: Wrote SimulationRunner and SimulationApp classes which handled the overall
    running of the project as well as generating the UI. Wrote the Visualizer hierarchy which handles
    the creation of the set of shapes and their colors to be rendered in the scene based on data from
    the Grid class. 
    
 * Team Member #2: Alex Xu, aqx
    * 

 * Team Member #3


## Design goals
#### What Features are Easy to Add
* This project was designed to make the addition of new shapes to the simulation as well as new models
for the simulation quick and easy.
* In order to add new shapes, one simply has to make a new extension of the Visualizer Class with a new corresponding
shextension of the abstract ```instantiateCellGrid()``` method which will handle the orientation and
placement of the new shape. In addition, one has to create a new extension of the Grid Class which will handle
the new neighbor-checking aspect of the shape (rectangles have 4-8 neighbors while hexagons have 6).
* In addition, the implementation of new simulations are quite easy. Simply create a new extension of the Cell
class with a new ruleset and have the XML config specify this new cell to create a simulation based on 
new rules. The UI and graphics will automatically handle this new update, even if it contains new parameters.
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
* The Config (Alex)
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
