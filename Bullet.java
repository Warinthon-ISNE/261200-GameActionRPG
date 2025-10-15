package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a single bullet fired by a character.
 * Handles movement, deactivation when off-screen, and rendering.
 */
public class Bullet {
    private static final float SPEED = 10f;       // world units per second
    private static final float SIZE = 0.25f;      // bullet size in world units
    private static final float WORLD_WIDTH = 20f; // depends on your game world
    private static final float WORLD_HEIGHT = 14f;

    private final Texture texture;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Rectangle bounds;
    private boolean active;

    public Bullet(Texture texture, Vector2 startPos, Vector2 targetPos) {
        this.texture = texture;
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(targetPos).sub(startPos).nor().scl(SPEED);
        this.active = true;

        // Define a small bounding box for collision
        this.bounds = new Rectangle(position.x, position.y, SIZE, SIZE);
    }

    /** Update bullet movement and deactivate when off-screen. */
    public void update(float delta) {
        if (!active) return;

        position.mulAdd(velocity, delta); // faster vector operation
        bounds.setPosition(position.x, position.y);

        // Deactivate when outside of the visible world bounds
        if (isOffScreen()) {
            active = false;
        }
    }

    /** Render bullet if active. */
    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, SIZE, SIZE);
        }
    }

    /** Determines whether the bullet has gone off-screen. */
    private boolean isOffScreen() {
        return position.x < -1f || position.x > WORLD_WIDTH + 1f
            || position.y < -1f || position.y > WORLD_HEIGHT + 1f;
    }

    // === Getters & Utility ===
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void deactivate() {
        this.active = false;
    }

    /** Disposes of the bullet texture — only call this if Bullet owns its texture. */
    public void dispose() {
        // NOTE: Texture is shared among all bullets via AttackManager
        // so we DON'T dispose it here individually.
    }
}
