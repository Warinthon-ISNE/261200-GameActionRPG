package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AttackManager {
    private final Array<Bullet> bullets;
    private final Texture bulletTexture;

    private float shootCooldown = 0.25f; // seconds between shots
    private float timeSinceLastShot = 0f;

    public AttackManager() {
        bullets = new Array<>();
        bulletTexture = new Texture("bullet.png"); // bullet image in assets
    }

    public void update(float delta, Vector2 heroCenter, Vector2 mouseWorldPos) {
        timeSinceLastShot += delta;

        // Left click to shoot
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= shootCooldown) {
            shoot(heroCenter, mouseWorldPos);
            timeSinceLastShot = 0f;
        }

        // Update bullets and remove if out of bounds
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta);
            if (b.isOutOfBounds(8f, 5f)) { // same as world size
                bullets.removeIndex(i);
            }
        }
    }

    private void shoot(Vector2 heroCenter, Vector2 mouseWorldPos) {
        bullets.add(new Bullet(bulletTexture, heroCenter, mouseWorldPos));
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
