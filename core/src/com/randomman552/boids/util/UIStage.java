package com.randomman552.boids.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    TextButton openButton;
    Table table;

    public UIStage() {
        super();
        Viewport vp = new ScreenViewport();
        setViewport(vp);

        Skin skin = Boids.getInstance().skin;

        openButton = new TextButton("Open params", skin);
        openButton.setWidth(300);
        openButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                table.setVisible(!table.isVisible());
                openButton.setText((table.isVisible()) ? "Close params": "Open params");
            }
        });
        addActor(openButton);

        // region Create table layout
        table = new Table();
        float tableHeight = 0;

        // region Force sliders
        Slider sepForceSlider = new Slider(0, 1, 0.01f,false, skin);
        Slider velMatchForceSlider = new Slider(0, 1, 0.01f,false, skin);
        Slider flockCenterForceSlider = new Slider(0, 1, 0.01f,false, skin);

        sepForceSlider.setValue(Constants.SEPARATION_FORCE);
        velMatchForceSlider.setValue(Constants.VELOCITY_MATCH_FORCE);
        flockCenterForceSlider.setValue(Constants.FLOCK_CENTERING_FORCE);

        // region Add input listeners
        sepForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.SEPARATION_FORCE = slider.getValue();
                }
            }
        });
        velMatchForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.VELOCITY_MATCH_FORCE = slider.getValue();
                }
            }
        });
        flockCenterForceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Slider) {
                    Slider slider = ((Slider) actor);
                    Constants.FLOCK_CENTERING_FORCE = slider.getValue();
                }
            }
        });
        // endregion

        Label sepForceSliderLabel = new Label("Separation:", skin);
        Label velMatchForceSliderLabel = new Label("Velocity matching:", skin);
        Label flockCenterForceSliderLabel = new Label("Flock centering:", skin);

        tableHeight += Math.max(sepForceSlider.getHeight(), sepForceSliderLabel.getHeight()) * 3;

        table.add(sepForceSliderLabel, sepForceSlider);
        table.row();
        table.add(velMatchForceSliderLabel, velMatchForceSlider);
        table.row();
        table.add(flockCenterForceSliderLabel, flockCenterForceSlider);
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
