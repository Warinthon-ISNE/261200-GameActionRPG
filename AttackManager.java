package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AttackManager {
    private Texture bulletTexture;
    private Array<Bullet> bullets;
    private float fireRate = 0.3f;  // time between shots
    private float fireTimer = 0f;

    public AttackManager() {
        bulletTexture = new Texture("bullet.png");
        bullets = new Array<>();
    }

    public void update(float delta, Vector2 heroPos, Vector2 targetPos) {
        fireTimer += delta;

        // Shoot with left click or spacebar
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isKeyPressed(Input.Keys.SPACE))
            && fireTimer >= fireRate) {
            fireTimer = 0f;
            bullets.add(new Bullet(bulletTexture, heroPos, targetPos));
        }

        // Update all bullets
        for (Bullet b : bullets) {
            b.update(delta);
        }

        // Remove inactive bullets safely
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
    }
}
