package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

// AttackManager — controls shooting logic and bullet updates. Works with both Goose (multishot) and Giraffe (attack speed).
public class AttackManager {
    private final Texture bulletTexture;
    private final Array<Bullet> bullets = new Array<>();
    private final Character hero;

    private float shootTimer = 0f; // controls fire rate

    public AttackManager(Character hero) {
        this.hero = hero;
        this.bulletTexture = new Texture(hero.getBulletTexturePath());
    }

    /** Updates attack timing and bullet behavior each frame */
    public void update(float delta, Vector2 heroPos, Vector2 targetPos) {
        shootTimer += delta;

        boolean attackInput = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        float interval = 1f / hero.getAttackSpeed(); // fire rate

        // Shoot if enough time passed
        if (attackInput && shootTimer >= interval) {
            shootTimer -= interval;

            // Main bullet
            bullets.add(new Bullet(bulletTexture, new Vector2(heroPos), new Vector2(targetPos)));

            // Goose multishot passive
            if (hero instanceof Goose) {
                Goose goose = (Goose) hero;
                int extra = goose.getExtraShots();
                if (extra > 0) {
                    float spreadAngle = 15f + (extra * 5f);
                    addSpreadBullets(heroPos, targetPos, extra, spreadAngle, goose.isDiagonalRight());
                }
            }
        }

        // Update all bullets
        for (Bullet b : bullets) b.update(delta);

        // Remove inactive bullets
        for (int i = bullets.size - 1; i >= 0; i--)
            if (!bullets.get(i).isActive()) bullets.removeIndex(i);
    }

    /** Adds spread bullets for Goose’s passive */
    private void addSpreadBullets(Vector2 start, Vector2 target, int extra, float spreadAngle, boolean diagonalRight) {
        Vector2 baseDir = new Vector2(target).sub(start).nor();
        float angleStep = spreadAngle / (extra + 1);

        // Alternate left/right pattern
        for (int i = 1; i <= extra; i++) {
            float angle = angleStep * i;
            Vector2 dir = (diagonalRight)
                ? (i % 2 == 1 ? rotateVector(baseDir, -angle) : rotateVector(baseDir, angle))
                : (i % 2 == 1 ? rotateVector(baseDir, angle) : rotateVector(baseDir, -angle));

            Vector2 bulletTarget = start.cpy().add(dir.scl(2f));
            bullets.add(new Bullet(bulletTexture, start, bulletTarget));
        }
    }

    /** Rotates a vector by given degrees */
    private Vector2 rotateVector(Vector2 v, float degrees) {
        float rad = (float) Math.toRadians(degrees);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        return new Vector2(v.x * cos - v.y * sin, v.x * sin + v.y * cos).nor();
    }

    /** Draws bullets on screen */
    public void render(SpriteBatch batch) {
        for (Bullet b : bullets) b.render(batch);
    }

    public Array<Bullet> getBullets() { return bullets; }

    public void dispose() { bulletTexture.dispose(); }
}
