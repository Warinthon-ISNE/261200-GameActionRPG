package ISNE12.project;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    protected Vector2 position;     // direction x,y
    protected float speed;
    protected boolean isDead = false;  // check died
    protected float deathTimer = 0f;
    protected float bodyRemove = 3f;   // seconds before removing body
    protected EnemyStat stat;
    protected Character target;
    protected Rectangle bounds;    // for collision

    // --- Constructor ---
    public Enemy(float x, float y, EnemyStat stat, float speed, Character target) {
        this.position = new Vector2(x, y);
        this.stat = stat;
        this.speed = speed;
        this.target = target;
        this.bounds = new Rectangle(x, y, 1f, 1f); // default enemy size 1x1
    }

    // --- Update ---
    public void update(float delta) {
        if (isDead) {
            deathTimer += delta;
            if (deathTimer >= bodyRemove) {
                dispose(); // ready to remove after timer
            }
            return;
        }

        aiming(delta); // Enemy alive -> move toward hero
        bounds.setPosition(position.x, position.y);
    }

    // --- Move toward target ---
    protected void aiming(float delta) {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0.1f) { // move only if not too close
            position.x += (dx / distance) * speed * delta;
            position.y += (dy / distance) * speed * delta;
        }
    }

    // --- Take damage ---
    public void gotDamage(int damage) {
        stat.gotDamage(damage);
        if (stat.getHP() <= 0) {
            die();
        }
    }

    // --- Handle death ---
    protected void die() {
        if (!isDead) {
            isDead = true;
            deathTimer = 0f;
        }
    }

    public void dispose() {
        // placeholder for cleanup or texture removal
    }

    // --- Getters ---
    public boolean isDead() {
        return isDead;
    }

    public Vector2 getPosition() {
        return position;
    }

    public EnemyStat getStat() {
        return stat;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
