package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    final Main game;

    private Character hero;
    private Sprite heroSprite;

    private Viewport viewport;

    private static final float WORLD_WIDTH = 8f;
    private static final float WORLD_HEIGHT = 5f;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        // Character
        hero = new Character(100, 20, 5, 2, 1); // hp, atk, def, startX, startY

        Texture heroTexture = new Texture("S__16261125-removebg-preview.png");
        heroSprite = new Sprite(heroTexture);

        // Hero size in world units
        heroSprite.setSize(1.5f, 1.5f);
        heroSprite.setPosition(hero.getPosition().x, hero.getPosition().y);
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        draw();
    }

    private void input(float delta) {
        float speed = 4f;

        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= speed * delta;

        hero.move(dx, dy);
        heroSprite.setPosition(hero.getPosition().x, hero.getPosition().y);
    }

    private void logic(float delta) {
        // Clamp hero inside world bounds
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float heroWidth = heroSprite.getWidth();
        float heroHeight = heroSprite.getHeight();

        float clampedX = MathUtils.clamp(heroSprite.getX(), 0, worldWidth - heroWidth);
        float clampedY = MathUtils.clamp(heroSprite.getY(), 0, worldHeight - heroHeight);

        hero.getPosition().set(clampedX, clampedY);
        heroSprite.setPosition(clampedX, clampedY);
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();
        heroSprite.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        heroSprite.getTexture().dispose();
    }
}
