package com.randomman552.boids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.randomman552.boids.boid.Boid;
import com.randomman552.boids.boid.BoidContactListener;
import com.randomman552.boids.obstacles.MapEdge;

import java.util.Map;
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
	public ShapeRenderer shapeRenderer;
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
		Viewport vp = new StretchViewport(Constants.WORLD_SIZE.x, Constants.WORLD_SIZE.y);
		stage = new Stage(vp);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0, 0, 1, 1);
		shapeRenderer.setAutoShapeType(true);

		// Start Box2D physics
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new BoidContactListener());
		if (debug) {
			stage.setDebugAll(true);
			box2DDebugRenderer = new Box2DDebugRenderer(true, true, true, true, false, true);
		}

		// Load assets
		boidTexture = new TextureRegion(new Texture(Gdx.files.internal("boid.png")));

		// Spawn the 4 walls of our map and place them correctly.
		for (int i = 0; i < 4; i++) {
			stage.addActor(new MapEdge(i));
		}

		// Spawn boids at random points with random rotations
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

		shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
		shapeRenderer.begin();
		stage.act();
		shapeRenderer.end();

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
