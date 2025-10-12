package ISNE12.project;

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
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    final Main game;

    // World size
    private static final float WORLD_WIDTH = 8f;
    private static final float WORLD_HEIGHT = 5f;

    // Cameras
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthographicCamera hudCamera;

    // Background
    private Texture backgroundTexture;

    // Hero
    private Character hero;
    private String selectedCharacter; // Store which character was selected
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

    public GameScreen(Main game, String characterType) {
        this.game = game;
        this.selectedCharacter = characterType;  // ✅ SAVE THE CHARACTER TYPE
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

        // HERO INIT - based on selected character
        if (selectedCharacter.equals("goose")) {
            hero = new Goose(3f, 2f);
            speed = 3f;
        } else if (selectedCharacter.equals("giraffe")) {
            hero = new Giraffe(3f, 2f);
            speed = 2.5f; // Giraffe is slightly slower
        } else {
            // Default to Goose
            hero = new Goose(3f, 2f);
            selectedCharacter = "goose";
        }

        // ATTACK SYSTEM - passes character type
        attackManager = new AttackManager(selectedCharacter);

        // ENEMIES
        enemies = new Array<>();
        enemyTexture = new Texture("enemy.png");
        enemies.add(new Zombie(1f, 1f, hero));
        enemies.add(new GasbySamSi(7f, 4f, "blue", 2f, hero));
        enemies.add(new GasbySamSi(2f, 4f, "red", 3f, hero));

        // FONT (white text)
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
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
        hero.updateAnimation(delta, moving, direction, facingRight);
    }

    /** Camera and world logic */
    private void updateLogic(float delta) {
        hero.applyPassive();

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
        attackManager.update(delta, heroCenter, mouseWorld, hero); // Pass hero reference

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
        batch.draw(hero.getCurrentFrame(), hero.getPosition().x, hero.getPosition().y, heroWidth, heroHeight);
        for (Enemy e : enemies)
            batch.draw(enemyTexture, e.getPosition().x, e.getPosition().y, 1f, 1f);
        attackManager.render(batch);
        batch.end();

        // --- HUD rendering ---
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.draw(batch, "Character: " + selectedCharacter.toUpperCase(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "HP: " + hero.getHp() + "/" + hero.getMaxHp(), 20, Gdx.graphics.getHeight() - 45);
        font.draw(batch, "ATK: " + hero.getAttack() + " | DEF: " + hero.getDefense(), 20, Gdx.graphics.getHeight() - 70);
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
    }
}
