package com.ISNE12.project;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Character {
    // --- Combat Stats ---
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;

    // --- Position & Movement ---
    protected Vector2 position;

    // --- Player Tracking ---
    protected int kills;
    protected int stamina;
    protected int maxStamina;

    // --- Animation Timing ---
    protected float stateTime = 0f;

    // --- Constructor ---
    public Character(int hp, int attack, int defense, float startX, float startY) {
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.position = new Vector2(startX, startY);
        this.kills = 0;
        this.maxStamina = 100;
        this.stamina = maxStamina;
    }

    // --- Movement ---
    public void move(float dx, float dy) {
        position.add(dx, dy);
    }

    // --- Combat Logic ---
    public void takeDamage(int damage) {
        int reduced = damage - defense;
        if (reduced < 0) reduced = 0;
        hp -= reduced;
        if (hp < 0) hp = 0;
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public void attack(Character target) {
        target.takeDamage(this.attack);
    }

    public void addKill() { kills++; }

    public void useStamina(int amount) {
        stamina -= amount;
        if (stamina < 0) stamina = 0;
    }

    public void recoverStamina(int amount) {
        stamina += amount;
        if (stamina > maxStamina) stamina = maxStamina;
    }

    // --- Animation hooks ---
    public abstract void updateAnimation(float delta, boolean moving, String direction, boolean facingRight);
    public abstract TextureRegion getCurrentFrame();

    // --- Passive / Ability hooks ---
    public abstract void applyPassive();
    public abstract void useSpecialAbility();

    // --- Getters ---
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public Vector2 getPosition() { return position; }
    public int getKills() { return kills; }
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public boolean isAlive() { return hp > 0; }
}

