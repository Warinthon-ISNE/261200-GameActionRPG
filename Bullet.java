package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Texture texture;
    private final Vector2 position;
    private final Vector2 velocity;
    private final float speed = 8f;
    private final float size = 0.2f;

    public Bullet(Texture texture, Vector2 startPos, Vector2 targetPos) {
        this.texture = texture;
        this.position = new Vector2(startPos);

        // Direction = normalized vector toward target
        Vector2 direction = targetPos.cpy().sub(startPos).nor();
        this.velocity = direction.scl(speed);
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - size / 2f, position.y - size / 2f, size, size);
    }

    public boolean isOutOfBounds(float worldWidth, float worldHeight) {
        return position.x < 0 || position.x > worldWidth || position.y < 0 || position.y > worldHeight;
    }

    public void dispose() {
        texture.dispose();
    }
}
