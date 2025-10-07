import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Enemy {
    // Enemy Stats 
    protected int HP;
    protected int ATK;
    protected int DEF;

    protected Vector2 position;//direction x,y
    protected float speed;
    protected Character target;
    protected boolean isDead = false;
    protected boolean Dying = false;
    protected float deathTimer = 0f;
    protected float bodyRemove = 3f;
    protected Rectangle bounds;

    //Enemy state
    protected EnemyState state = EnemyState.WALK;
    protected EnemyFacing facing;
    protected float stateTime = 0f;

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
    public Enemy(float x, float y, int HP, int ATK, int DEF, float speed, Character target){
        this.position = new Vector2(x, y); //may random spawn
        this.HP = HP;
        this.ATK = ATK;
        this.DEF = DEF;
        this.speed = speed;
        this.target = target;
        this.bounds = new Rectangle(x, y, 1f, 1f); // default enemy size
    }
    public void update(float delta) {
        stateTime += delta;

        if (isDead) {
            updateDeathAnimation();
            deathTimer += delta;
            if (deathTimer >= bodyRemove) {
                dispose(); // mark for removal
            }
            return;
        }

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

        bounds.setPosition(position.x, position.y);
    }

    // --- Move toward target ---
    protected void aiming(float delta) {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0.1f) { // move closer
            position.x += (dx / distance) * speed * delta;
            position.y += (dy / distance) * speed * delta;
            setState(EnemyState.WALK);
        } else {
            setState(EnemyState.ATTACK);
        }
    }

    protected void updateDirection() {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            facing = (dx > 0) ? EnemyFacing.RIGHT : EnemyFacing.LEFT;
        } else {
            facing = (dy > 0) ? EnemyFacing.UP : EnemyFacing.DOWN;
        }
    }

    protected void updateWalkAnimation() {
        switch (facing) {
            case UP:
                currentFrame = walkUp != null ? walkUp.getKeyFrame(stateTime, true) : currentFrame;
                break;
            case DOWN:
                currentFrame = walkDown != null ? walkDown.getKeyFrame(stateTime, true) : currentFrame;
                break;
            case LEFT:
                currentFrame = walkLeft != null ? walkLeft.getKeyFrame(stateTime, true) : currentFrame;
                break;
            case RIGHT:
                currentFrame = walkRight != null ? walkRight.getKeyFrame(stateTime, true) : currentFrame;
                break;
        }
    }

    protected void updateAttackAnimation() {
        if (attackAnimation == null) return;
        currentFrame = attackAnimation.getKeyFrame(stateTime, false);
        if (attackAnimation.isAnimationFinished(stateTime)) {
            setState(EnemyState.WALK);
        }
    }

    protected void updateDamagedAnimation() {
        if (damagedAnimation == null) return;
        currentFrame = damagedAnimation.getKeyFrame(stateTime, false);
        if (damagedAnimation.isAnimationFinished(stateTime)) {
            setState(EnemyState.WALK);
        }
    }

    protected void updateDeathAnimation() {
        if (deathAnimation == null) return;
        currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        if (deathAnimation.isAnimationFinished(stateTime)) {
            dispose(); // removed from array
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    //takeDamage
    public void gotDamage(int damage) {
        int finalDamage = Math.max(0, damage - DEF);
        HP -= finalDamage;

        if (HP <= 0 && !isDead) {
            die();
        } else {
            setState(EnemyState.DAMAGED);
        }
    }

    protected void setState(EnemyState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f;
        }
    }

    protected void die() {
        isDead = true;
        deathTimer = 0f;
        setState(EnemyState.DEAD);
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

    public int getHP() {
        return HP;
    }
    public int getATK() {
        return ATK;
    }
    public int getDEF() {
        return DEF;
    }
}
