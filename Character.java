package com.ISNE12.project;

import com.badlogic.gdx.math.Vector2;

public class Character {
    private int hp;
    private int attack;
    private int defense;
    private Vector2 position;  // character position (x,y)


    public Character(int hp, int attack, int defense, float startX, float startY) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.position = new Vector2(startX, startY);
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void move(float dx, float dy) {
        position.add(dx, dy);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    // Take damage
    public void takeDamage(int damage) {
        int reducedDamage = damage - defense;
        if (reducedDamage < 0) reducedDamage = 0; // defense can't heal
        hp -= reducedDamage;
        if (hp < 0) hp = 0;
    }

    // Attack another character
    public void attack(Character target) {
        target.takeDamage(this.attack);
    }



    public boolean isAlive() {
        return hp > 0;
    }
}
