package com.randomman552.boids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/**
 * Singleton class representing the Boids simulation.
 */
public class Boids extends ApplicationAdapter {
	private static Boids instance;
	public static Boids getInstance() {
		return instance;
	}

	private Stage stage;
	private Box2DDebugRenderer box2DDebugRenderer;
	private final Random random;
	private final boolean debug;

	public World world;
	public TextureRegion boidTexture;

	public Boids(boolean debug) {
		if (instance != null) {
			throw new RuntimeException("Attempt to instantiate multiple of singleton Boids class!");
		}

		this.debug = debug;
		this.random = new Random();

		instance = this;
	}
	
	@Override
	public void create () {
		// Create stage with static viewport size
		Viewport vp = new StretchViewport(16, 9);
		stage = new Stage(vp);

		// Start Box2D physics
		world = new World(new Vector2(0, 0), true);
		if (debug) {
			stage.setDebugAll(true);
			// Draw ALL box2d debug
			box2DDebugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
		}

		// Load assets
		boidTexture = new TextureRegion(new Texture(Gdx.files.internal("boid.png")));

		// Spawn 100 boids at random points with random rotations
		for (int i = 0; i < 100; i++) {
			float x, y, rotation;
			x = random.nextFloat() * vp.getWorldWidth();
			y = random.nextFloat() * vp.getWorldHeight();
			rotation = random.nextFloat() * 360;

			Boid boid = new Boid(x, y, rotation);
			stage.addActor(boid);
		}

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);

		stage.act();

		stage.draw();
		if (debug) box2DDebugRenderer.render(world, stage.getCamera().combined);

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
	}
	
	@Override
	public void dispose () {
		stage.dispose();
		instance = null;
	}
}
