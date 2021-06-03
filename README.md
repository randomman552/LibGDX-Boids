# LibGDX-Boids
![Simulation in progress](.github/preview.gif "Simulation in progress")

Inspired by a video and similar project by Sebastian Lague.\
His video can be found [here](https://www.youtube.com/watch?v=bqtqltqcQhw).

The rules that define boids are given by a report he references,
which can be found [here](https://www.cs.toronto.edu/~dt/siggraph97-course/cwr87/).

## What is a boid?
Boids are an algorithmic approach to simulation of flocking behaviour such as that found in flocks of birds or schools of fish.

They achieve this by following a series of simple rules which are defined in the report above 
and are as follows:
- Collision Avoidance: Avoid collisions with nearby flock mates.
- Velocity Matching: Attempt to match velocity with nearby flock mates.
- Flock Centering: Attempt to stay close to nearby flock mates.

These are in order of decreasing presidence, 
meaning that Collision Avoidance is the most important, and Flock Centering is the least important.

Currently, this implementation handles avoidance of collisions with other objects, this is carried out by performing 
ray casts at increasing angles until an escape route can be found. The boid then turns towards this escape vector.

## Spatial partitioning
In an attempt to improve performance of this boid implementation, we make use of spatial partitioning
to prevent us from looping over every other boid for each frame, this implementation uses the 
underlying Box2D physics engine so that boids can only "perceive" other boids that are within a 
certain radius of them, this radius is defined in [Constants.java](core/src/com/randomman552/boids/Constants.java).

## Customising behaviour
The behaviour of the boids can be controlled by editing the values in [Constants.java](core/src/com/randomman552/boids/Constants.java)

The values in [Constants.java](core/src/com/randomman552/boids/Constants.java) can be adjusted during runtime using the 
options menu which can be opened with the button in the top left of the screen.\ 
The values are not saved on program exit.

## Running the simulation
The program can be run by downloading the Jar archive located in [releases](https://github.com/randomman552/LibGDX-Boids/releases/tag/latest).\
You will require Java 11 in order to execute the program which you can do using the following command:
```shell
java -jar Boids.jar
```