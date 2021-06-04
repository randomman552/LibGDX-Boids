package com.randomman552.boids.obstacles;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Align;
import com.randomman552.boids.Boids;

public class RectangleObstacle extends Obstacle {
    public RectangleObstacle() {
        this(0, 0);
    }

    public RectangleObstacle(float x, float y) {
        this(x, y, 1, 1);
    }

    public RectangleObstacle(float x, float y, float width, float height) {
        super();
        setSize(width, height);
        setOrigin(Align.center);
        body = createBody(width, height);
        setPosition(x, y, Align.center);
    }

    private Body createBody(float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        Body body = Boids.getInstance().world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();

        polygonShape.setAsBox(width/2, height/2);
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);

        return body;
    }
}
