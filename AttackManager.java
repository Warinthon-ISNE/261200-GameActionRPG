package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * AttackManager — handles all bullet creation, timing, and rendering.
 * Works with both Goose (multishot) and Giraffe (attack speed).
 */
public class AttackManager {
    private final Texture bulletTexture;
    private final Array<Bullet> bullets = new Array<>();
    private final Character hero;

    // Timer for attack rate
    private float shootTimer = 0f;

    public AttackManager(Character hero) {
        this.hero = hero;
        this.bulletTexture = new Texture(hero.getBulletTexturePath());
    }

    public void update(float delta, Vector2 heroPos, Vector2 targetPos) {
        shootTimer += delta;

        boolean attackInput = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        // Shooting interval depends on attack speed (attacks per second)
        float interval = 1f / hero.getAttackSpeed();

        if (attackInput && shootTimer >= interval) {
            shootTimer -= interval;

            // Base bullet
            bullets.add(new Bullet(bulletTexture, new Vector2(heroPos), new Vector2(targetPos)));

            // Goose passive — multishot spread
            if (hero instanceof Goose) {
                Goose goose = (Goose) hero;
                int extra = goose.getExtraShots();

                if (extra > 0) {
                    float spreadAngle = 15f + (extra * 5f);
                    addSpreadBullets(heroPos, targetPos, extra, spreadAngle, goose.isDiagonalRight());
                }
            }
        }

        // Update bullets
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

    /**
     * Adds spread bullets for Goose's multishot passive.
     * Alternates left/right diagonals based on passive state.
     */
    private void addSpreadBullets(Vector2 start, Vector2 target, int extra, float spreadAngle, boolean diagonalRight) {
        Vector2 baseDir = new Vector2(target).sub(start).nor();

        int sideCount = extra;
        float angleStep = spreadAngle / (sideCount + 1);

        // Alternate bullets — first left, then right, next pair wider, etc.
        for (int i = 1; i <= sideCount; i++) {
            float angle = angleStep * i;
            Vector2 dir;

            // Determine side based on passive alternation
            if (diagonalRight) {
                // start right then alternate left-right
                dir = (i % 2 == 1)
                    ? rotateVector(baseDir, -angle)
                    : rotateVector(baseDir, angle);
            } else {
                // start left then alternate right-left
                dir = (i % 2 == 1)
                    ? rotateVector(baseDir, angle)
                    : rotateVector(baseDir, -angle);
            }

            Vector2 bulletTarget = start.cpy().add(dir.scl(2f));
            bullets.add(new Bullet(bulletTexture, start, bulletTarget));
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
