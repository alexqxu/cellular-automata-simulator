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
* Alex Xu: 32  
* Maverick Chung: 29
### Primary Roles
* Alex Oesterling:
    - Wrote Runner and Application class which integrate Modeling, Configuration, and Visualization components into the total simulation application. 
    These classes contain the UI element of the project, including all buttons, sliders, and menus aside from simulation-specific parameter fields
    which the user interacts with. Wrote Visualizer class which takes data from Grid class and uses this data to render a visualization of
    the scene, as well as generating the graph of cell populations and the simulation-specific parameter-tuning text fields. This visualizer passes
    its generated nodes to the Application for placing in the stage along with the general UI controls.
* Alex Xu:
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
        * Invalid or simulation type given
        * Invalid cell state values given
        * Cell locations given that are outside the bounds of the grid's size (Config just crops)
        * Appropriate default values when parameter values are invalid or not given (Handles too many or too few numbers of parameters, and invalid numbers for each parameter)      
    * Allow simulations initial configuration to be set by
        * List of specific locations and states
        * Completely randomly based on a total number of locations to occupy
        * Randomly based on probability distributions, not concentration distributions 
    * Allow users to save the current state of the simulation as an XML configuration file that can be loaded in as the configuration of a simulation
    * Allow any aspect of a simulation to be "styled", such as the following examples:
        * Kind of grid to use, shapes, neighbors or edges with appropriate error checking
        * Whether or not grid locations should be outlined
* 
    * 

### XML Guide
Note: In general, poorly formatted XML or bad options (e.g. setting size to `Pentagon`) will cause the XML reader
to ask you to pick a new file, while giving bad numbers will likely result in undesirable behavior (e.g. giving a 
negative shark breeding time will result in sharks breeding every timestep.)

#### Title and Author
Self-explanatory, indicating the title and author.

#### Shape
Can be one of Rect, Tri, or Hex. Inputting something else will cause the reader to ask for a new file.

#### Border Type
Indicates the edge type of the simulation. Inputting a non-negative number will treat all cells beyond the visible edge as that
type. Inputting a -1 will treat the grid as a toroid, and inputting a -2 will make the grid scale infinitely.

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
True or false, indicating whether or not the grid should use the custom configuration or be random.

#### Cell Rows
Each row has a numbr field, which has no effect on the simulation and is purely there to be human readable.
However, it will ask for a new file if it is larger than 255. The cells list is the states of the cells to be put on each row.
If it is larger than the stated dimensions, it will be truncated, and if it is smaller, the remaining cells will
be filled in with the default state.

### Notes/Assumptions

Assumptions or Simplifications:

Interesting data files:

Known Bugs:

Extra credit:


### Impressions

