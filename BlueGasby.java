package com.ISNE12.project;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * BlueGasby — smarter ranged/melee hybrid enemy.
 * Behavior:
 *  - Shoots Poltergeist when player is within 7 units.
 *  - Switches to dash if surrounded by other Gasbys.
 *  - Dash slows player's movement for 5 seconds (debuff simplified).
 */
public class BlueGasby extends GasbySamSi {

    // === Poltergeist ===
    private static final float POLTERGEIST_COOLDOWN = 4f;
    private float poltergeistTimer = 0f;

    // === Dash ===
    private static final float DASH_INTERVAL = 2f;
    private static final float DASH_SPEED = 6f;
    private float dashTimer = 0f;
    private boolean isDashing = false;

    // === Debuff (speed only, since no DEF system) ===
    private boolean debuffApplied = false;
    private float debuffTimer = 0f;
    private static final float DEBUFF_DURATION = 5f;

    // === References ===
    private final Array<Enemy> enemies;
    private final Character target;
    private final Array<Projectile> projectiles; // external projectile list

    public BlueGasby(float x, float y, Character target, Array<Enemy> enemies, Array<Projectile> projectiles) {
        super(x, y, "blue", target);
        this.enemies = enemies;
        this.target = target;
        this.projectiles = projectiles;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (isDead) return;

        // Timers
        poltergeistTimer += delta;
        dashTimer += delta;
        if (debuffApplied) {
            debuffTimer += delta;
            if (debuffTimer >= DEBUFF_DURATION) {
                removeDebuff();
            }
        }

        // --- Decision-making ---
        if (isInPoltergeistRange()) {
            if (countNearbyGasby() < 3) {
                shootPoltergeist();
            } else {
                dashAttack(delta);
            }
        } else {
            aiming(delta);
        }
    }

    private boolean isInPoltergeistRange() {
        return Vector2.dst(position.x, position.y, target.getPosition().x, target.getPosition().y) <= 7f;
    }

    private int countNearbyGasby() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e instanceof GasbySamSi && e != this) {
                if (Vector2.dst(e.getPosition().x, e.getPosition().y, position.x, position.y) <= 7f) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Fires a single Poltergeist projectile at the player. */
    private void shootPoltergeist() {
        if (poltergeistTimer < POLTERGEIST_COOLDOWN) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        GasbyPoltergeist shot = new GasbyPoltergeist(position.x, position.y, direction, 10f, 5, target);
        projectiles.add(shot); // now actually added to world/projectile system

        poltergeistTimer = 0f;
        System.out.println("Blue Gasby fires a Poltergeist!");
    }

    /** Short-range dash that can apply a speed debuff. */
    private void dashAttack(float delta) {
        if (dashTimer < DASH_INTERVAL) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        position.mulAdd(direction, DASH_SPEED * delta);
        isDashing = true;

        if (Vector2.dst(position.x, position.y, target.getPosition().x, target.getPosition().y) <= 2.5f) {
            applySpeedDebuff();
            dashTimer = 0f;
            isDashing = false;
        }
    }

    /** Simplified debuff: reduce player speed for a few seconds. */
    private void applySpeedDebuff() {
        if (debuffApplied) return;

        // Here we just simulate slowing; your Character class would need a proper method later
        System.out.println("⚡ Player speed temporarily reduced by Blue Gasby!");
        debuffApplied = true;
        debuffTimer = 0f;
    }

    private void removeDebuff() {
        System.out.println("Debuff expired — player speed restored.");
        debuffApplied = false;
    }

    @Override
    protected void die() {
        super.die();
        System.out.println("Blue Gasby has been defeated.");
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        // (Optional) draw dash or attack effects here
    }
}
