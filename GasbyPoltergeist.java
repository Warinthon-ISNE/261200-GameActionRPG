package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

/**
 * GasbyPoltergeist — projectile shot by Gasby enemies.
 * Moves in a straight line, damages the player on hit, and disappears.
 */
public class GasbyPoltergeist extends Projectile {

    private static final Texture POLTERGEIST_TEXTURE = new Texture("poltergeist.png"); // shared texture
    private static final float SIZE = 0.6f;

    private final Rectangle bounds;
    private final Character target; // who this projectile can hit
    private float lifetime = 3f; // seconds before disappearing


    public GasbyPoltergeist(float x, float y, Vector2 direction, float speed, int damage, Character target) {
        super(x, y, direction, speed, damage, SIZE, 8f); // 8f = max lifetime
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.target = target;
    }

    @Override
    public void update(float delta) {
        if (!active) return;

        // Move the projectile
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Update hitbox
        bounds.setPosition(position.x, position.y);

        // Lifetime check
        lifetime -= delta;
        if (lifetime <= 0f) {
            disappear();
        }

        // Collision check
        checkHit();
    }

    /** Checks collision with player and applies damage */
    private void checkHit() {
        if (target == null || !active) return;

        // TODO: Ensure Character class has getPosition() or a bounding box
        Rectangle targetBounds = new Rectangle(target.getPosition().x, target.getPosition().y, 1f, 1f);
        if (bounds.overlaps(targetBounds)) {
            target.takeDamage(damage);
            disappear();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;
        batch.draw(POLTERGEIST_TEXTURE, position.x, position.y, SIZE, SIZE);
    }

    @Override
    public void disappear() {
        active = false;
    }

    public void dispose() {
        // Static texture shared, so do not dispose here!
        // Leave it to a global asset manager or at game shutdown.
    }
}
