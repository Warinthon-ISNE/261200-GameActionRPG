package ISNE.lab.preGame.Entities;

import com.badlogic.gdx.Gdx;

public class Enemy {
    protected float x, y; //direction
    protected int speed;
    protected boolean isDead = false; //check died
    protected float deathTimer = 0f;
    protected float bodyRemove = 5f;
    protected EnemyStat stat;
    protected Character target; //Called from Character.java Needed!!
    float delta = Gdx.graphics.getDeltaTime();

    //constructor
    public Enemy(float x, float y, EnemyStat stat, int speed, Character target){
        this.x = x;
        this.y = y;
        this.stat = stat;
        this.speed = speed;
    }

    //update moveable
    public void update(float delta) {
        if(isDead){
            deathTimer += delta;
            if(deathTimer >= bodyRemove){
                dispose();
            }
            return; //in case of death -> remove
        }
        aiming(delta); // alive -> enemy m toward Character
    }

    protected void aiming(float delta) { //called Character as target
        /*
        //NEED Character.java to be continue
        //update Character distance by minus enemy location
        float dx = target.getx() - x;
        float dy = target.gety() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1) {
            float nx = dx / distance;
            float ny = dy / distance;

            x += nx * speed * delta;
            y += ny * speed * delta;
        }*/
    }

    public void gotDamage(int damage){
        stat.gotDamage(damage);
        if(stat.HP <= 0 && isDead()) {
            die();
        }
    }

    protected void die(){
        isDead = true;
        deathTimer += delta;
        if(deathTimer >= bodyRemove) { //if already dead time > time to remove
            dispose();
        }
    }
    public void dispose(){

    } //remove enemy from the game

    // Getter
    public boolean isDead() {
        return isDead;
    }

    public float getx() {
        return x;
    }
    public float gety() {
        return y;
    }
    public EnemyStat getStat() {
        return stat;
    }
}

