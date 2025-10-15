package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Bullet — moves forward and disappears when off-screen.
 */
public class Bullet {
    private static final float SPEED = 10f;       // movement speed
    private static final float SIZE = 0.25f;      // bullet size
    private static final float WORLD_WIDTH = 20f; // game world width
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
        this.bounds = new Rectangle(position.x, position.y, SIZE, SIZE);
    }

    /** Moves bullet and disables it when off-screen */
    public void update(float delta) {
        if (!active) return;

        position.mulAdd(velocity, delta);
        bounds.setPosition(position.x, position.y);

        if (isOffScreen()) active = false;
    }

    /** Draws bullet */
    public void render(SpriteBatch batch) {
        if (active) batch.draw(texture, position.x, position.y, SIZE, SIZE);
    }

    /** Checks if bullet left the game area */
    private boolean isOffScreen() {
        return position.x < -1f || position.x > WORLD_WIDTH + 1f
            || position.y < -1f || position.y > WORLD_HEIGHT + 1f;
    }

    // === Getters ===
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle getBounds() { return bounds; }
    public Vector2 getPosition() { return position; }

    public void deactivate() { this.active = false; }

    /** Shared texture (don’t dispose here) */
    public void dispose() { /* managed by AttackManager */ }
}
