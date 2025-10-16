package com.ISNE12.project;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/**
 * GasbySamSi — Base class for Red & Blue Gasby enemies.
 * - Blue: Uses ranged Poltergeist attack.
 * - Red: Uses dash melee attack.
 */
public class GasbySamSi extends Enemy {

    protected final String color;
    private final Random random = new Random();

    // === Attack properties ===
    protected float attackCooldown = 4f; // seconds
    protected float attackTimer = 0f;
    protected float dashRange = 4f;
    protected float shootRange = 7f;
    protected float dashSpeed = 6f;
    protected boolean isDashing = false;

    public GasbySamSi(float x, float y, String color, Character target) {
        super(x, y, getHP(color), getATK(color), 0.5f, target);
        this.color = color.toLowerCase();
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            super.update(delta);
            return;
        }

        attackTimer += delta;

        // Choose behavior
        if (shouldAttack()) {
            performAttack(delta);
        } else {
            aiming(delta); // move toward player
        }

        // Let base class handle animation and bounds
        super.update(delta);
    }

    /** Determines whether Gasby should attack (by range + cooldown). */
    protected boolean shouldAttack() {
        float distance = Vector2.dst(position.x, position.y,
            target.getPosition().x, target.getPosition().y);
        return attackTimer >= attackCooldown && distance <= (color.equals("red") ? dashRange : shootRange);
    }

    /** Performs the correct attack depending on color. */
    protected void performAttack(float delta) {
        attackTimer = 0f; // reset cooldown

        if (color.equals("blue")) {
            performPoltergeist();
        } else if (color.equals("red")) {
            performDash(delta);
        }
    }

    /** Blue Gasby — shoot Poltergeist projectile toward player. */
    private void performPoltergeist() {
        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        GasbyPoltergeist projectile =
            new GasbyPoltergeist(position.x, position.y, direction, 10f, 5, target);

        // Typically you'd add this to a global projectile list
        // Example: projectileManager.add(projectile);
        System.out.println("Blue Gasby fires a Poltergeist!");
    }

    /** Red Gasby — dash attack (short-range charge). */
    private void performDash(float delta) {
        isDashing = true;
        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        position.mulAdd(direction, dashSpeed * delta);

        // Apply damage if close enough
        if (Vector2.dst(position.x, position.y,
            target.getPosition().x, target.getPosition().y) <= 1.5f) {
            target.takeDamage(getATK());
            isDashing = false;
            System.out.println("Red Gasby hits player with dash!");
        }
    }

    // --- Color-based stats ---
    private static int getHP(String color) {
        switch (color.toLowerCase()) {
            case "red": return 70;
            case "blue": return 50;
            default: return 50;
        }
    }

    private static int getATK(String color) {
        switch (color.toLowerCase()) {
            case "red": return 35;
            case "blue": return 15;
            default: return 15;
        }
    }

    // --- On death ---
    @Override
    protected void die() {
        super.die();
        dropLoot();
    }

    private void dropLoot() {
        if ("red".equals(color) && random.nextInt(100) < 50) {
            int healAmount = (int) (target.getMaxHp() * 0.05f); // 5% heal
            target.heal(healAmount);
            System.out.println("Player healed by " + healAmount + " from Red Gasby drop!");
        }
    }

    @Override
    public void dispose() {
        // No textures owned directly here — handled by base class or manager.
    }
}
