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

/**
 * CharacterSelectionScreen — lets player choose between Goose and Giraffe.
 * Hover over a character to highlight, click to start the game.
 */
public class CharacterSelectionScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;

    // === Assets ===
    private Texture goosePreview;
    private Texture giraffePreview;
    private Texture backgroundTexture;

    // === Selection boxes ===
    private Rectangle gooseBox;
    private Rectangle giraffeBox;

    // === UI layout constants ===
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final float BOX_WIDTH = 220f;
    private static final float BOX_HEIGHT = 270f;
    private static final float SPACING = 120f;

    // === State ===
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

        // FONT SETUP
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // LOAD TEXTURES
        goosePreview = new Texture("goose1.png");
        giraffePreview = new Texture("giraffe.png");
        backgroundTexture = new Texture("background.jpg");

        // CHARACTER BOXES (centered horizontally)
        float centerX = WORLD_WIDTH / 2f;
        float centerY = WORLD_HEIGHT / 2f - 40f;

        gooseBox = new Rectangle(centerX - BOX_WIDTH - SPACING / 2f, centerY - BOX_HEIGHT / 2f, BOX_WIDTH, BOX_HEIGHT);
        giraffeBox = new Rectangle(centerX + SPACING / 2f, centerY - BOX_HEIGHT / 2f, BOX_WIDTH, BOX_HEIGHT);
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    /** Handle mouse hover and click selection */
    private void handleInput() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        hoveredChar = -1;
        if (gooseBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 0;
        } else if (giraffeBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 1;
        }

        // Mouse click or Enter key confirms selection
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (hoveredChar == 0) {
                game.setScreen(new GameScreen(game, "goose"));
                dispose();
            } else if (hoveredChar == 1) {
                game.setScreen(new GameScreen(game, "giraffe"));
                dispose();
            }
        }
    }

    /** Draw UI and background */
    private void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // === Background ===
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // === Title ===
        font.getData().setScale(2.5f);
        font.draw(batch, "SELECT YOUR CHARACTER", WORLD_WIDTH / 2f - 300, WORLD_HEIGHT - 80);

        // === Draw character boxes ===
        drawCharacterBox(gooseBox, goosePreview, hoveredChar == 0);
        drawCharacterBox(giraffeBox, giraffePreview, hoveredChar == 1);

        // === Character names (centered above boxes) ===
        font.getData().setScale(2f);
        font.draw(batch, "GOOSE",
            gooseBox.x + gooseBox.width / 2f - 60f,
            gooseBox.y + gooseBox.height + 40f);

        font.draw(batch, "GIRAFFE",
            giraffeBox.x + giraffeBox.width / 2f - 70f,
            giraffeBox.y + giraffeBox.height + 40f);

        // === Character stats / abilities ===
        font.getData().setScale(1.2f);
        font.draw(batch, "HP: 200  ATK: 20  ATKSPD: 1.5", gooseBox.x + 10, gooseBox.y - 10);
        font.draw(batch, "Passive: Multishot (+1 bullet per 5 kills)", gooseBox.x + 10, gooseBox.y - 30);

        font.draw(batch, "HP: 100  ATK: 10  ATKSPD: 3", giraffeBox.x + 10, giraffeBox.y - 10);
        font.draw(batch, "Passive: Rapid Fire (+0.3 atk speed per 5 kills)", giraffeBox.x + 10, giraffeBox.y - 30);

        batch.end();
    }

    /** Draws one selectable character preview box */
    private void drawCharacterBox(Rectangle box, Texture texture, boolean hovered) {
        if (hovered) {
            // Hover highlight
            batch.setColor(1f, 1f, 0.5f, 1f);
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.draw(texture, box.x, box.y, box.width, box.height - 50);
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
