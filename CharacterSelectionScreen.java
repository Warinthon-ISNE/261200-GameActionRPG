package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class CharacterSelectionScreen implements Screen {
    final Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    // Character preview textures
    private Texture goosePreview;
    private Texture magePreview;  // Add your second character texture
    private Texture backgroundTexture;

    // Selection boxes
    private Rectangle gooseBox;
    private Rectangle mageBox;

    // UI dimensions
    private static final float BOX_WIDTH = 200;
    private static final float BOX_HEIGHT = 250;
    private static final float SPACING = 50;

    // Selected character (-1 = none, 0 = goose, 1 = mage, etc.)
    private int hoveredChar = -1;

    public CharacterSelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = game.batch;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // Load character preview images
        goosePreview = new Texture("goose1.png");
        magePreview = new Texture("giraffe.png"); // Replace with your mage texture
        backgroundTexture = new Texture("background.jpg");

        // Setup selection boxes (centered on screen)
        float centerX = 400;
        float centerY = 240;

        gooseBox = new Rectangle(
            centerX - BOX_WIDTH - SPACING/2,
            centerY - BOX_HEIGHT/2,
            BOX_WIDTH,
            BOX_HEIGHT
        );

        mageBox = new Rectangle(
            centerX + SPACING/2,
            centerY - BOX_HEIGHT/2,
            BOX_WIDTH,
            BOX_HEIGHT
        );
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    private void handleInput() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        // Check hover
        hoveredChar = -1;
        if (gooseBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 0;
        } else if (mageBox.contains(touchPos.x, touchPos.y)) {
            hoveredChar = 1;
        }

        // Check click
        if (Gdx.input.justTouched()) {
            if (hoveredChar == 0) {
                // Selected Goose
                game.setScreen(new GameScreen(game, "goose"));
                dispose();
            } else if (hoveredChar == 1) {
                // Selected Mage
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

        // Background
        batch.draw(backgroundTexture, 0, 0, 800, 480);

        // Title
        font.draw(batch, "SELECT YOUR CHARACTER", 200, 450);

        // Draw Goose box
        drawCharacterBox(gooseBox, goosePreview, "GOOSE", hoveredChar == 0);
        font.getData().setScale(1f);
        font.draw(batch, "HP: 150  ATK: 8  DEF: 5", gooseBox.x + 10, gooseBox.y - 10);
        font.draw(batch, "Ability: Rapid Fire", gooseBox.x + 10, gooseBox.y - 30);
        font.getData().setScale(2f);

        // Draw Mage box
        drawCharacterBox(mageBox, magePreview, "GIRAFFE", hoveredChar == 1);
        font.getData().setScale(1f);
        font.draw(batch, "HP: 100  ATK: 15  DEF: 3", mageBox.x + 10, mageBox.y - 10);
        font.draw(batch, "Ability: Magic Burst", mageBox.x + 10, mageBox.y - 30);
        font.getData().setScale(2f);

        batch.end();
    }

    private void drawCharacterBox(Rectangle box, Texture texture, String name, boolean hovered) {
        // Draw border (highlight if hovered)
        if (hovered) {
            batch.setColor(1f, 1f, 0f, 1f); // Yellow highlight
        } else {
            batch.setColor(0.3f, 0.3f, 0.3f, 1f); // Gray border
        }

        // Simple border effect
        batch.draw(texture, box.x - 5, box.y - 5, box.width + 10, box.height + 10);

        // Draw character preview
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(texture, box.x, box.y, box.width, box.height - 50);

        // Draw character name
        font.draw(batch, name, box.x + 50, box.y + box.height - 10);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        goosePreview.dispose();
        magePreview.dispose();
        backgroundTexture.dispose();
    }
}
