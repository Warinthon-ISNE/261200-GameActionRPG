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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;

public class GameScreen implements Screen {
    final Main game;

    // === WORLD CONFIG ===
    private static final float WORLD_WIDTH = 20f;
    private static final float WORLD_HEIGHT = 14f;

    // === CAMERA & VIEWPORT ===
    private OrthographicCamera camera;
    private Viewport viewport;
    private OrthographicCamera hudCamera;

    // === BACKGROUND ===
    private Texture backgroundTexture;

    // === HERO ===
    private Character hero;
    private String selectedCharacter; // store which character was chosen
    private float heroWidth = 1f, heroHeight = 1f;
    private float speed = 3f;
    private String direction = "down";
    private boolean facingRight = true;

    // === ENEMIES ===
    private Array<Enemy> enemies;
    private Texture enemyTexture;

    // === ATTACK SYSTEM ===
    private AttackManager attackManager;

    // === HUD ===
    private BitmapFont font;

    // ===Music===
    private Music bgm;

    /** Constructor receives selected character type */
    public GameScreen(Main game, String characterType) {
        this.game = game;
        this.selectedCharacter = characterType;
    }

    @Override
    public void show() {
        // CAMERA SETUP
        camera = new OrthographicCamera();
        viewport = new StretchViewport(10f, 7f, camera); // fills screen
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        // HUD CAMERA
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // LOAD BACKGROUND
        backgroundTexture = new Texture("noblehouse01.png");

        // HERO INIT (based on selection)
        if (selectedCharacter.equals("goose")) {
            hero = new Goose(3f, 2f);
            speed = 3f;
        } else if (selectedCharacter.equals("giraffe")) {
            hero = new Giraffe(3f, 2f);
            speed = 2.5f; // giraffe moves slower
        } else {
            hero = new Goose(3f, 2f);
            selectedCharacter = "goose";
        }

        // ✅ Pass the hero object (not the string)
        attackManager = new AttackManager(hero);


        // BGM
        bgm = Gdx.audio.newMusic(Gdx.files.internal("RPG_Battle_03.mp3"));
        bgm.setLooping(true);  // loop
        bgm.setVolume(0.5f);   // volume
        bgm.play();

        // ENEMIES
        enemies = new Array<>();
        enemyTexture = new Texture("enemy.png");
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new GasbySamSi(7f, 4f, "blue", 2f, hero));
        enemies.add(new GasbySamSi(2f, 4f, "red", 3f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new Zombie(1f, 1f, hero));

        // FONT
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    @Override
    public void render(float delta) {
        // Optional: allow returning to character select with ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new CharacterSelectionScreen(game));
            dispose();
            return;
        }

        handleInput(delta);
        updateLogic(delta);
        updateAttack(delta);
        updateEnemies(delta);
        draw();
    }

    /** Handle hero movement and pass direction info to hero */
    private void handleInput(float delta) {
        float dx = 0, dy = 0;
        boolean moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { dy += speed * delta; direction = "up"; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { dy -= speed * delta; direction = "down"; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { dx -= speed * delta; direction = "side"; facingRight = false; moving = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { dx += speed * delta; direction = "side"; facingRight = true; moving = true; }

        hero.move(dx, dy);
        hero.updateAnimation(delta, moving, direction, facingRight);
    }

    /** World and camera logic */
    private void updateLogic(float delta) {
        hero.applyPassive(); // hero’s passive skill logic

        Vector2 pos = hero.getPosition();
        pos.x = MathUtils.clamp(pos.x, 0, WORLD_WIDTH - heroWidth);
        pos.y = MathUtils.clamp(pos.y, 0, WORLD_HEIGHT - heroHeight);

        // Smooth camera follow
        camera.position.lerp(
            new com.badlogic.gdx.math.Vector3(pos.x + heroWidth / 2f, pos.y + heroHeight / 2f, 0),
            0.1f
        );

        // Clamp camera inside world bounds
        float halfW = camera.viewportWidth * 0.5f;
        float halfH = camera.viewportHeight * 0.5f;
        camera.position.x = MathUtils.clamp(camera.position.x, halfW, WORLD_WIDTH - halfW);
        camera.position.y = MathUtils.clamp(camera.position.y, halfH, WORLD_HEIGHT - halfH);

        camera.update();
    }

    /** Attack & bullet logic */
    private void updateAttack(float delta) {
        Vector2 heroCenter = new Vector2(hero.getPosition().x + heroWidth / 2f, hero.getPosition().y + heroHeight / 2f);
        Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        attackManager.update(delta, heroCenter, mouseWorld);

        // Bullet collisions
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

    /** Enemy update & cleanup */
    private void updateEnemies(float delta) {
        for (Enemy e : enemies) e.update(delta);
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) enemies.removeIndex(i);
        }
    }

    /** Draw world + HUD */
    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        SpriteBatch batch = game.batch;

        // === WORLD RENDER ===
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(hero.getCurrentFrame(), hero.getPosition().x, hero.getPosition().y, heroWidth, heroHeight);
        for (Enemy e : enemies)
            batch.draw(enemyTexture, e.getPosition().x, e.getPosition().y, 1f, 1f);
        attackManager.render(batch);
        batch.end();

        // === HUD RENDER ===
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.draw(batch, "Character: " + selectedCharacter.toUpperCase(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "HP: " + hero.getHp() + "/" + hero.getMaxHp(), 20, Gdx.graphics.getHeight() - 45);
        font.draw(batch, "ATK: " + hero.getAttack(), 20, Gdx.graphics.getHeight() - 70);
        font.draw(batch, "Kills: " + hero.getKills(), 20, Gdx.graphics.getHeight() - 95);
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
        enemyTexture.dispose();
        attackManager.dispose();
        font.dispose();
        if (bgm != null) bgm.dispose();
    }
}
