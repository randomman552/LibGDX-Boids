package com.randomman552.boids.obstacles;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Align;
import com.randomman552.boids.Boids;
import com.randomman552.boids.Constants;

/**
 * Object to represent the edge of the map.
 * Automatically sets it's size and position according to the wall index given to it.
 * Index starts at 0 for the top wall and increases by 1 per clockwise rotation (so right wall is 1).
 */
public class MapEdge extends Obstacle {
    public MapEdge(int index) {
        body = createBody(index);

        switch (index) {
            case 0:
                setOrigin(Align.topRight);
                setPosition(0, Constants.WORLD_SIZE.y);
                break;
            case 1:
                setOrigin(Align.topRight);
                setPosition(Constants.WORLD_SIZE.x, 0);
                break;
            case 2:
                setOrigin(Align.bottomLeft);
                setPosition(0, -1);
                break;
            case 3:
                setOrigin(Align.bottomLeft);
                setPosition(-1, 0);
                break;
        }
    }

    protected Body createBody(int index) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape polyShape = new PolygonShape();

        fixtureDef.shape = polyShape;
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Create correct size based on wall index (even is top, odd is side).
        if (index % 2 == 0) {
            polyShape.setAsBox(Constants.WORLD_SIZE.x, 1);
            setSize(Constants.WORLD_SIZE.x, 1);
        } else {
            polyShape.setAsBox(1, Constants.WORLD_SIZE.y);
            setSize(1, Constants.WORLD_SIZE.y);
        }

        Body body = Boids.getInstance().world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setUserData(this);
        return body;
    }
}
