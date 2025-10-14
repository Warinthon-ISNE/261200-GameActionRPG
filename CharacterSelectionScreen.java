package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CharacterSelectionScreen implements Screen {
    final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    // Character preview textures
    private Texture goosePreview;
    private Texture giraffePreview;
    private Texture backgroundTexture;

    // Selection boxes
    private Rectangle gooseBox;
    private Rectangle giraffeBox;

    // UI dimensions
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final float BOX_WIDTH = 200;
    private static final float BOX_HEIGHT = 250;
    private static final float SPACING = 100;

    // Hovered / selected
    private int hoveredChar = -1;

    public CharacterSelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // CAMERA + VIEWPORT
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        batch = game.batch;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // Load images
        goosePreview = new Texture("goose1.png");
        giraffePreview = new Texture("giraffe.png");
        backgroundTexture = new Texture("background.jpg");

        // Setup selection boxes
        float centerX = WORLD_WIDTH / 2f;
        float centerY = WORLD_HEIGHT / 2f;

        gooseBox = new Rectangle(centerX - BOX_WIDTH - SPACING / 2, centerY - BOX_HEIGHT / 2, BOX_WIDTH, BOX_HEIGHT);
        giraffeBox = new Rectangle(centerX + SPACING / 2, centerY - BOX_HEIGHT / 2, BOX_WIDTH, BOX_HEIGHT);
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    private void handleInput() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        hoveredChar = -1;
        if (gooseBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 0;
        } else if (giraffeBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 1;
        }

        if (Gdx.input.justTouched()) {
            if (hoveredChar == 0) {
                game.setScreen(new GameScreen(game, "goose"));
                dispose();
            } else if (hoveredChar == 1) {
                game.setScreen(new GameScreen(game, "giraffe"));
                dispose();
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw background scaled to fit screen
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Title
        font.draw(batch, "SELECT YOUR CHARACTER", WORLD_WIDTH / 2f - 300, WORLD_HEIGHT - 60);

        // Draw character boxes (no text inside)
        drawCharacterBox(gooseBox, goosePreview, hoveredChar == 0);
        drawCharacterBox(giraffeBox, giraffePreview, hoveredChar == 1);

        // === Draw Names Separately ===
        font.getData().setScale(2f);

        // Goose name (adjust horizontally until centered)
        font.draw(batch, "GOOSE", gooseBox.x + 50, gooseBox.y + gooseBox.height);

        // Giraffe name (already looks centered, so leave default)
        font.draw(batch, "GIRAFFE", giraffeBox.x + 40, giraffeBox.y + giraffeBox.height);

        // === Character Stats ===
        font.getData().setScale(1f);
        font.draw(batch, "HP: 150  ATK: 8", gooseBox.x + 10, gooseBox.y - 10);
        font.draw(batch, "Ability: Rapid Fire", gooseBox.x + 10, gooseBox.y - 30);

        font.draw(batch, "HP: 100  ATK: 15", giraffeBox.x + 10, giraffeBox.y - 10);
        font.draw(batch, "Ability: Magic Burst", giraffeBox.x + 10, giraffeBox.y - 30);

        font.getData().setScale(2f);

        batch.end();
    }

    private void drawCharacterBox(Rectangle box, Texture texture, boolean hovered) {
        // Highlight effect
        if (hovered) {
            batch.setColor(1f, 1f, 0f, 1f);
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        // Draw character
        batch.draw(texture, box.x, box.y, box.width, box.height - 50);

        // Reset color
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        goosePreview.dispose();
        giraffePreview.dispose();
        backgroundTexture.dispose();
    }
}
