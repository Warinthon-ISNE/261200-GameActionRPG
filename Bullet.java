package ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private static final float SPEED = 10f;  // bullet speed
    private static final float SIZE = 0.2f;  // bullet size in world units

    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private Texture texture;
    private Rectangle bounds;

    public Bullet(Texture texture, Vector2 startPos, Vector2 targetPos) {
        this.texture = texture;
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(targetPos).sub(startPos).nor().scl(SPEED);
        this.active = true;
        this.bounds = new Rectangle(position.x, position.y, SIZE, SIZE);
    }

    public void update(float delta) {
        if (!active) return;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Update hitbox
        bounds.setPosition(position.x, position.y);

        // Remove if offscreen (just in case)
        if (position.x < -1 || position.x > 10 || position.y < -1 || position.y > 10) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, SIZE, SIZE);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
