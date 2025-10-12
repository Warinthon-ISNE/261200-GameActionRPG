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
    
    // from enemy sync dynamic -> get buff and debuff for a while
    protected float speed;       // current speed (affected by debuff)
    protected float baseSpeed;   // default speed (for reset after debuff)

    protected float speedDebuffPercent = 0f;
    protected float attackDebuffPercent = 0f;
    protected float defenseDebuffPercent = 0f;

    protected float speedDebuffTimer = 0f;
    protected float attackDebuffTimer = 0f;
    protected float defenseDebuffTimer = 0f;
    
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
    
// Adding to sync Enemy logic and skills
    public void gainDEF(int amount) {
        defense += amount;
    }
    
    public void decreaseSpeed(float percent, float duration) {
        speedDebuffPercent += percent;
        speedDebuffTimer = Math.max(speedDebuffTimer, duration);
        updateDebuffedStats();
    }

    public void decreaseAttack(float percent, float duration) {
        attackDebuffPercent += percent;
        attackDebuffTimer = Math.max(attackDebuffTimer, duration);
        updateDebuffedStats();
    }

    public void decreaseDefense(float percent, float duration) {
        defenseDebuffPercent += percent;
        defenseDebuffTimer = Math.max(defenseDebuffTimer, duration);
        updateDebuffedStats();
    }

    private void updateDebuffedStats() {
        speed = baseSpeed * (1f - speedDebuffPercent);
        attack = (int) (attack * (1f - attackDebuffPercent));
        defense = (int) (defense * (1f - defenseDebuffPercent));
    }

    private void resetDebuff() {
        speedDebuffPercent = 0;
        attackDebuffPercent = 0;
        defenseDebuffPercent = 0;
        speed = baseSpeed;
    }

    public void update(float delta) {
        if (!isAlive()) return;

        if (speedDebuffTimer > 0) speedDebuffTimer -= delta;
        if (attackDebuffTimer > 0) attackDebuffTimer -= delta;
        if (defenseDebuffTimer > 0) defenseDebuffTimer -= delta;

        if (speedDebuffTimer <= 0 && attackDebuffTimer <= 0 && defenseDebuffTimer <= 0) {
            resetDebuff();
        }
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

