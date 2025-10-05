package com.ISNE12.project;

import com.badlogic.gdx.math.Vector2;

public class Character {
    // --- Combat Stats ---
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;

    // --- Position & Movement ---
    private Vector2 position;

    // --- Player Tracking ---
    private int kills;
    private int stamina;
    private int maxStamina;

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

    // --- Player Tracking Logic ---
    public void addKill() {
        kills++;
    }

    public void useStamina(int amount) {
        stamina -= amount;
        if (stamina < 0) stamina = 0;
    }

    public void recoverStamina(int amount) {
        stamina += amount;
        if (stamina > maxStamina) stamina = maxStamina;
    }

    // --- Getters ---
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public Vector2 getPosition() { return position; }
    public int getKills() { return kills; }
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }

    public boolean isAlive() {
        return hp > 0;
    }
}
