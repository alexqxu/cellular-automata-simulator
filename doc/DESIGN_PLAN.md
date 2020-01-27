# Simulation Design Plan
### Team Number 11
### Names: Maverick Chung, Alex Oesterling, Alex Xu

## Introduction
The main problem we are trying to solve is how to create a simulation program that is flexible enough to take different configurations of cells, as well as different rules for different types of simulations (game of life, percolation, fire, etc).
Design goals of the project include making the initial configuration of cells and rules flexible, allowing for changes to the how the visualizer displays the simulation (flexibility in customizing the appearance), and flexibility in changing simulation parameters, such as the speed of updating the simulation. From a backend perspective, design goals of this project are for the type of cell and simulation to be swappable via new configuration files. In addition, another design goal is that the data structures used can be changed if new information or requirements arise later along in the project timeline. Finally, if a component of one “area” of the project (Configuration, Simulation, Visualization) needs to be changed, its modification should not affect the functionality of another component.

To achieve these goals, we decided that it would make the most sense to separate the program into different classes that are responsible for different aspects of the simulation. Thus, the specific implementation of another aspect of the program (e.g. how individual cells update) does not affect the functioning of the rest of the program (e.g. how the cells are displayed in the GUI) without major changes (this is closed).

Furthermore, certain information will be public/open between the different parts of the program, while others will be private/closed. For example, the cell module will have an abstract Cell class which is extended by the different cells used in each different simulation. This means if another simulation is added, all that it takes to adopt is creating a new cell class with rules corresponding to this new simulation. Our other classes would not benefit from abstraction because they all serve one purpose and do not have a requirement for significant flexibility dictating an abstraction; creating an abstract Main class which can be extended by one main which can handle user mouse clicks to update the status of the cells and one which randomly changes the status of the cells seems to be less useful than just adding additional methods such as “handleMouse()” into the singular main method. Another way we can close our methods is by making the configuration class create the grid, instead of taking the XML data, passing it into main, and then having the main class use that data to create the grid and cells. If the configuration class handles creating the grid and cells, then the information processed by the XML data stays within the configuration class and so any changes to the XML format and processing do not affect the code written in the main class and grid class.

In terms of public and private information, the visualization aspect of the program would not need to know how many cells are neighbors to a certain cell; that information would be closed to the visualizer. Another example is that each individual cell in the grid would not need to know whether or not it is in a full neighborhood of other cells, or a half-neighborhood. That information would be closed to the grid only. On the other hand, an example of information that would be open would be the state of a cell. Each cell would need to check the state of some other cells before updating. Thus this information would need to be open and publically available to these other parts of the program, and not just private to each individual cell.

## Overview

We will have four different classes for this project. A visualization class will be our main class, and it will take information regarding color from the Grid class (locations of where to put certain colors). The visualization class is responsible for GUI, user inputs into the GUI, and drawing out the simulation. 

The Grid class will hold the data structure that represents the grid of cells for the program. The specific implementation of what type of data structure will be flexible, and other parts of the program/other classes would not depend on the implementation of a specific data structure. 

The Cell class represents a single cell in the grid, and holds information regarding that particular cell (i.e. state, color, previous state, current state). It is also responsible for updating information regarding itself.

Finally, a configuration class is responsible for loading and reading from a text file.

First, configuration class is used to create the initial configuration of cells and define what simulation the program will run. The configuration class will load a file the user has specified by interacting with the GUI created in the main class. The main class then runs the simulation (when the user specifies it to do so, by clicking a button). To step through the simulation, the main class calls stepGrid(). update() is then called in the Grid class, which calls planUpdate() and then update(). This updates the cells. The main class/visualizer now requires locations and colors to draw. getColorGrid() returns this information, and the visualization redraws the next frame.

Please see CRC Cards Below for more information regarding these classes.

**2 Different Implementations (for communicating color to the Main):**

Our chosen implementation for the data structure representing the grid of cells is an ArrayList. However, other implementations such as a 2D array, other types of lists, or Map could also work as well. One of our aims in designing our code is to make other classes/methods implementation agnostic. This means that regardless of what data structure we use here, it would not require changing the implementation of other classes. For example, for the visualizer/main class to work, it takes a 2D array of Color objects from the getColorGrid() method in the Grid class. So, if the internal implementation of the Grid class changes, as long as getColorGrid() has the same return type (which it will), the signature will not change and no changes are needed in the visualizer/main class. In addition, our usage of ArrayLists can be generalized to Lists. So, by using Lists for the signature in the methods in the Grid Class, other types of lists can be implemented without even changing the signature of these other methods.

Finally, we have considered alternatives that perhaps could increase the flexibility of our code in handling different implementations of the grid data structure. One alternative to assemble the 2D array of Color objects in the main/visualizer class instead of doing it in the Grid class. This way, only the signature of a cell is required for that method, which does not change (as Cells are objects that we define ourselves).

## User Interface


## Design Details


## Design Considerations

#### Components

#### Use Cases


## Team Responsibilities

 * Team Member #1

 * Team Member #2

 * Team Member #3

