package com.randomman552.boids.obstacles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.randomman552.boids.Boids;
import com.randomman552.boids.util.BodyLinkedActor;

public abstract class Obstacle extends BodyLinkedActor {
    @Override
    protected TextureRegion getFrame() {
        return Boids.getInstance().obstacleTexture;
    }
}
