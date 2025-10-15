package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {

    private final Main game;
    private final int totalKills;
    private final int waveReached;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    public GameOverScreen(Main game, int totalKills, int waveReached) {
        this.game = game;
        this.totalKills = totalKills;
        this.waveReached = waveReached;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(640, 360, 0);
        camera.update();

        batch = game.batch;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2.5f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, "GAME OVER", 500, 450);
        font.getData().setScale(1.5f);
        font.draw(batch, "Wave Reached: " + waveReached, 530, 360);
        font.draw(batch, "Total Kills: " + totalKills, 530, 330);
        font.draw(batch, "Press ENTER to Retry", 520, 250);
        font.draw(batch, "Press ESC to Exit", 550, 220);

        batch.end();

        // Key input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new CharacterSelectionScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { font.dispose(); }
}
