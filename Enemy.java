package ISNE.lab.preGame.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    protected Vector2 position;//direction x,y
    protected float speed;
    protected boolean isDead = false; //check died
    protected float deathTimer = 0f;
    protected float bodyRemove = 5f;
    protected EnemyStat stat;
    protected Character target;

    //constructor
    public Enemy(float x, float y, EnemyStat stat, float speed, Character target){
        this.position = new Vecter2(x, y);
        this.stat = stat;
        this.speed = speed;
        this.target = target;
    }

    //update moveable
    public void update(float delta) {
        if(isDead){
            deathTimer += delta;
            if(deathTimer >= bodyRemove){
                dispose();
            }
            return; //in case of Enemy death -> remove
        }
        aiming(delta); // Enemy alive -> enemy move toward Character
    }
    protected void aiming(float delta){ //target = Character
        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if(distance > 1) {//update distance
            position.x += (dx / distance) * speed * delta;
            position.y += (dy / distance) * speed * delta;
        } else {
            if(target == null) return; //if no target (Character dead)
        }
    }

    //takeDamage
    public void gotDamage(int damage){
        stat.gotDamage(damage);
        if(stat.HP <= 0 && isDead()) {
            die();
        }
    }
    private void die(){
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

    public Vector2 getPosition() {
        return position;
    }

    public EnemyStat getStat() {
        return stat;
    }
}

