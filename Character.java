package com.ISNE12.project;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Character {

    // === Basic Stats ===
    protected int maxHp;
    protected int hp;
    protected int attack;
    protected float attackSpeed; // attacks per second (used for fire rate)

    // === Position & Movement ===
    protected Vector2 position;
    protected float baseSpeed;
    protected float speed;

    // === Gameplay State ===
    protected int kills;
    protected float stateTime = 0f;

    // === Constructor ===
    public Character(int maxHp, int attack, float attackSpeed, float startX, float startY) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.attackSpeed = attackSpeed;
        this.position = new Vector2(startX, startY);
        this.kills = 0;

        this.baseSpeed = 2f;
        this.speed = baseSpeed;
    }

    // === Movement ===
    public void move(float dx, float dy) {
        position.add(dx, dy);
    }

    // === Combat ===
    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void addKill() {
        kills++;
        applyPassive(); // Recalculate passives when kill count changes
    }

    public boolean isDead() {
        return hp <= 0;
    }

    // === Animation Hooks ===

    public abstract void updateAnimation(float delta, boolean moving, String direction, boolean facingRight);

    public abstract TextureRegion getCurrentFrame();

    // === Passive & Shooting ===
    public abstract void applyPassive();
    public abstract String getBulletTexturePath();

    // === Getters ===
    public Vector2 getPosition() { return position; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public float getAttackSpeed() { return attackSpeed; }
    public int getKills() { return kills; }
    public float getSpeed() { return speed; }

    // === Setters ===
    public void setSpeed(float newSpeed) {
        this.speed = Math.max(0f, newSpeed);
    }

    public void setAttackSpeed(float newAttackSpeed) {
        this.attackSpeed = Math.max(0.1f, newAttackSpeed);
    }
}
