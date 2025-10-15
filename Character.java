package com.ISNE12.project;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

//Base class for all playable characters.
public abstract class Character {

    // === Stats ===
    protected int maxHp, hp, attack;
    protected float attackSpeed;   // attacks per second

    // === Movement ===
    protected Vector2 position;
    protected float baseSpeed, speed;

    // === State ===
    protected int kills;
    protected float stateTime = 0f; // for animation timing

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
    public void move(float dx, float dy) { position.add(dx, dy); }

    // === Combat ===
    public void takeDamage(int damage){
        hp = Math.max(0, hp - damage);

        // Play hit sound when taking damage
        SoundManager.getInstance().playHit();
    }
    public void heal(int amount) { hp = Math.min(maxHp, hp + amount); }

    // Add kill and trigger passive effect
    public void addKill() {
        kills++;
        applyPassive();
    }

    public boolean isDead() { return hp <= 0; }

    // === Animation (to be implemented by subclass) ===
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
    public void setSpeed(float newSpeed) { speed = Math.max(0f, newSpeed); }
    public void setAttackSpeed(float newAttackSpeed) { attackSpeed = Math.max(0.1f, newAttackSpeed); }
}
