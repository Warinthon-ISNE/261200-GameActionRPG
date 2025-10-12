package ISNE12.project;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Character {
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int kills;
    protected Vector2 position;
    protected float stateTime = 0f;

    public Character(int hp, int attack, int defense, float startX, float startY) {
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.position = new Vector2(startX, startY);
        this.kills = 0;
    }

    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getMaxHp() { return maxHp; }
    public int getKills() { return kills; }
    public Vector2 getPosition() { return position; }

    public void move(float dx, float dy) {
        position.add(dx, dy);
    }

    public void setHp(int hp) { this.hp = Math.min(hp, maxHp); }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }

    // ฟังก์ชันรักษา (heal)
    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    // ฟังก์ชันโจมตี
    public void attack(Character target) {
        target.takeDamage(this.attack);
        if (!target.isAlive()) {
            addKill();
        }
    }

    // ฟังก์ชันรับดาเมจ
    public void takeDamage(int damage) {
        int reducedDamage = damage - defense;
        if (reducedDamage < 0) reducedDamage = 0;
        hp -= reducedDamage;
        if (hp < 0) hp = 0;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void addKill() {
        kills++;
    }

    // Virtual methods (override ได้ใน subclass)
    public void updateAnimation(float delta, boolean moving, String direction, boolean facingRight) {}
    public void applyPassive() {}
    public void useSpecialAbility() {}

    public TextureRegion getCurrentFrame() { return null; }
}
