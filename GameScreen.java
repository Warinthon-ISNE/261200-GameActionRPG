package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private OrthographicCamera camera;//camera to follow the player

    private Texture backgroundTexture; // for background

    // world size for background
    private static final float WORLD_WIDTH = 20f;
    private static final float WORLD_HEIGHT = 10f;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        //create camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(8f, 5f, camera); // viewport size　"camera" uses this view
        viewport.apply();

        // load background
        backgroundTexture = new Texture("noblehouse01.png");

        hero = new Character(100, 20, 5, 2, 1);

        Texture heroTexture = new Texture("S__16261125-removebg-preview.png");
        heroSprite = new Sprite(heroTexture);

        heroSprite.setSize(1.5f, 1.5f);
        heroSprite.setPosition(hero.getPosition().x, hero.getPosition().y);

        camera.position.set(hero.getPosition().x, hero.getPosition().y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        updateCamera(delta);
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
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float heroWidth = heroSprite.getWidth();
        float heroHeight = heroSprite.getHeight();

        float clampedX = MathUtils.clamp(heroSprite.getX(), 0, WORLD_WIDTH - heroWidth);
        float clampedY = MathUtils.clamp(heroSprite.getY(), 0, WORLD_HEIGHT - heroHeight);

        hero.getPosition().set(clampedX, clampedY);
        heroSprite.setPosition(clampedX, clampedY);
    }

    private void updateCamera(float delta) {

        // Get player's center position
        float playerCenterX = heroSprite.getX() + heroSprite.getWidth() / 2f;
        float playerCenterY = heroSprite.getY() + heroSprite.getHeight() / 2f;

        // Smoothly move camera towards player's center
        camera.position.x += (playerCenterX - camera.position.x);
        camera.position.y += (playerCenterY - camera.position.y);

        // Margin settings for world edges
        float horizontalMargin = 2f; // Horizontal margin
        float verticalMargin = 2f;   // Vertical margin

        // Half size of camera viewport
        float cameraHalfWidth = camera.viewportWidth / 2f;
        float cameraHalfHeight = camera.viewportHeight / 2f;

        // prevent camera moving outside world
        // The minimum and maximum positions the camera can move to
        // Margins allow a small extra view beyond world edges
        float minCameraX = cameraHalfWidth - horizontalMargin;
        float maxCameraX = WORLD_WIDTH - cameraHalfWidth + horizontalMargin;
        float minCameraY = cameraHalfHeight - verticalMargin;
        float maxCameraY = WORLD_HEIGHT - cameraHalfHeight + verticalMargin;

        // Clamp camera position within bounds
        camera.position.x = MathUtils.clamp(camera.position.x, minCameraX, maxCameraX);
        camera.position.y = MathUtils.clamp(camera.position.y, minCameraY, maxCameraY);

        // Update camera
        camera.update();
    }



    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // draw background
        game.batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        heroSprite.draw(game.batch);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        heroSprite.getTexture().dispose();
        backgroundTexture.dispose();
    }
}
