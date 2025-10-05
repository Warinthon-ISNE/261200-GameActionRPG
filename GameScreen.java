package ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    final Main game;

    private Character hero;
    private Sprite heroSprite;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Texture backgroundTexture;

    private AttackManager attackManager;

    private static final float WORLD_WIDTH = 20f;
    private static final float WORLD_HEIGHT = 10f;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(8f, 5f, camera);
        viewport.apply();

        backgroundTexture = new Texture("noblehouse01.png");
        hero = new Character(100, 20, 5, 2, 1);

        Texture heroTexture = new Texture("S__16261125-removebg-preview.png");
        heroSprite = new Sprite(heroTexture);
        heroSprite.setSize(1.5f, 1.5f);
        heroSprite.setPosition(hero.getPosition().x, hero.getPosition().y);

        attackManager = new AttackManager();

        camera.position.set(hero.getPosition().x, hero.getPosition().y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        updateCamera(delta);
        updateAttack(delta);
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
        float heroWidth = heroSprite.getWidth();
        float heroHeight = heroSprite.getHeight();

        float clampedX = MathUtils.clamp(heroSprite.getX(), 0, WORLD_WIDTH - heroWidth);
        float clampedY = MathUtils.clamp(heroSprite.getY(), 0, WORLD_HEIGHT - heroHeight);

        hero.getPosition().set(clampedX, clampedY);
        heroSprite.setPosition(clampedX, clampedY);
    }

    private void updateCamera(float delta) {
        float playerCenterX = heroSprite.getX() + heroSprite.getWidth() / 2f;
        float playerCenterY = heroSprite.getY() + heroSprite.getHeight() / 2f;

        camera.position.x += (playerCenterX - camera.position.x);
        camera.position.y += (playerCenterY - camera.position.y);

        float horizontalMargin = 2f;
        float verticalMargin = 2f;

        float cameraHalfWidth = camera.viewportWidth / 2f;
        float cameraHalfHeight = camera.viewportHeight / 2f;

        float minCameraX = cameraHalfWidth - horizontalMargin;
        float maxCameraX = WORLD_WIDTH - cameraHalfWidth + horizontalMargin;
        float minCameraY = cameraHalfHeight - verticalMargin;
        float maxCameraY = WORLD_HEIGHT - cameraHalfHeight + verticalMargin;

        camera.position.x = MathUtils.clamp(camera.position.x, minCameraX, maxCameraX);
        camera.position.y = MathUtils.clamp(camera.position.y, minCameraY, maxCameraY);

        camera.update();
    }

    private void updateAttack(float delta) {
        Vector2 heroCenter = new Vector2(
            heroSprite.getX() + heroSprite.getWidth() / 2f,
            heroSprite.getY() + heroSprite.getHeight() / 2f
        );

        // convert mouse screen position to world position
        Vector2 mouseScreen = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 mouseWorld = viewport.unproject(new Vector2(mouseScreen));

        attackManager.update(delta, heroCenter, mouseWorld);
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        heroSprite.draw(game.batch);
        attackManager.render(game.batch);
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
        attackManager.dispose();
    }
}
