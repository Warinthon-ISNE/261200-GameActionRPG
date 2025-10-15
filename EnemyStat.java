package com.ISNE12.project;

/**
 * EnemyStat — holds basic combat stats for enemies (HP & ATK).
 * Simplified: no defense stat used in this game.
 */
public class EnemyStat {

    private float HP;
    private final float maxHP;
    private int ATK;

    /** Constructor */
    public EnemyStat(float HP, int ATK) {
        this.HP = HP;
        this.maxHP = HP;
        this.ATK = ATK;
    }

    /** Applies raw damage directly to HP */
    public void gotDamage(int damage) {
        if (damage <= 0) return;
        HP -= damage;
        if (HP < 0) HP = 0;
    }

    /** Heals HP up to maximum */
    public void heal(float amount) {
        if (amount <= 0) return;
        HP = Math.min(maxHP, HP + amount);
    }

    /** Whether this enemy is dead */
    public boolean isDead() {
        return HP <= 0;
    }

    /** HP ratio (0–1) useful for HP bars */
    public float getHpPercent() {
        return maxHP > 0 ? HP / maxHP : 0f;
    }

    // === Getters ===
    public float getHP() { return HP; }
    public float getMaxHP() { return maxHP; }
    public int getATK() { return ATK; }

    // === Setters ===
    public void setATK(int ATK) { this.ATK = ATK; }

    @Override
    public String toString() {
        return String.format("HP: %.1f / %.1f | ATK: %d", HP, maxHP, ATK);
    }
}
