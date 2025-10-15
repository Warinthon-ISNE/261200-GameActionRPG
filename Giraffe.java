package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Giraffe extends Character {

    // === Animation ===
    private Texture heroSheet;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> animDown, animLeft, animRight, animUp;
    private TextureRegion currentFrame;
    private float stateTime = 0f;

    // === Passive tracking ===
    private int lastKillThreshold = 0;
    private final float maxAttackSpeed = 10.0f;

    public Giraffe(float startX, float startY) {
        super(100, 10, 3f, startX, startY); // HP, ATK, SPD, pos

        // Split sprite sheet
        heroSheet = new Texture("giraffe1.png");
        frames = TextureRegion.split(heroSheet, heroSheet.getWidth() / 3, heroSheet.getHeight() / 4);

        // Animations by direction
        animDown = new Animation<>(0.2f, frames[0]);
        animRight = new Animation<>(0.2f, frames[1]);
        animLeft = new Animation<>(0.2f, frames[2]);
        animUp = new Animation<>(0.2f, frames[3]);

        currentFrame = frames[0][1]; // idle
    }

    @Override
    public void updateAnimation(float delta, boolean moving, String direction, boolean facingRight) {
        stateTime += delta;

        // Choose frame by direction
        if (moving) {
            switch (direction) {
                case "up": currentFrame = animUp.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = animDown.getKeyFrame(stateTime, true); break;
                case "side":
                    currentFrame = facingRight
                        ? animRight.getKeyFrame(stateTime, true)
                        : animLeft.getKeyFrame(stateTime, true);
                    break;
            }
        } else {
            // Idle frame
            switch (direction) {
                case "up": currentFrame = frames[3][1]; break;
                case "down": currentFrame = frames[0][1]; break;
                case "side": currentFrame = facingRight ? frames[1][1] : frames[2][1]; break;
            }
        }
    }

    @Override
    public TextureRegion getCurrentFrame() { return currentFrame; }

    // Passive: +1.0 attack speed every 5 kills, capped at 10
    @Override
    public void applyPassive() {
        int threshold = (kills / 5) * 5;
        if (kills > 0 && kills % 5 == 0 && threshold != lastKillThreshold) {
            lastKillThreshold = threshold;
            increaseAttackSpeed();
        }
    }

    private void increaseAttackSpeed() {
        if (attackSpeed < maxAttackSpeed) {
            attackSpeed += 1f;
        }
    }

    @Override
    public String getBulletTexturePath() { return "magic_bullet.png"; }

    public void dispose() { heroSheet.dispose(); }
}
