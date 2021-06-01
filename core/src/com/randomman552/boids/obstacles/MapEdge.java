package com.randomman552.boids.obstacles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;
import com.randomman552.boids.Boids;
import com.randomman552.boids.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Object to represent the edge of the map.
 * Automatically sets it's size and position according to the wall index given to it.
 * Index starts at 0 for the top wall and increases by 1 per clockwise rotation (so right wall is 1).
 */
public class MapEdge extends Obstacle {
    private MapEdge paired;
    private final boolean isHorizontal;
    private final ArrayList<Body> toTeleport = new ArrayList<>();
    private static final Map<Body, Float> coolDowns = new HashMap<>();

    public MapEdge(int index) {
        body = createBody(index);
        isHorizontal = index % 2 == 0;

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
        fixtureDef.filter.groupIndex = Constants.MAP_EDGE_GROUP;
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

    public void setPaired(MapEdge mapEdge) {
        paired = mapEdge;
    }

    public void teleportToPaired(Body body) {
        if (!onCoolDown(body)) {
            toTeleport.add(body);
            // Put on cool down for 10 frames
            coolDowns.put(body, (1f/60f) * 10f);
        }
    }

    private boolean onCoolDown(Body body) {
        return coolDowns.containsKey(body);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Teleport boids to other side of map
        for (Body boid: toTeleport) {
            Vector2 boidPos = boid.getPosition();

            // Calculate opposing position to teleport to
            if (isHorizontal) {
                boidPos.y = Constants.WORLD_SIZE.y - boidPos.y;
            } else {
                boidPos.x = Constants.WORLD_SIZE.x - boidPos.x;
            }
            boid.setTransform(boidPos.x, boidPos.y, body.getAngle());
        }
        toTeleport.clear();

        // Process cool downs
        Iterator<Map.Entry<Body, Float>> cdIter = coolDowns.entrySet().iterator();
        while (cdIter.hasNext()) {
            Map.Entry<Body, Float> entry = cdIter.next();
            entry.setValue(entry.getValue() - delta);
            if (entry.getValue() <= 0) {
                cdIter.remove();
            }
        }
    }
}
