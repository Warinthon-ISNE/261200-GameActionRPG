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
    private Character hero;

    // --- New fields for fire rate control ---
    private float fireTimer = 0f;
    private float fireRate; // seconds between shots (lower = faster)

    public AttackManager(Character hero) {
        this.hero = hero;
        this.bulletTexture = new Texture(hero.getBulletTexturePath()); // load character-specific bullet
        this.bullets = new Array<>();

        // Set default fire rate based on character
        if (hero instanceof Goose) {
            fireRate = 0.2f; // Goose shoots faster
        } else if (hero instanceof Giraffe) {
            fireRate = 0.5f; // Giraffe shoots slower
        } else {
            fireRate = 0.3f; // Default
        }
    }

    public void update(float delta, Vector2 heroPos, Vector2 targetPos) {
        hero.updateCooldown(delta);
        fireTimer += delta;

        boolean attackInput = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        // Only shoot if both cooldown and fire rate are ready
        if (attackInput && hero.canAttack() && fireTimer >= fireRate) {
            hero.resetAttackCooldown();
            fireTimer = 0f; // reset timer for next allowed shot

            bullets.add(new Bullet(bulletTexture, heroPos, targetPos));

            // Goose passive — multishot
            if (hero instanceof Goose) {
                Goose goose = (Goose) hero;
                int extra = goose.getExtraShots();

                if (extra > 0) {
                    float spreadAngle = 15f + (extra * 5f);
                    addSpreadBullets(heroPos, targetPos, extra, spreadAngle);
                }
            }
        }

        // Update bullets
        for (Bullet b : bullets) b.update(delta);

        // Remove inactive bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            if (!bullets.get(i).isActive()) bullets.removeIndex(i);
        }
    }

    /** Adds spread bullets for Goose multishot passive */
    private void addSpreadBullets(Vector2 start, Vector2 target, int extra, float spreadAngle) {
        Vector2 dir = new Vector2(target).sub(start).nor();
        int sideCount = extra;
        float angleStep = spreadAngle / sideCount;

        for (int i = 1; i <= sideCount; i++) {
            float angle = angleStep * i;
            Vector2 leftDir = rotateVector(dir, angle);
            Vector2 rightDir = rotateVector(dir, -angle);

            bullets.add(new Bullet(bulletTexture, start, start.cpy().add(leftDir.scl(2f))));
            bullets.add(new Bullet(bulletTexture, start, start.cpy().add(rightDir.scl(2f))));
        }
    }

    /** Helper: rotate a vector by N degrees */
    private Vector2 rotateVector(Vector2 v, float degrees) {
        float radians = (float) Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        return new Vector2(v.x * cos - v.y * sin, v.x * sin + v.y * cos).nor();
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : bullets) b.render(batch);
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public void dispose() {
        bulletTexture.dispose();
    }
}
