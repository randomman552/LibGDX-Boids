package com.randomman552.boids.obstacles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.randomman552.boids.Boids;
import com.randomman552.boids.Constants;
import com.randomman552.boids.util.BodyLinkedActor;

public abstract class Obstacle extends BodyLinkedActor {
    @Override
    protected TextureRegion getFrame() {
        return Boids.getInstance().obstacleTexture;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setColor(Constants.FOREGROUND_COLOR);
        super.draw(batch, parentAlpha);
    }
}
