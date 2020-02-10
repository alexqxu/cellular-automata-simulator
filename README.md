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
* Maverick Chung

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
        * Whether or not grid locations should be outlined (in UI)
        * Color of cell or patch states
        * Shape of cells
*Visualization
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


### Notes/Assumptions

Assumptions or Simplifications:
*
Interesting data files:

Known Bugs:

Extra credit:


### Impressions

