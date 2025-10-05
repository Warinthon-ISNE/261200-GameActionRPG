package com.ISNE12.project;

public class EnemyStat {
    public float HP;
    public int ATK;
    public float DEF;

    // constructor
    public EnemyStat(float HP, int ATK, float DEF) {
        this.HP = HP;
        this.ATK = ATK;
        this.DEF = DEF;
    }

    // take damage logic
    public void gotDamage(int damage) {
        if (DEF > 0) {
            DEF -= damage;
            if (DEF <= 0) {
                HP += DEF; // carry over excess damage
                DEF = 0;
            }
        } else {
            HP -= damage;
        }
        HP = Math.max(HP, 0); // prevent negative HP
    }

    public float getHP() {
        return HP;
    }
}
