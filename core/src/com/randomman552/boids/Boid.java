package com.randomman552.boids;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

public class Boid extends Actor implements Disposable {
    private static final int VELOCITY = 4;
    private static final Vector2 SIZE = new Vector2(0.10f, 0.20f);

    private final Body body;

    public Boid() {
        this(0, 0);
    }

    public Boid(float x, float y) {
        this(x, y, 0);
    }

    public Boid(float x, float y, float rotation) {
        setSize(SIZE.x, SIZE.y);
        setOrigin(Align.center);

        // Define body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = Boids.getInstance().world.createBody(bodyDef);
        body.setUserData(this);
        body.setLinearVelocity(0, VELOCITY);

        // Set position and rotation AFTER body creation to prevent null pointer
        setPosition(x - getOriginX(), y - getOriginY());
        setRotation(rotation);
    }

    @Override
    public void act(float delta) {
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
