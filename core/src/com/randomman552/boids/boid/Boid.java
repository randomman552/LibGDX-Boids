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
import java.util.Vector;

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

    private final ArrayList<Body> boids = new ArrayList<>(100);
    private final ArrayList<Body> obstacles = new ArrayList<>(100);

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

        // Set initial velocity based on rotation
        Vector2 vel = body.getLinearVelocity();
        vel.set(Constants.BOID_VELOCITY, 0);
        vel.rotateDeg(rotation);
        setVelocity(vel);

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
        if (toAdd.getUserData() instanceof Boid && !boids.contains(toAdd)) {
            boids.add(toAdd);
        }
        else if (toAdd.getUserData() instanceof Obstacle && !obstacles.contains(toAdd)) {
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


    /**
     * Utility function which clamp the angle of vec to be within the given number of degrees of ref.
     * @param vec - The vector to clamp
     * @param ref - Reference to calculate angle too
     * @param degreesVariance - Maximum degrees of variance
     */
    private Vector2 clampAngle(Vector2 vec, Vector2 ref, float degreesVariance) {
        float turnAngle = vec.angleDeg(ref);
        if (Math.abs(turnAngle) > degreesVariance) {
            turnAngle = -Math.copySign(degreesVariance, turnAngle -180);
        }
        vec.set(ref);
        vec.rotateDeg(turnAngle);

        return vec;
    }

    /**
     * @param delta Time between this frame and the last.
     * @param vec The vector to turn towards (MUST BE NORMALISED)
     */
    public void turnTowards(float delta, Vector2 vec) {
        if (vec.len() <= 0) {
            setVelocity(getVelocity());
            return;
        }
        vec = clampAngle(vec, getVelocity(), delta * Constants.BOID_TURN_RATE);
        setVelocity(vec);
    }

    /**
     * @param vec The vector to turn towards (MUST BE NORMALISED)
     */
    public void turnTowards(Vector2 vec) {
        turnTowards(Gdx.graphics.getDeltaTime(), vec);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 vel = new Vector2();

        // https://www.cs.toronto.edu/~dt/siggraph97-course/cwr87/

        // region Calculate velocity match AND flock centering forces
        Vector2 flockAvgVel = new Vector2(this.body.getLinearVelocity());
        Vector2 flockCenter = new Vector2(this.body.getPosition());

        for (Body body: boids) {
            flockAvgVel.add(body.getLinearVelocity());
            flockCenter.add(body.getPosition());
        }

        flockAvgVel.scl(1f/(boids.size() + 1));
        flockCenter.scl(1f/(boids.size() + 1));

        // Calculate flock centering force (vector from current position to flock center)
        Vector2 flockCenterForce = flockCenter.sub(this.body.getPosition());

        // Normalise and scale both vectors to respective force scalars
        flockAvgVel.nor().scl(Constants.VELOCITY_MATCH_FORCE);
        flockCenterForce.nor().scl(Constants.FLOCK_CENTERING_FORCE);

        // endregion

        // Calculate desired velocity
        vel.set(0, 0);
        vel.add(flockCenterForce);

        turnTowards(delta, vel);
    }

    /**
     * Obstacle avoidance behavior method.
     * Should be called from the act method.
     */
    /*private void avoidObstacles() {
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
    }*/
}
