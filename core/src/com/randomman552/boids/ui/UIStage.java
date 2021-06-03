package com.randomman552.boids.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.randomman552.boids.Boids;
import com.randomman552.boids.Constants;

/**
 * Class defining all on screen ui elements for controlling boids.
 */
public class UIStage extends Stage {
    /**
     * Change listener that is used to update an instance of Color depending on the positions of passed sliders.
     */
    private static class ColorUpdateListener extends ChangeListener {
        private final Color color;
        private final Slider rSlider, gSlider, bSlider, aSlider;

        public ColorUpdateListener(Color color, Slider rSlider, Slider gSlider, Slider bSlider, Slider aSlider) {
            this.color = color;
            this.rSlider = rSlider;
            this.gSlider = gSlider;
            this.bSlider = bSlider;
            this.aSlider = aSlider;
        }

        public ColorUpdateListener(Color color, Slider rSlider, Slider gSlider, Slider bSlider) {
            this(color, rSlider, gSlider, bSlider, null);
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (aSlider != null)
                color.set(rSlider.getValue(), gSlider.getValue(), bSlider.getValue(), aSlider.getValue());
            else
                color.set(rSlider.getValue(), gSlider.getValue(), bSlider.getValue(), color.a);
        }
    }

    protected final TextButton openButton;
    protected final Table table;

