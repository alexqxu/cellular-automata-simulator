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
        * Cell locations given that are outside the bounds of the grid's size (Config just crops)
        * Appropriate default values when parameter values are invalid or not given
* 
    * 


### Notes/Assumptions

Assumptions or Simplifications:

Interesting data files:

Known Bugs:

Extra credit:


### Impressions

