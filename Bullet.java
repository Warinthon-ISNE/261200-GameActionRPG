package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private static final float SPEED = 10f;  // bullet speed (world units per second)
    private static final float SIZE = 0.2f;  // bullet size in world units
    private static final float WORLD_WIDTH = 20f;  // match GameScreen
    private static final float WORLD_HEIGHT = 14f;

    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private Texture texture;
    private Rectangle bounds;

    public Bullet(Texture texture, Vector2 startPos, Vector2 targetPos) {
        this.texture = texture;

        // Position & velocity in world space
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(targetPos).sub(startPos).nor().scl(SPEED);
        this.active = true;

        // Create collision bounds
        this.bounds = new Rectangle(position.x, position.y, SIZE, SIZE);
    }

    public void update(float delta) {
        if (!active) return;

        // Move in world space
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Update hitbox
        bounds.setPosition(position.x, position.y);

        // Deactivate if bullet leaves the map area
        if (position.x < -1f || position.x > WORLD_WIDTH + 1f ||
            position.y < -1f || position.y > WORLD_HEIGHT + 1f) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            // Draw in world coordinates (scaled for your world units)
            batch.draw(texture, position.x, position.y, SIZE, SIZE);
        }
    }

    // === Getters / Setters ===
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
