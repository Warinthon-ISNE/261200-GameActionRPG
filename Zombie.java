package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Zombie extends Enemy {
    private static final float ATTACK_RANGE = 2f;
    private final Texture zombieSideSheet;
    private final Texture zombieUpSheet;
    private final Texture zombieDownSheet;
    //zombie class base stat

    public Zombie(float x, float y, int hp, int atk, float speed, Character target) {
        super(x, y, hp, atk, speed, target);
        this.maxHP = hp;

        // Load the sprite sheets
        zombieSideSheet = new Texture(Gdx.files.internal("Side_Zombie.png"));
        zombieUpSheet = new Texture(Gdx.files.internal("Back_Zombie.png"));
        zombieDownSheet = new Texture(Gdx.files.internal("Front_Zombie.png"));

        // Split sprite sheets into 2D arrays of TextureRegions
        TextureRegion[][] walkFramesSide = TextureRegion.split(zombieSideSheet, 160, 140);
        TextureRegion[][] walkFramesUp = TextureRegion.split(zombieUpSheet, 160, 140);
        TextureRegion[][] walkFramesDown = TextureRegion.split(zombieDownSheet, 160, 140);

        // Define number of frames per animation
        final int WALK_FRAMES = 4;
        final int ATTACK_FRAMES = 4;
        final int DAMAGED_FRAMES = 4;
        final int DEATH_FRAMES = 4;

        // Create animation arrays from sprite sheet rows
        Array<TextureRegion> walkRightArray = new Array<>(walkFramesSide[0]);
        Array<TextureRegion> walkLeftArray = new Array<>();
        for (TextureRegion frame : walkRightArray) {
            TextureRegion flippedFrame = new TextureRegion(frame);
            flippedFrame.flip(true, false);
            walkLeftArray.add(flippedFrame);
        }

        Array<TextureRegion> walkUpArray = new Array<>(walkFramesUp[0]);
        Array<TextureRegion> walkDownArray = new Array<>(walkFramesDown[0]);

        Array<TextureRegion> attackArray = new Array<>(walkFramesSide[1]);
        Array<TextureRegion> damagedArray = new Array<>(walkFramesSide[2]);
        Array<TextureRegion> deathArray = new Array<>(walkFramesSide[3]);

        // Initialize animations with correct frames and frame duration
        walkRight = new Animation<>(0.15f, walkRightArray, Animation.PlayMode.LOOP);
        walkLeft = new Animation<>(0.15f, walkLeftArray, Animation.PlayMode.LOOP);
        walkUp = new Animation<>(0.15f, walkUpArray, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(0.15f, walkDownArray, Animation.PlayMode.LOOP);

        attackAnimation = new Animation<>(0.1f, attackArray, Animation.PlayMode.NORMAL);
        damagedAnimation = new Animation<>(0.1f, damagedArray, Animation.PlayMode.NORMAL);
        deathAnimation = new Animation<>(0.1f, deathArray, Animation.PlayMode.NORMAL);

        // Set initial frame
        currentFrame = walkDown.getKeyFrame(0);
    }

    public void attack(Character target) {
        target.takeDamage(this.getATK());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= ATTACK_RANGE) { //attack range 2*2
            setState(EnemyState.ATTACK);
        } else {
            setState(EnemyState.WALK); //if doesn't reach yet, walk
        }
    }

    @Override
    protected void updateWalkAnimation() {
        switch (facing) {
            case UP:
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case DOWN:
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                break;
            case LEFT:
                currentFrame = walkLeft.getKeyFrame(stateTime, true);
                break;
            case RIGHT:
                currentFrame = walkRight.getKeyFrame(stateTime, true);
                break;
        }
    }

    @Override
    protected void updateAttackAnimation() {
        if (attackAnimation == null) return;
        currentFrame = attackAnimation.getKeyFrame(stateTime, false);
        if (attackAnimation.isAnimationFinished(stateTime)) {
            setState(EnemyState.WALK);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        zombieSideSheet.dispose();
        zombieUpSheet.dispose();
        zombieDownSheet.dispose();
    }
}
