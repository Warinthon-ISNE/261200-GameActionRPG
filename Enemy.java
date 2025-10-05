package ISNE.lab.preGame.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy {
    protected Vector2 position;//direction x,y
    protected float speed;
    protected EnemyStat stat;
    protected Character target;

    protected EnemyState state;
    protected EnemyFacing facing;
    protected boolean isDead = false; //check died
    protected boolean Dying = false;
    protected float timer = 0f;

    //Animation loop, walk direction + other idle
    protected TextureRegion currentFrame; //current state
    protected Animation<TextureRegion> walkUp;
    protected Animation<TextureRegion> walkDown;
    protected Animation<TextureRegion> walkLeft;
    protected Animation<TextureRegion> walkRight;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> damagedAnimation;
    protected Animation<TextureRegion> deathAnimation;

    //constructor
    public Enemy(float x, float y, EnemyStat stat, float speed, Character target){
        this.position = new Vector2(x, y); //may be random spawn
        this.stat = stat;
        this.speed = speed;
        this.target = target;
    }
    public void update(float delta) {
        if (isDead) return;

        timer += delta;
        aiming(delta);
        updateDirection();

        switch (state) {
            case WALK:
                updateWalkAnimation();
                break;
            case ATTACK:
                updateAttackAnimation();
                break;
            case DAMAGED:
                updateDamagedAnimation();
                break;
            case DEAD:
                updateDeathAnimation();
                break;
        }
    }
    
    protected void aiming (float delta) {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1f) {
            position.x += (dx / distance) * speed * delta;
            position.y += (dy / distance) * speed * delta;
        }else {
            setState(EnemyState.ATTACK);
        }
    }

    protected void updateDirection() {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            facing = (dx > 0) ? facing.RIGHT : facing.LEFT;
        } else {
            facing = (dy > 0) ? facing.UP : facing.DOWN;
        }
    }

    protected void updateWalkAnimation() {
        switch (facing) {
            case UP:
                currentFrame = walkUp.getKeyFrame(timer, true);
                break;
            case DOWN:
                currentFrame = walkDown.getKeyFrame(timer, true);
                break;
            case LEFT:
                currentFrame = walkLeft.getKeyFrame(timer, true);
                break;
            case RIGHT:
                currentFrame = walkRight.getKeyFrame(timer, true);
                break;
        }
    }

    protected void updateAttackAnimation() {
        currentFrame = attackAnimation.getKeyFrame(timer, false);
        if (attackAnimation.isAnimationFinished(timer)) {
            state = EnemyState.WALK;
            timer = 0f;
        }
    }

    protected void updateDamagedAnimation() {
        currentFrame = damagedAnimation.getKeyFrame(timer, false);
        if (damagedAnimation.isAnimationFinished(timer)) {
            state = EnemyState.WALK;
            timer = 0f;
        }
    }

    protected void updateDeathAnimation() {
        currentFrame = deathAnimation.getKeyFrame(timer, false);
        if (deathAnimation.isAnimationFinished(timer)) {
            dispose();
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    //takeDamage
    public void gotDamage(int damage){
        stat.gotDamage(damage);
        if (stat.getHP() <= 0 && !isDead) {
            die();
        } else {
            setState(EnemyState.DAMAGED);
        }
    }

    protected void setState(EnemyState newState) {
        if (state != newState) {
            state = newState;
            timer = 0f;
        }
    }

    protected void die() {
        isDead = true;
        state = EnemyState.DEAD;
        timer = 0f;
    }

    public boolean isDying() {
        return Dying;
    }

    public void dispose(){
        Dying = true;
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
