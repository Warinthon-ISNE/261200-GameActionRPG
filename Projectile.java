package com.ISNE12.project;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public abstract class Projectile {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float speed;
    protected int damage;
    protected boolean active = true;
    protected Rectangle bounds;
    protected float maxTravelDistance = 12f;
    protected float traveledDistance = 0f;

    public Projectile(float x, float y, Vector2 direction, float speed, int damage, float size, float maxTravelDistance) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(direction).nor().scl(speed);
        this.speed = speed;
        this.damage = damage;
        this.bounds = new Rectangle(x, y, size, size);
        this.maxTravelDistance = maxTravelDistance;
    }

    public void update(float delta) {
        if (!active) return;

        float dx = velocity.x * delta;
        float dy = velocity.y * delta;
        position.add(dx, dy);
        traveledDistance += Math.sqrt(dx * dx + dy * dy);

        // sync bounds with projectile position
        bounds.setPosition(position.x, position.y);

        if (traveledDistance >= maxTravelDistance) {
            active = false;
        }
    }

    public abstract void render(SpriteBatch batch);

    public boolean isActive() {
        return active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getDamage() {
        return damage;
    }

    public void disappear() {
        active = false;
    }
}
