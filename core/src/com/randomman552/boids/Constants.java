package com.randomman552.boids;

import com.badlogic.gdx.math.Vector2;

public abstract class Constants {
    public static final int VELOCITY = 1;
    public static final Vector2 SIZE = new Vector2(0.10f, 0.20f);
    public static final short BOID_SENSE_CATEGORY = -1;
    public static final float BOID_SENSE_RADIUS = 1f;
    public static final int BOID_ESCAPE_STEPS = 6;

    public static final Vector2 WORLD_SIZE = new Vector2(16, 9);
}
