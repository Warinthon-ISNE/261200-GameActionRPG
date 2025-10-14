package com.ISNE12.project;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Character {
    protected int maxHp;
    protected int hp;
    protected int attack;
    protected int defense;
    protected Vector2 position;
    protected int kills;

    // Attack speed system
    protected float attackSpeed = 1f;
    protected float attackCooldown = 0f;

    public Character(int maxHp, int attack, int defense, float startX, float startY) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.position = new Vector2(startX, startY);
        this.kills = 0;
    }

    public void move(float dx, float dy) {
        position.add(dx, dy);
    }

    public Vector2 getPosition() { return position; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getKills() { return kills; }
    public void addKill() { kills++; }

    // === Attack speed logic ===
    public boolean canAttack() { return attackCooldown <= 0f; }
    public void resetAttackCooldown() { attackCooldown = 1f / attackSpeed; }
    public void updateCooldown(float delta) { if (attackCooldown > 0) attackCooldown -= delta; }

    // === HP logic ===
    public void takeDamage(int damage) {
        int actual = Math.max(0, damage - defense);
        hp -= actual;
        if (hp < 0) hp = 0;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    // === Abstract methods ===
    public abstract void updateAnimation(float delta, boolean moving, String direction, boolean facingRight);
    public abstract TextureRegion getCurrentFrame();
    public abstract void applyPassive();
    public abstract void useSpecialAbility();
    public abstract String getBulletTexturePath(); // 👈 NEW METHOD
}
