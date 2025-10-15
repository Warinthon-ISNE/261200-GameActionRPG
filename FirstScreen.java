package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * FirstScreen — splash screen that displays a logo before moving to character selection.
 */
public class FirstScreen implements Screen {

    private final Main game;
    private Texture logo;
    private SpriteBatch batch;
    private float timer = 0f;
    private final float displayTime = 1.5f; // seconds before switching screen

    public FirstScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        logo = new Texture("logo.jpg"); // Put your logo image in assets
        batch = game.batch;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float logoWidth = logo.getWidth() * 0.7f;
        float logoHeight = logo.getHeight() * 0.7f;
        batch.draw(logo, (width - logoWidth) / 2f, (height - logoHeight) / 2f, logoWidth, logoHeight);
        batch.end();

        // Automatically switch to CharacterSelectionScreen after a few seconds
        if (timer >= displayTime) {
            game.setScreen(new CharacterSelectionScreen(game));
            dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (logo != null) logo.dispose();
    }
}
