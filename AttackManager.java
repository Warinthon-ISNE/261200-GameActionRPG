package ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AttackManager {
    private Texture bulletTexture;
    private Texture magicBulletTexture;
    private Array<Bullet> bullets;
    private float fireRate;
    private float fireTimer = 0f;
    private String attackType; // "normal" or "magic"

    public AttackManager(String character) {
        bulletTexture = new Texture("bullet.png");
        magicBulletTexture = new Texture("magic_bullet.png");
        bullets = new Array<>();

        if (character.equals("goose")) {
            attackType = "normal";
            fireRate = 0.2f;
        } else if (character.equals("giraffe")) {
            attackType = "magic";
            fireRate = 0.5f;
        } else {
            attackType = "normal";
            fireRate = 0.3f;
        }
    }

    public void update(float delta, Vector2 heroPos, Vector2 targetPos, Character hero) {
        fireTimer += delta;

        // Shoot with left click or spacebar
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isKeyPressed(Input.Keys.SPACE))
            && fireTimer >= fireRate) {
            fireTimer = 0f;

            if (attackType.equals("normal")) {
                bullets.add(new Bullet(bulletTexture, heroPos, targetPos));
            } else if (attackType.equals("magic")) {
                // Magic attack: single straight bullet
                bullets.add(new Bullet(magicBulletTexture, heroPos, targetPos));
            }
        }

        // Update all bullets
        for (Bullet b : bullets) {
            b.update(delta);
        }

        // Remove inactive bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).isActive()) {
                bullets.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void dispose() {
        bulletTexture.dispose();
        magicBulletTexture.dispose();
    }
}
