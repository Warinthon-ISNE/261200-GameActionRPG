package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Enemy {
    // === Stats ===
    protected int HP, maxHP;
    protected int ATK;
    protected float speed;

    // === State & Position ===
    protected Vector2 position;
    protected Rectangle bounds;
    protected Character target;

    protected boolean isDead = false;
    protected boolean Dying = false;
    protected float deathTimer = 0f;
    protected float bodyRemove = 3f;

    // === Animation ===
    protected EnemyState state = EnemyState.WALK;
    protected EnemyFacing facing;
    protected float stateTime = 0f;
    protected TextureRegion currentFrame;
    protected Animation<TextureRegion> walkUp, walkDown, walkLeft, walkRight;
    protected Animation<TextureRegion> attackAnimation, damagedAnimation, deathAnimation;

    // === Attack System ===
    protected float attackCooldown = 1.2f;  // time between attacks
    protected float attackTimer = 0f;
    protected float attackRange = 1.0f;     // range to hit player

    public Enemy(float x, float y, int HP, int ATK, float speed, Character target) {
        this.position = new Vector2(x, y);
        this.maxHP = HP;
        this.HP = HP;
        this.ATK = ATK;
        this.speed = speed;
        this.target = target;
        this.bounds = new Rectangle(x, y, 1f, 1f); // default size
    }

    public void update(float delta) {
        stateTime += delta;
        attackTimer += delta;

        if (isDead) {
            updateDeathAnimation();
            deathTimer += delta;
            if (deathTimer >= bodyRemove) dispose();
            return;
        }

        // === AI behavior ===
        aiming(delta);
        checkAttack(delta);
        updateDirection();

        // === Animation ===
        switch (state) {
            case WALK: updateWalkAnimation(); break;
            case ATTACK: updateAttackAnimation(); break;
            case DAMAGED: updateDamagedAnimation(); break;
            case DEAD: updateDeathAnimation(); break;
        }

        bounds.setPosition(position.x, position.y);
    }

    /** Enemy moves toward the player */
    protected void aiming(float delta) {
        if (target == null) return;

        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > attackRange) {
            position.x += (dx / distance) * speed * delta;
            position.y += (dy / distance) * speed * delta;
            setState(EnemyState.WALK);
        } else {
            setState(EnemyState.ATTACK);
        }
    }


    /** New: Enemy attacks the player when close enough */
    protected void checkAttack(float delta) {
        if (target == null || target.isDead()) return;

        float distance = Vector2.dst(position.x, position.y, target.getPosition().x, target.getPosition().y);

        if (distance <= attackRange && attackTimer >= attackCooldown) {
            target.takeDamage(ATK);
            attackTimer = 0f; // reset cooldown
            Gdx.app.log("Enemy", "Dealt " + ATK + " damage to player!");
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
            case UP: currentFrame = (walkUp != null) ? walkUp.getKeyFrame(stateTime, true) : currentFrame; break;
            case DOWN: currentFrame = (walkDown != null) ? walkDown.getKeyFrame(stateTime, true) : currentFrame; break;
            case LEFT: currentFrame = (walkLeft != null) ? walkLeft.getKeyFrame(stateTime, true) : currentFrame; break;
            case RIGHT: currentFrame = (walkRight != null) ? walkRight.getKeyFrame(stateTime, true) : currentFrame; break;
        }
    }

    protected void updateAttackAnimation() {
        if (attackAnimation == null) return;
        currentFrame = attackAnimation.getKeyFrame(stateTime, false);
        if (attackAnimation.isAnimationFinished(stateTime)) setState(EnemyState.WALK);
    }

    protected void updateDamagedAnimation() {
        if (damagedAnimation == null) return;
        currentFrame = damagedAnimation.getKeyFrame(stateTime, false);
        if (damagedAnimation.isAnimationFinished(stateTime)) setState(EnemyState.WALK);
    }

    protected void updateDeathAnimation() {
        if (deathAnimation == null) return;
        currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        if (deathAnimation.isAnimationFinished(stateTime)) dispose();
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y);
    }

    public void enemyBar(ShapeRenderer shapeRenderer) {
        float width = 40f;
        float height = 5f;
        float x = position.x - width / 2;
        float y = position.y + 50f;

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x - 1, y - 1, width + 2, height + 2);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, width * getHpPercent(), height);
    }

    public void gotDamage(int damage) {
        HP -= damage;
        if (HP <= 0 && !isDead) {
            die();
        } else {
            setState(EnemyState.DAMAGED);
        }

        // Play hit sound for enemy when damaged
        SoundManager.getInstance().playHit();
    }

    public Rectangle getBounds() {
        return bounds;
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

    public boolean isDying() { return Dying; }
    public void dispose() { Dying = true; }
    public boolean isDead() { return isDead; }

    // === Getters ===
    public Vector2 getPosition() { return position; }
    public int getHP() { return HP; }
    public int getATK() { return ATK; }
    public float getHpPercent() { return (float) HP / maxHP; }

    // === Setter ===
    public void setPosition(float x, float y) { this.position.set(x, y); }
}
