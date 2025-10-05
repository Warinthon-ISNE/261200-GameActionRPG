package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    final Main game;

    // World size
    private static final float WORLD_WIDTH = 8f;
    private static final float WORLD_HEIGHT = 5f;

    // Cameras and viewports
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthographicCamera hudCamera; // separate camera for HUD

    // Background
    private Texture backgroundTexture;

    // Hero animation
    private Texture heroSheet;
    private TextureRegion[][] heroFrames;
    private Animation<TextureRegion> animDown, animUp, animSide;
    private TextureRegion currentFrame;
    private float stateTime;

    // Hero setup
    private Character hero;
    private float heroWidth = 1f, heroHeight = 1f;
    private float speed = 3f;
    private String direction = "down";
    private boolean facingRight = true;

    // Enemies
    private Array<Enemy> enemies;
    private Texture enemyTexture;

    // Attack system
    private AttackManager attackManager;

    // Font for HUD
    private BitmapFont font;

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

        // HUD CAMERA
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // LOAD TEXTURES
        backgroundTexture = new Texture("noblehouse01.png");
        heroSheet = new Texture("S__28893189-removebg-preview.png");

        // Split hero sheet (4 rows, 3 columns)
        heroFrames = TextureRegion.split(heroSheet, heroSheet.getWidth() / 3, heroSheet.getHeight() / 4);
        animDown = new Animation<>(0.15f, heroFrames[0]);
        animSide = new Animation<>(0.15f, heroFrames[2]);
        animUp   = new Animation<>(0.15f, heroFrames[3]);
        currentFrame = heroFrames[0][1];
        stateTime = 0f;

        // HERO INIT
        hero = new Character(100, 10, 2, 3f, 2f);

        // ATTACK SYSTEM
        attackManager = new AttackManager();

        // ENEMIES
        enemies = new Array<>();
        enemyTexture = new Texture("enemy.png");
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new GasbySamSi(7f, 4f, "blue", 2f, hero));
        enemies.add(new GasbySamSi(2f, 4f, "red", 3f, hero));

        // FONT (white text)
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1f); // readable screen size
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        updateLogic(delta);
        updateAttack(delta);
        updateEnemies(delta);
        draw();
    }

    /** Handle hero movement and animation */
    private void handleInput(float delta) {
        float dx = 0, dy = 0;
        boolean moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; direction = "up"; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; direction = "down"; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; direction = "side"; facingRight = false; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; direction = "side"; facingRight = true; moving = true; }

        hero.move(dx, dy);
        stateTime += delta;

        // Animate
        if (moving) {
            switch (direction) {
                case "up": currentFrame = animUp.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = animDown.getKeyFrame(stateTime, true); break;
                case "side": currentFrame = animSide.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (direction) {
                case "up": currentFrame = heroFrames[3][1]; break;
                case "down": currentFrame = heroFrames[0][1]; break;
                case "side": currentFrame = heroFrames[2][1]; break;
            }
        }

        if ((facingRight && currentFrame.isFlipX()) || (!facingRight && !currentFrame.isFlipX()))
            currentFrame.flip(true, false);
    }

    /** Camera and world logic */
    private void updateLogic(float delta) {
        Vector2 pos = hero.getPosition();
        pos.x = MathUtils.clamp(pos.x, 0, WORLD_WIDTH - heroWidth);
        pos.y = MathUtils.clamp(pos.y, 0, WORLD_HEIGHT - heroHeight);

        camera.position.lerp(new com.badlogic.gdx.math.Vector3(pos.x + heroWidth / 2f, pos.y + heroHeight / 2f, 0), 0.1f);
        camera.update();
    }

    /** Update attacks & bullets */
    private void updateAttack(float delta) {
        Vector2 heroCenter = new Vector2(hero.getPosition().x + heroWidth / 2f, hero.getPosition().y + heroHeight / 2f);
        Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        attackManager.update(delta, heroCenter, mouseWorld);

        // Check bullet collisions
        for (Bullet bullet : attackManager.getBullets()) {
            for (Enemy e : enemies) {
                if (!e.isDead() && bullet.getBounds().overlaps(e.getBounds())) {
                    e.gotDamage(hero.getAttack());
                    bullet.setActive(false);
                    if (e.isDead()) hero.addKill();
                }
            }
        }
    }

    /** Update enemy movement and cleanup */
    private void updateEnemies(float delta) {
        for (Enemy e : enemies) e.update(delta);
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) enemies.removeIndex(i);
        }
    }

    /** Draw everything (world + HUD) */
    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        SpriteBatch batch = game.batch;

        // --- World rendering ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(currentFrame, hero.getPosition().x, hero.getPosition().y, heroWidth, heroHeight);
        for (Enemy e : enemies)
            batch.draw(enemyTexture, e.getPosition().x, e.getPosition().y, 1f, 1f);
        attackManager.render(batch);
        batch.end();

        // --- HUD rendering (fixed to screen) ---
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + hero.getHp() + "/" + hero.getMaxHp(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Kills: " + hero.getKills(), 20, Gdx.graphics.getHeight() - 50);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudCamera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        heroSheet.dispose();
        enemyTexture.dispose();
        attackManager.dispose();
        font.dispose();
    }
}
