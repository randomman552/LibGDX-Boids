package com.randomman552.boids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public abstract class Constants {
    public static final Vector2 WORLD_SIZE = new Vector2(16, 9);

    // Boid appearance settings
    public static final Vector2 BOID_SIZE = new Vector2(0.10f, 0.20f);
    public static final Color BOID_COLOR = new Color(1, 1, 1, 1);
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 1);

    // Sense groups to control Box2D collisions
    public static final short BOID_SENSE_GROUP = -1;
    public static final short BOID_COLLISION_GROUP = -2;
    public static final short MAP_EDGE_GROUP = BOID_SENSE_GROUP;

    // Boid behavior settings
    public static final int VELOCITY = 3;
    public static float SENSE_RADIUS = 1f;
    public static int ESCAPE_STEPS = 6;
    /**
     * Angle at which an obstacle in front of a boid must be avoided (in degrees)
     * For example, with a value of 15, -15 and 15 degrees will be checked,
     * if an obstacle is detected, the boid will attempt to divert away.
     */
    public static float INITIAL_ESCAPE_ANGLE = 15;
    /**
     * In degrees per second
     */
    public static int TURN_RATE = 240;
    public static float MIN_SEPARATION_DIST = 0.25f;
    public static float SEPARATION_FORCE_AT_MIN_DIST = 2;

    // Boid "desire" force scalars
    // Are used to scale behavior elements of boids (weighted avg)
    public static float SEPARATION_FORCE_SCALAR = 1f;
    public static float VELOCITY_MATCH_FORCE_SCALAR = 1f;
    public static float FLOCK_CENTER_FORCE_SCALAR = 1f;

    // Debug drawing variables
    public static boolean DRAW_SEPARATION_FORCE = false;
    public static final Color COLOR_SEPARATION_FORCE = new Color(0, 1, 0, 1);
    public static boolean DRAW_VELOCITY_MATCH_FORCE = false;
    public static final Color COLOR_VELOCITY_MATCH_FORCE = new Color(1, 0, 0, 1);
    public static boolean DRAW_FLOCK_CENTERING_FORCE = false;
    public static final Color COLOR_FLOCK_CENTERING_FORCE = new Color(0, 0, 1, 1);
    public static boolean DRAW_OBSTACLE_AVOIDANCE = false;
    public static final Color COLOR_OBSTACLE_AVOIDANCE_HIT = new Color(1, 1, 0, 1);
    public static final Color COLOR_OBSTACLE_AVOIDANCE_MISS = new Color(0, 1, 0, 1);
}
