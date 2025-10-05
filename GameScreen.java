package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    final Main game;

    // World size
    private static final float WORLD_WIDTH = 8f;
    private static final float WORLD_HEIGHT = 5f;

    // Camera and viewport
    private OrthographicCamera camera;
    private FitViewport viewport;

    // Background texture
    private Texture backgroundTexture;

    // Hero sprite sheet and animation frames
    private Texture heroSheet;
    private TextureRegion[][] heroFrames;

    // Animations for each direction
    private Animation<TextureRegion> animDown;
    private Animation<TextureRegion> animUp;
    private Animation<TextureRegion> animSide;

    // visible frame for rendering
    private TextureRegion currentFrame;

    // Timing for animation
    private float stateTime;

    // Hero position and size
    private float heroX, heroY;
    private float heroWidth = 1f, heroHeight = 1f;

    // Movement speed
    private float speed = 3f;

    // Direction and facing side
    private String direction = "down";
    private boolean facingRight = true;

    // Attack
    private AttackManager attackManager;


    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // CAMERA & VIEWPORT SETUP
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        // LOAD BACKGROUND
        // Put this image inside your assets/ folder
        backgroundTexture = new Texture("noblehouse01.png");

        // LOAD HERO SHEET
        heroSheet = new Texture("S__28893189-removebg-preview.png");

        // Split into 4 directions × 3 animation frames
        heroFrames = TextureRegion.split(
            heroSheet,
            heroSheet.getWidth() / 3,   // columns
            heroSheet.getHeight() / 4   // rows
        );

        // Create walking animations for each direction
        animDown = new Animation<>(0.15f, heroFrames[0]);
        animSide = new Animation<>(0.15f, heroFrames[2]);
        animUp   = new Animation<>(0.15f, heroFrames[3]);

        // Set initial frame and position
        currentFrame = heroFrames[0][1];
        heroX = 3f;
        heroY = 2f;
        stateTime = 0f;

        // ATTACK SYSTEM
        attackManager = new AttackManager();
    }

    @Override
    public void render(float delta) {
        handleInput(delta);  // Process player input
        updateLogic(delta);  // Update hero and camera logic
        updateAttack(delta); // Update attack
        draw();              // Render everything
    }

    /** Handle player input (WASD movement and animation switching) */
    private void handleInput(float delta) {
        float dx = 0, dy = 0;
        boolean moving = false;

        // MOVEMENT INPUT
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += speed * delta;
            direction = "up";
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= speed * delta;
            direction = "down";
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= speed * delta;
            direction = "side";
            facingRight = false;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += speed * delta;
            direction = "side";
            facingRight = true;
            moving = true;
        }

        // Update hero position
        heroX += dx;
        heroY += dy;

        // Increase animation time
        stateTime += delta;

        // ANIMATION SELECTION
        if (moving) {
            switch (direction) {
                case "up":
                    currentFrame = animUp.getKeyFrame(stateTime, true);
                    break;
                case "down":
                    currentFrame = animDown.getKeyFrame(stateTime, true);
                    break;
                case "side":
                    currentFrame = animSide.getKeyFrame(stateTime, true);
                    break;
            }
        } else {
            // Idle
            switch (direction) {
                case "up": currentFrame = heroFrames[3][1]; break;
                case "down": currentFrame = heroFrames[0][1]; break;
                case "side": currentFrame = heroFrames[2][1]; break;
            }
        }

        // FLIP SPRITE WHEN FACING LEFT
        if ((facingRight && currentFrame.isFlipX()) || (!facingRight && !currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }
    }

    /** Keep hero inside world and move camera smoothly */
    private void updateLogic(float delta) {
        // Prevent hero from moving off-screen
        heroX = MathUtils.clamp(heroX, 0, WORLD_WIDTH - heroWidth);
        heroY = MathUtils.clamp(heroY, 0, WORLD_HEIGHT - heroHeight);

        // Make camera smoothly follow hero
        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(
                heroX + heroWidth / 2f,
                heroY + heroHeight / 2f,
                0),
            0.1f
        );
        camera.update();
    }
    /** Handle shooting and bullet updates */
    private void updateAttack(float delta) {
        // hero center in world coordinates
        float heroCenterX = heroX + heroWidth / 2f;
        float heroCenterY = heroY + heroHeight / 2f;
        Vector2 heroCenter = new Vector2(heroCenterX, heroCenterY);

        // convert mouse screen → world coordinates
        Vector2 mouseScreen = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 mouseWorld = viewport.unproject(new Vector2(mouseScreen));

        attackManager.update(delta, heroCenter, mouseWorld);
    }

    /** Draw background and hero */
    private void draw() {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply viewport and camera transform
        viewport.apply();
        SpriteBatch batch = game.batch;
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw the background (fills entire world area)
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Draw the current hero animation frame
        batch.draw(currentFrame, heroX, heroY, heroWidth, heroHeight);

        // Draw bullets
        attackManager.render(batch);

        batch.end();
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
        backgroundTexture.dispose();
        heroSheet.dispose();
        attackManager.dispose();
    }
}
