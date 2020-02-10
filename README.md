simulation
====

This project implements a cellular automata simulator.

Names:
Alex Oesterling, axo
Alex Xu, aqx
Maverick Chung, mc608
### Timeline

#####Start Date:   
January 26, 2020
#####Finish Date:   
February 9, 2020
#####Hours Spent:  
* Alex Oesterling: 34  
* Alex Xu: 35  
* Maverick Chung: 29
### Primary Roles
* Alex Oesterling:
    - Wrote Runner and Application class which integrate Modeling, Configuration, and Visualization components into the total simulation application. 
    These classes contain the UI element of the project, including all buttons, sliders, and menus aside from simulation-specific parameter fields
    which the user interacts with. Wrote Visualizer class and inheritance hierarchy which takes data from Grid class and uses this data to render a visualization of
    the scene, as well as generating the graph of cell populations and the simulation-specific parameter-tuning text fields. This visualizer passes
    its generated nodes to the Application for placing in the stage along with the general UI controls.
* Alex Xu:
    - Created XML formatting used for specifying simulations in the program
    - Created XML files for different simulation types (e.g. Conway, Percolation, Fire, etc.)
    - Wrote Configuration portion of assignment which reads XML files and uses their data to specify
    the creation of the correct shape, dimensions, parameters, and types of simulations.
    - Created an XMLWriter class which saves a simulation's current status and parameters to a new XML file that the user
    can then load again in the future.
    - Created an XMLValidator class which uses a XSD Schema file to validate an XML file before loading, to verify that the XML
    is of the correct format (Element structure are correct, and Values are of the expected types).
    - Implemented error handling for exceptions that can occur in XML configuration
    - Created ImageReader, which initializes a grid based on pixels in an image. This is a work-in-progress feature that is
    not currently activated in the code, but the framework for its functionality exists.
* Maverick Chung:
    - Wrote Cell class and subclasses to implement the cellular simulations, including making a Rule Table subclass to read simulation
    rules from a String table.
    - Wrote Grid class and subclasses to implement different types of grid shapes, including rectangular, triangular, and hexagonal.
    - Implemented different types of neighborhoods for cells to interact with their neighbors, changeable by XML.
    - Implemented different types of edges for simulations, including finite, toroidal, and infinite.


### Resources Used
* Stack Overflow
* JavaFX online documentation
* Design Checkup DEV
* CS 308 website

### Running the Program

Main class:
* Simulation Runner  

Data files needed: 
 * Any of the XML files listed in the /data/ folder
 * All Files in the cellsociety and resources packages should be present.
 
Features implemented:
* Simulation
    * Allows for different arrangements of neighbor checking
    * Allows for different grid location shapes, including:
        * Squares
        * Triangles
        * Hexagons
    * Allows for the following kinds of grid edge types (accounting for special conditions unique to simulations):
        * Finite
        * Toroidal
        * Infinite
    * Implemented the following additional simulations:
        * Rock, Paper, Scissors
        * Langton's Loop
        * Byl Loop
        * Chou Reggia Loop
* Configuration
    * Implemented error checking for incorrect file data of the following forms:
        * Invalid title or simulation type given
        * Invalid cell state values given
        * Cell locations given that are outside the bounds of the grid's size
            * Those cells will not be used.
        * Not enough cells specified to fill the bounds of the grid's size
            * If cell location is smaller than grid size, the Grid is populated missing cells with the default state (user specifiable)
        * Appropriate default values when parameter values are invalid or not given (Handles too many or too few numbers of parameters, and invalid numbers for each parameter)
        * XML Structure is incorrect
            * Misspelled or missing Elements
            * Data in text Nodes are not of the expected type (String, unsigned byte, etc.)
        * Invalid grid size given (too small)
        * <Custom> field, which specifies if the grid will be randomly generated or use custom configuration, is not a boolean
            * Field defaults to false unless explicitly true.
        * Incorrect Shape Requested
        * Incorrect Mask or Border Type requested
            * Default values are loaded based on simulation.
        * Extra whitespace in text fields
            * Whitespace is ignored when extracting information from XML.
    * Allow simulations initial configuration to be set by
        * List of specific locations and states
        * Completely randomly based on a total number of locations to occupy
        * Randomly based on probability distributions, not concentration distributions 
    * Allow users to save the current state of the simulation as an XML configuration file that can be loaded in as the configuration of a simulation
    * Allow any aspect of a simulation to be "styled", such as the following examples:
        * Kind of grid to use, shapes, neighbors or edges with appropriate error checking
        * Whether or not grid locations should be outlined (in UI)
        * Color of cell or patch states
        * Shape of cells
        * Special parameters that are simulation-specific, like probCatch or happinessThresh
    * Other ideas for initialization:
        * Using an image to initialize a Rock-Paper-Scissors simulation (Work in Progress).
        
