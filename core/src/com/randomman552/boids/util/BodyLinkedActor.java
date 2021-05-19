package com.randomman552.boids.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.randomman552.boids.Boids;

public class BodyLinkedActor extends Actor implements Disposable {
    protected Body body;

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(body.getPosition().x - getOriginX(), body.getPosition().y - getOriginY());
        setRotation((float) Math.toDegrees(body.getAngle()));
    }

    @Override
    protected void positionChanged() {
        body.setTransform(getX() + getOriginX(), getY() + getOriginY(), body.getAngle());
    }

    @Override
    protected void rotationChanged() {
        body.setTransform(body.getPosition().x, body.getPosition().y, (float) Math.toRadians(getRotation()));
        body.setLinearVelocity(body.getLinearVelocity().rotateDeg(getRotation()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Boids.getInstance().boidTexture,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation()
        );
    }

    @Override
    public void dispose() {
        Boids.getInstance().world.destroyBody(body);
    }
}