    public UIStage() {
        super();
        Viewport vp = new ScreenViewport();
        setViewport(vp);

        Skin skin = Boids.getInstance().skin;

        openButton = new TextButton("Open options", skin);
        openButton.setWidth(300);
        openButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                table.setVisible(!table.isVisible());
                openButton.setText((table.isVisible()) ? "Close options": "Open options");
            }
        });
        addActor(openButton);

        // region Create table layout
        table = new Table();
        float tableHeight = 0;

        // region Force sliders
        Label forceSliderLabel = new Label("Boid force scalars", skin);
        Label sepForceSliderLabel = new Label("Separation:", skin);
        Slider sepForceSlider = new Slider(0, 1, 0.01f,false, skin);
        Label velMatchForceSliderLabel = new Label("Velocity matching:", skin);
        Slider velMatchForceSlider = new Slider(0, 1, 0.01f,false, skin);
        Label flockCenterForceSliderLabel = new Label("Flock centering:", skin);
        Slider flockCenterForceSlider = new Slider(0, 1, 0.01f,false, skin);

        sepForceSlider.setValue(Constants.SEPARATION_FORCE_SCALAR);
        velMatchForceSlider.setValue(Constants.VELOCITY_MATCH_FORCE_SCALAR);
        flockCenterForceSlider.setValue(Constants.FLOCK_CENTER_FORCE_SCALAR);

        // region Add input listeners
        sepForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.SEPARATION_FORCE_SCALAR = slider.getValue();
                }
            }
        });
        velMatchForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.VELOCITY_MATCH_FORCE_SCALAR = slider.getValue();
                }
            }
        });
        flockCenterForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.FLOCK_CENTER_FORCE_SCALAR = slider.getValue();
                }
            }
        });
        // endregion

        tableHeight += Math.max(sepForceSlider.getHeight(), sepForceSliderLabel.getHeight()) * 4;

        table.row();
        table.add(forceSliderLabel).colspan(2);
        table.row();
        table.add(sepForceSliderLabel, sepForceSlider);
        table.row();
        table.add(velMatchForceSliderLabel, velMatchForceSlider);
        table.row();
        table.add(flockCenterForceSliderLabel, flockCenterForceSlider);
        // endregion

        // region Debug draw options
        Label debugHeaderLabel = new Label("Debug draw options:", skin);
        CheckBox drawPhysicsDebugCheckbox = new CheckBox("Draw physics", skin);
        // Avoidance drawing checkbox
        CheckBox drawObstacleAvoidanceCheckbox = new CheckBox("Draw avoidance", skin);
        // Force drawing checkboxes
        CheckBox drawSeparationForceCheckbox = new CheckBox("Draw separation force", skin);
        CheckBox drawVelMatchForceCheckbox = new CheckBox("Draw velocity match force", skin);
        CheckBox drawCenteringForceCheckbox = new CheckBox("Draw flock centering force", skin);

        drawObstacleAvoidanceCheckbox.setChecked(Constants.DRAW_OBSTACLE_AVOIDANCE);
        drawSeparationForceCheckbox.setChecked(Constants.DRAW_SEPARATION_FORCE);
        drawVelMatchForceCheckbox.setChecked(Constants.DRAW_VELOCITY_MATCH_FORCE);
        drawCenteringForceCheckbox.setChecked(Constants.DRAW_FLOCK_CENTERING_FORCE);

        tableHeight += debugHeaderLabel.getHeight() + drawPhysicsDebugCheckbox.getHeight() * 5;

        // region Add input listeners
        drawObstacleAvoidanceCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = ((CheckBox) actor);
                Constants.DRAW_OBSTACLE_AVOIDANCE = checkBox.isChecked();
            }
        });
        drawPhysicsDebugCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = ((CheckBox) actor);
                Box2DDebugRenderer debugRenderer = Boids.getInstance().box2DDebugRenderer;
                debugRenderer.setDrawVelocities(checkBox.isChecked());
                debugRenderer.setDrawBodies(checkBox.isChecked());
            }
        });
        drawSeparationForceCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = ((CheckBox) actor);
                Constants.DRAW_SEPARATION_FORCE = checkBox.isChecked();
            }
        });
        drawVelMatchForceCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = ((CheckBox) actor);
                Constants.DRAW_VELOCITY_MATCH_FORCE = checkBox.isChecked();
            }
        });
        drawCenteringForceCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checkBox = ((CheckBox) actor);
                Constants.DRAW_FLOCK_CENTERING_FORCE = checkBox.isChecked();
            }
        });
        // endregion

        table.row();
        table.add(debugHeaderLabel).colspan(2);

        table.row();
        table.add(drawPhysicsDebugCheckbox).colspan(2).align(Align.left);
        table.row();
        table.add(drawObstacleAvoidanceCheckbox).colspan(2).align(Align.left);
        table.row();
        table.add(drawSeparationForceCheckbox).colspan(2).align(Align.left);
        table.row();
        table.add(drawVelMatchForceCheckbox).colspan(2).align(Align.left);
        table.row();
        table.add(drawCenteringForceCheckbox).colspan(2).align(Align.left);
        // endregion

        // region Boid color options
        Label boidColors = new Label("Boid Colors", skin);
        Label boidColorRedLabel = new Label("Red:", skin);
        Slider boidColorRedSlider = new Slider(0, 1, 0.01f,false, skin);
        Label boidColorGreenLabel = new Label("Green:", skin);
        Slider boidColorGreenSlider = new Slider(0, 1, 0.01f,false, skin);
        Label boidColorBlueLabel = new Label("Blue:", skin);
        Slider boidColorBlueSlider = new Slider(0, 1, 0.01f,false, skin);
        Label boidColorAlphaLabel = new Label("Alpha:", skin);
        Slider boidColorAlphaSlider = new Slider(0, 1, 0.01f,false, skin);

        boidColorRedSlider.setValue(Constants.BOID_COLOR.r);
        boidColorGreenSlider.setValue(Constants.BOID_COLOR.g);
        boidColorBlueSlider.setValue(Constants.BOID_COLOR.b);
        boidColorAlphaSlider.setValue(Constants.BOID_COLOR.a);

        // region Input listeners
        ColorUpdateListener boidColorUpdateListener = new ColorUpdateListener(
                Constants.BOID_COLOR,
                boidColorRedSlider,
                boidColorGreenSlider,
                boidColorBlueSlider,
                boidColorAlphaSlider
        );
        boidColorRedSlider.addListener(boidColorUpdateListener);
        boidColorGreenSlider.addListener(boidColorUpdateListener);
        boidColorBlueSlider.addListener(boidColorUpdateListener);
        boidColorAlphaSlider.addListener(boidColorUpdateListener);
        // endregion

        tableHeight += 5 * Math.max(boidColorBlueLabel.getHeight(), boidColorBlueSlider.getHeight());

        table.row();
        table.add(boidColors).colspan(2).align(Align.center);
        table.row();
        table.add(boidColorRedLabel, boidColorRedSlider);
        table.row();
        table.add(boidColorGreenLabel, boidColorGreenSlider);
        table.row();
        table.add(boidColorBlueLabel, boidColorBlueSlider);
        table.row();
        table.add(boidColorAlphaLabel, boidColorAlphaSlider);
        // endregion

        // region Background color options
        Label backgroundColorsLabel = new Label("Background colors", skin);
        Label backgroundColorsRedLabel = new Label("Red", skin);
        Slider backgroundColorsRedSlider = new Slider(0, 1, 0.01f,false, skin);
        Label backgroundColorsGreenLabel = new Label("Green", skin);
        Slider backgroundColorsGreenSlider = new Slider(0, 1, 0.01f,false, skin);
        Label backgroundColorsBlueLabel = new Label("Blue", skin);
        Slider backgroundColorsBlueSlider = new Slider(0, 1, 0.01f,false, skin);

        backgroundColorsRedSlider.setValue(Constants.BACKGROUND_COLOR.r);
        backgroundColorsGreenSlider.setValue(Constants.BACKGROUND_COLOR.g);
        backgroundColorsBlueSlider.setValue(Constants.BACKGROUND_COLOR.b);

        // region Input listeners
        ColorUpdateListener backgroundColorUpdateListener = new ColorUpdateListener(
                Constants.BACKGROUND_COLOR,
                backgroundColorsRedSlider,
                backgroundColorsGreenSlider,
                backgroundColorsBlueSlider
        );
        backgroundColorsRedSlider.addListener(backgroundColorUpdateListener);
        backgroundColorsGreenSlider.addListener(backgroundColorUpdateListener);
        backgroundColorsBlueSlider.addListener(backgroundColorUpdateListener);
        // endregion

        tableHeight += 4 * Math.max(boidColorBlueLabel.getHeight(), boidColorBlueSlider.getHeight());

        table.row();
        table.add(backgroundColorsLabel).colspan(2).align(Align.center);
        table.row();
        table.add(backgroundColorsRedLabel, backgroundColorsRedSlider);
        table.row();
        table.add(backgroundColorsGreenLabel, backgroundColorsGreenSlider);
        table.row();
        table.add(backgroundColorsBlueLabel, backgroundColorsBlueSlider);
        // endregion

        table.setWidth(300);
        table.setHeight(tableHeight);
        addActor(table);
        table.setVisible(false);
        // endregion

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        getViewport().update(width, height);

        openButton.setPosition(-Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f - openButton.getHeight());
        table.setPosition(-Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f - openButton.getHeight() - table.getHeight());
    }
}
