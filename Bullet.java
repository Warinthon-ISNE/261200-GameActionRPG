package ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Sprite sprite;
    private Vector2 velocity;
    private float speed = 10f;
    private float damage;

    public Bullet(Texture texture, float x, float y, Vector2 direction, float damage) {
        sprite = new Sprite(texture);
        sprite.setSize(0.3f, 0.3f);
        sprite.setOriginCenter();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        this.velocity = direction.nor().scl(speed);
        this.damage = damage;

        // rotate sprite to face direction
        float angleDeg = direction.angleDeg();
        sprite.setRotation(angleDeg);
    }

    public void update(float delta) {
        sprite.translate(velocity.x * delta, velocity.y * delta);
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOutOfBounds(float worldWidth, float worldHeight) {
        return sprite.getX() < 0 || sprite.getX() > worldWidth ||
            sprite.getY() < 0 || sprite.getY() > worldHeight;
    }

    public float getDamage() {
        return damage;
    }
}
