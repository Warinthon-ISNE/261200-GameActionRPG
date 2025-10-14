package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Giraffe — gains attack speed every 5 kills, capped at max.
 * Uses giraffe_bullet.png.
 */
public class Giraffe extends Character {

    private Texture heroSheet;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> animDown, animUp, animSide;
    private TextureRegion currentFrame;
    private boolean facingRight = true;
    private float stateTime = 0f;

    // Passive tracking
    private int lastKillThreshold = 0;
    private final float maxAttackSpeed = 3.0f;

    public Giraffe(float startX, float startY) {
        super(200, 10, 8, startX, startY);
        this.attackSpeed = 1.0f;

        heroSheet = new Texture("giraffe.png");
        frames = TextureRegion.split(heroSheet, heroSheet.getWidth() / 3, heroSheet.getHeight() / 4);

        animDown = new Animation<>(0.2f, frames[0]);
        animSide = new Animation<>(0.2f, frames[1]);
        animUp   = new Animation<>(0.2f, frames[3]);

        currentFrame = frames[0][1];
    }

    @Override
    public void updateAnimation(float delta, boolean moving, String direction, boolean facingRight) {
        this.stateTime += delta;
        this.facingRight = facingRight;

        if (moving) {
            switch (direction) {
                case "up":   currentFrame = animUp.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = animDown.getKeyFrame(stateTime, true); break;
                case "side": currentFrame = animSide.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (direction) {
                case "up":   currentFrame = frames[3][1]; break;
                case "down": currentFrame = frames[0][1]; break;
                case "side": currentFrame = frames[1][1]; break;
            }
        }

        if (facingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        else if (!facingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);
    }

    @Override
    public TextureRegion getCurrentFrame() { return currentFrame; }

    @Override
    public void applyPassive() {
        int currentThreshold = kills / 5;
        if (currentThreshold > lastKillThreshold) {
            lastKillThreshold = currentThreshold;
            increaseAttackSpeed();
        }
    }

    private void increaseAttackSpeed() {
        if (attackSpeed < maxAttackSpeed) {
            attackSpeed += 0.3f;
            if (attackSpeed > maxAttackSpeed) attackSpeed = maxAttackSpeed;
            System.out.println("Giraffe's attack speed increased! Current: " + attackSpeed + "x");
        }
    }

    @Override
    public void useSpecialAbility() {
        attack += 5;
        System.out.println("Giraffe used Power Stomp!");
    }

    @Override
    public String getBulletTexturePath() {
        return "magic_bullet.png";
    }

    public void dispose() { heroSheet.dispose(); }
}
