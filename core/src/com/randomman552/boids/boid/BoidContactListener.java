package com.randomman552.boids.boid;

import com.badlogic.gdx.physics.box2d.*;

public class BoidContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();

        // Add perceived data to Boid instances
        if (body1.getUserData() instanceof Boid) {
            ((Boid) body1.getUserData()).addPerceived(body2);
        }
        if (body2.getUserData() instanceof Boid) {
            ((Boid) body2.getUserData()).addPerceived(body1);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();

        // Add perceived data to Boid instances
        if (body1.getUserData() instanceof Boid) {
            ((Boid) body1.getUserData()).removePerceived(body2);
        }
        if (body2.getUserData() instanceof Boid) {
            ((Boid) body2.getUserData()).removePerceived(body1);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
