package com.randomman552.boids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public abstract class Constants {
    public static final int BOID_VELOCITY = 3;
    public static final Vector2 BOID_SIZE = new Vector2(0.10f, 0.20f);
    public static final float BOID_SENSE_RADIUS = 1f;
    public static final int BOID_ESCAPE_STEPS = 6;
    /**
     * In degrees per second
     */
    public static final int BOID_TURN_RATE = 240;
    public static final float MIN_SEPARATION_DIST = 0.25f;
    public static final float SEPARATION_FORCE_AT_MIN_DIST = 2;

    // Sense groups to control collisions
    public static final short BOID_SENSE_GROUP = -1;
    public static final short BOID_COLLISION_GROUP = -2;
    public static final short MAP_EDGE_GROUP = BOID_SENSE_GROUP;

    // Boid "desire" force scalars
    // Are used to scale behavior elements of boids (weighted avg)
    public static float SEPARATION_FORCE = 1f;
    public static float VELOCITY_MATCH_FORCE = 1f;
    public static float FLOCK_CENTERING_FORCE = 1f;

    // Debug drawing variables
    public static boolean DRAW_SEPARATION_FORCE = false;
    public static boolean DRAW_VELOCITY_MATCH_FORCE = false;
    public static boolean DRAW_FLOCK_CENTERING_FORCE = false;
    public static Color COLOR_SEPARATION_FORCE = new Color(0, 1, 0, 1);
    public static Color COLOR_VELOCITY_MATCH_FORCE = new Color(1, 0, 0, 1);
    public static Color COLOR_FLOCK_CENTERING_FORCE = new Color(0, 0, 1, 1);


    public static final Vector2 WORLD_SIZE = new Vector2(16, 9);
}
