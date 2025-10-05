package ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AttackManager {
    private final Array<ISNE12.project.Bullet> bullets;
    private final Texture bulletTexture;

    private float timeSinceLastShot = 0f;

    public AttackManager() {
        bullets = new Array<>();
        bulletTexture = new Texture("bullet.png"); // your bullet image here
    }

    public void update(float delta, Vector2 heroCenter, Vector2 mouseWorldPos) {
        timeSinceLastShot += delta;

        // seconds
        float shootCooldown = 0.3f;
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= shootCooldown) {
            shoot(heroCenter, mouseWorldPos);
            timeSinceLastShot = 0f;
        }

        // update all bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta);
            if (b.isOutOfBounds(20f, 10f)) { // use your world size
                bullets.removeIndex(i);
            }
        }
    }

    private void shoot(Vector2 heroCenter, Vector2 mouseWorldPos) {
        Vector2 direction = mouseWorldPos.cpy().sub(heroCenter);
        float bulletDamage = 10f;
        bullets.add(new ISNE12.project.Bullet(bulletTexture, heroCenter.x, heroCenter.y, direction, bulletDamage));
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }

    public void dispose() {
        bulletTexture.dispose();
    }
}
