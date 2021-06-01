package com.randomman552.boids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.randomman552.boids.boid.Boid;
import com.randomman552.boids.boid.BoidContactListener;
import com.randomman552.boids.obstacles.MapEdge;
import com.randomman552.boids.ui.UIStage;

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
	private UIStage uiStage;
	private InputMultiplexer inputMultiplexer;

	public final Random random;
	public Box2DDebugRenderer box2DDebugRenderer;
	public World world;
	public ShapeRenderer shapeRenderer;
	public TextureRegion boidTexture;
	public TextureRegion obstacleTexture;
	public Skin skin;

	public Boids() {
		if (instance != null) {
			throw new RuntimeException("Attempt to instantiate multiple of singleton Boids class!");
		}
		this.random = new Random();

		instance = this;
	}

	@Override
	public void create () {
		// Load assets
		boidTexture = new TextureRegion(new Texture(Gdx.files.internal("boid.png")));
		obstacleTexture = new TextureRegion(new Texture(Gdx.files.internal("obstacle.png")));
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		// Create stage with static viewport size for boid world
		Viewport vp = new StretchViewport(Constants.WORLD_SIZE.x, Constants.WORLD_SIZE.y);
		stage = new Stage(vp);
		uiStage = new UIStage();

		// Multiplex input processors
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(uiStage);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0, 0, 1, 1);
		shapeRenderer.setAutoShapeType(true);

		// Start Box2D physics
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new BoidContactListener());
		box2DDebugRenderer = new Box2DDebugRenderer(false, false, false, false, false, false);
		box2DDebugRenderer.VELOCITY_COLOR.set(1, 1, 0, 1);

		// Spawn the 4 walls of our map and place them correctly.
		MapEdge[] edges = new MapEdge[4];
		for (int i = 0; i < edges.length; i++) {
			edges[i] = new MapEdge(i);
		}
		for (int i = 0; i < edges.length; i++) {
			edges[i].setPaired(edges[(i + 2) % edges.length]);
			stage.addActor(edges[i]);
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
		ScreenUtils.clear(Constants.BACKGROUND_COLOR);
		Gdx.gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
		shapeRenderer.begin();
		stage.act();
		shapeRenderer.end();

		stage.draw();
		box2DDebugRenderer.render(world, stage.getCamera().combined);
		uiStage.draw();

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
	}
	
	@Override
	public void dispose () {
		stage.dispose();
		instance = null;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height);
		uiStage.resize(width, height);
	}
}