* Visualization
    * Display a graph of stats about the populations of all the "kinds" of cells over the time of the simulation
    * Allow users to interact with the simulation dynamically to change the values of its parameter
    * Allow users to interact with the simulation dynamically to create or change a state at a grid location
    * Allow users to run multiple simulations at the same time so they can compare the results side by side
        * These simulations run independently of each other as separate applications. This was a design choice we made because having each individual application
        be synchronized would require an element of the UI be contained inside the universal runner whereas the rest of the UI 
        is unique to each instance of the application. This "global UI control" would violate the closed-ness of our UI design
        and split functionality into different classes, making our code less focused.
        * Furthermore, creating multiple windows allows for more flexibility and ease of use in terms of loading and closing new files/windows.
        I decided to allow running independently as users can always run two simulations they want themselves and compare them, even if 
        these simulations are not exactly in lock-step.

### XML Guide
Note: In general, poorly formatted XML or bad options (e.g. setting size to `Pentagon`) will cause the XML reader
to ask you to pick a new file, while giving bad numbers will likely result in undesirable behavior, but not break the program (e.g. giving a 
negative shark breeding time will result in sharks breeding every timestep.)

#### Title and Author
Self-explanatory, indicating the title and author. Title should correspond to a defined Simulation Type to work properly.

#### Shape
Can be one of Rect, Tri, or Hex. Inputting something else will cause the reader to ask for a new file.

#### Border Type
Indicates the edge type of the simulation. Inputting a non-negative number will treat all cells beyond the visible edge as that
type. Inputting a -1 will treat the grid as a toroid, and inputting a -2 will make the grid scale infinitely. Inputting a non-number
will prompt for a new file.

#### Mask
Indicates the neighborhood of each cell. Must match in dimension to the maximum number of possible neighbors for the given shape
(e.g. 12 for triangle) or it will be ignored. Put a 0 for every neighbor the cell should ignore, and a 1 for all other neighbors.
The first value is the northmost neighbor, and they rotate clockwise from there (e.g. the mask `1 0 0 1 0 0`) will only check
neighbors directly above or below the cell in a hex grid.

#### Dimensions
* Width and Height: Must both be integer values larger than 0.
* Speed: A float between 0 and 1, indicating the speed of the simulation. Speed of 1 updates every frame, while speed of 0
updates once every two seconds. Indicating a speed higher than 1 has the same effect of inputting 1, while inputting a 
negative speed will cause the speed to be update slower than once per two seconds.

#### Special Parameters
These are the parameters for the simulations, in the format `<Parameter name = "fishFeedEnergy">1.1</Parameter>`.
Putting parameters in a simulation that does not require them will have no effect. Not including required parameters
will cause the reader to ask for a new file.

#### States
* Default: the default state used to fill in unspecified cells.
* State: a tag pairing a state to a color. The colors can be standard HTML names or hex strings (e.g. "00cc00").
Deleting a required state will ask for a new file.

#### Custom
True or false, indicating whether or not the grid should use the custom configuration or be random. Inputting anything other
than true or false defaults to false.

#### Cell Rows
Each row has a "numbr" field, which has no effect on the simulation and is purely there to be human readable.
However, it will ask for a new file if it is larger than 255. The cells list is the states of the cells to be put on each row.
If it is larger than the stated dimensions, it will be truncated, and if it is smaller, the remaining cells will
be filled in with the default state.

### Notes/Assumptions
Assumptions or Simplifications:
* Assuming that states are continuous (if there are 3 states they are always specified as 0, 1, 2, not 1, 3, 5): This allows us to click to cycle through cell states dynamically
* Loop simulations assume the grid is of rectangles
* Assuming that new windows will not have a universal play-pause button because it would violate certain design principles and make the class hierarchy more complicated. This 
choice is justified because we assume that a user will never need to see two simulations running in lock step because they can play two near each other in time or even alternate
back and forth using the step option

Interesting data files:
* ```LangtonLoop``` is an interesting example of an infinitely-scaling, growing loop
* If you look at the smaller loops (```ChouReggia``` or ```Byl```) and put it on high speed, they form a fractal-like pattern which is really mesmerising to watch.
* ```UpwardFire``` demonstrates masks (setting different arrangements of neighbor checking)

Files that test Error Handling (in badXML folder):
* ```2BylLoop```is a bad xml files which demonstrate how error handling occurs.
* ```InvalidCellTest``` tests the exception handling for specifying a nonexistent cell/simulation
* ```InvalidShapeTest``` tests the exception handling for specifying an invalid shape for a grid (Circle)
* ```InvalidStructureTest``` tests the exception handling for incorrect labelling in the XML (ie <Dymensionz> instead of <Dimensions>)

Known Bugs:
* Turning off the gridlines in a hex grid leaves a small 1-pixel line where the gridlines were.
* Turning off the gridlines in a triangle grid leaves a small 1-pixel line where the diagonal gridlines were.
* Running large grids at full speed causes significant lag.

Extra credit:
* Implemented more features than the minimum required
* Work-in-progress of ImageReader, which uses an image to initialize the grid.

### Impressions
* Cellular Autonoma is really interesting and mesmerising once you get the really nifty simulations running.
 It shows the more concrete and scientific applications of computer science and application building as well as the importance
 of separating the frontend and the backend.
