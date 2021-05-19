package com.randomman552.boids.boid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Align;
import com.randomman552.boids.Boids;
import com.randomman552.boids.Constants;
import com.randomman552.boids.obstacles.Obstacle;
import com.randomman552.boids.util.BodyLinkedActor;

import java.util.ArrayList;

public class Boid extends BodyLinkedActor {
    private static class BoidRayCastCallback implements RayCastCallback {
        private final Boid boid;

        public BoidRayCastCallback(Boid boid) {
            this.boid = boid;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // Ignore sensors
            if (fixture.isSensor())
                return -1;

            boid.addRayCastFixture(fixture);
            return 0;
        }
    }

    private final ArrayList<Body> boids = new ArrayList<>();
    private final ArrayList<Body> obstacles = new ArrayList<>();

    private final ArrayList<Fixture> rayCastFixtures = new ArrayList<>();

    // Multi use vectors (prevent re-creation of objects)
    private final Vector2 sensePoint = new Vector2();
    private final Vector2 centerPoint = new Vector2();

    public Boid() {
        this(0, 0);
    }

    public Boid(float x, float y) {
        this(x, y, 0);
    }

    public Boid(float x, float y, float rotation) {
        setSize(Constants.BOID_SIZE.x, Constants.BOID_SIZE.y);
        setOrigin(Align.center);

        // Define body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = Boids.getInstance().world.createBody(bodyDef);
        body.setUserData(this);
        body.setLinearVelocity(0, Constants.BOID_VELOCITY);

        // region Create fixtures

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        PolygonShape polygonShape = new PolygonShape();

        // Primary sense circle
        circleShape.setRadius(Constants.BOID_SENSE_RADIUS);
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.groupIndex = Constants.BOID_SENSE_CATEGORY;
        body.createFixture(fixtureDef);

        // Central collision body
        polygonShape.setAsBox(getWidth()/2, getHeight()/2);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.groupIndex = 0;
        body.createFixture(fixtureDef);

        // endregion

        // Set position and rotation AFTER body creation to prevent null pointer
        setPosition(x - getOriginX(), y - getOriginY());
        setRotation(rotation);

        // Dispose shapes
        circleShape.dispose();
        polygonShape.dispose();
    }


    /**
     * Add a body which the Boid can perceive and react to.
     * @param toAdd The body to add.
     */
    public void addPerceived(Body toAdd) {
        if (toAdd.getUserData() instanceof Boid) {
            boids.add(toAdd);
        }
        else if (toAdd.getUserData() instanceof Obstacle) {
            obstacles.add(toAdd);
        }
    }

    /**
     * Remove a body which the Boid can no longer perceive and react to.
     * @param toRemove The body to remove.
     */
    public void removePerceived(Body toRemove) {
        if (toRemove.getUserData() instanceof Boid) {
            boids.remove(toRemove);
        }
        else if (toRemove.getUserData() instanceof Obstacle) {
            obstacles.remove(toRemove);
        }
    }


    private void addRayCastFixture(Fixture fixture) {
        rayCastFixtures.add(fixture);
    }

    private boolean rayCastFixtureDetected() {
        boolean result = rayCastFixtures.size() > 0;
        rayCastFixtures.clear();
        return result;
    }


    private boolean obstaclesToAvoid() {
        return obstacles.size() > 0;
    }

    /**
     * Get center coordinates of this boid.
     * NOTE: Always returns the same vector, not a new one.
     * @return Center point of this boid.
     */
    private Vector2 getCenterPoint() {
        centerPoint.set(getX() + getOriginX(), getY() + getOriginY());
        return centerPoint;
    }

    /**
     * Get coordinates of a point along the sensor radius (in front of the boid).
     * NOTE: Always returns the same vector, not a new one.
     * @return coordinates of point in front of the boid at the sensor radius distance.
     */
    private Vector2 getSensePoint() {
        return getSensePoint(0);
    }

    /**
     * Get coordinates of a point along the sensor radius at the specified angle.
     * NOTE: Always returns the same vector, not a new one.
     * @param degrees The angle at which the sense point should be placed.
     * @return coordinates of point at the specified angle in front of the boid.
     */
    private Vector2 getSensePoint(float degrees) {
        sensePoint.set(0, Constants.BOID_SENSE_RADIUS);
        sensePoint.rotateDeg(getRotation());
        sensePoint.rotateDeg(degrees);
        sensePoint.add(getCenterPoint());
        return sensePoint;
    }


    private Vector2 getVelocity() {
        Vector2 velocity = new Vector2(0, Constants.BOID_VELOCITY);
        velocity.rotateDeg(getRotation());
        return velocity;
    }


    private void drawRay(Vector2 fromPoint, Vector2 toPoint, boolean collides) {
        ShapeRenderer shapeRenderer = Boids.getInstance().shapeRenderer;
        if (collides) {
            shapeRenderer.setColor(1, 0, 0, 1);
        } else {
            shapeRenderer.setColor(0, 0, 1, 1);
        }
        shapeRenderer.line(fromPoint, toPoint);
    }


    public void turnTowards(float degrees) {
        float rotationDiff = getRotation() - degrees;
        float rotationAmount = Gdx.graphics.getDeltaTime() * Constants.BOID_TURN_RATE * Math.copySign(1f, rotationDiff);
        setRotation(getRotation() - rotationAmount);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        avoidObstacles();

        body.setLinearVelocity(getVelocity());
    }

    /**
     * Obstacle avoidance behavior method.
     * Should be called from the act method.
     */
    private void avoidObstacles() {
        if (obstaclesToAvoid()) {
            // RayCast forward to see if path is clear.
            World world = Boids.getInstance().world;
            Vector2 fromPoint = getCenterPoint();
            Vector2 sensePoint = getSensePoint();

            world.rayCast(new BoidRayCastCallback(this), fromPoint, sensePoint);
            if (rayCastFixtureDetected()) {
                drawRay(fromPoint, sensePoint, true);
                for (int i = 0; i < Constants.BOID_ESCAPE_STEPS; i++) {
                    float degrees = (180f / Constants.BOID_ESCAPE_STEPS) * i;

                    // Left side ray cast
                    sensePoint = getSensePoint(degrees);
                    world.rayCast(new BoidRayCastCallback(this), fromPoint, sensePoint);
                    if (!rayCastFixtureDetected()) {
                        drawRay(fromPoint, sensePoint, false);
                        turnTowards(getRotation() + degrees);
                        break;
                    } else {
                        drawRay(fromPoint, sensePoint, true);
                    }

                    // Right side ray cast
                    sensePoint = getSensePoint(-degrees);
                    world.rayCast(new BoidRayCastCallback(this), fromPoint, sensePoint);
                    if (!rayCastFixtureDetected()) {
                        drawRay(fromPoint, sensePoint, false);
                        turnTowards(getRotation() - degrees);
                        break;
                    } else {
                        drawRay(fromPoint, sensePoint, true);
                    }
                }
            } else {
                drawRay(fromPoint, sensePoint, false);
            }
        }
    }
}
