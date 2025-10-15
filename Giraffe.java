package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Giraffe — gains attack speed every 5 kills, capped at 3.0f.
 * Uses giraffe1.png with 4-direction animation:
 * Row 0 = down, Row 1 = right, Row 2 = left, Row 3 = up.
 */
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
        // maxHp, attack, attackSpeed, startX, startY
        super(100, 10, 3f, startX, startY);

        heroSheet = new Texture("giraffe1.png");
        frames = TextureRegion.split(heroSheet, heroSheet.getWidth() / 3, heroSheet.getHeight() / 4);

        // Row order: 0=Down, 1=Right, 2=Left, 3=Up
        animDown = new Animation<>(0.2f, frames[0]);
        animRight = new Animation<>(0.2f, frames[1]);
        animLeft = new Animation<>(0.2f, frames[2]);
        animUp = new Animation<>(0.2f, frames[3]);

        currentFrame = frames[0][1]; // idle facing down
    }

    @Override
    public void updateAnimation(float delta, boolean moving, String direction, boolean facingRight) {
        stateTime += delta;

        if (moving) {
            switch (direction) {
                case "up":
                    currentFrame = animUp.getKeyFrame(stateTime, true);
                    break;
                case "down":
                    currentFrame = animDown.getKeyFrame(stateTime, true);
                    break;
                case "side":
                    // ✅ Pick correct animation row (1=right, 2=left)
                    currentFrame = facingRight
                        ? animRight.getKeyFrame(stateTime, true)
                        : animLeft.getKeyFrame(stateTime, true);
                    break;
            }
        } else {
            // Idle frame for each direction
            switch (direction) {
                case "up":
                    currentFrame = frames[3][1];
                    break;
                case "down":
                    currentFrame = frames[0][1];
                    break;
                case "side":
                    currentFrame = facingRight ? frames[1][1] : frames[2][1];
                    break;
            }
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Passive — every 5 kills, Giraffe gains +0.3 attack speed, capped at 3.0f.
     */
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
            if (attackSpeed > maxAttackSpeed) attackSpeed = maxAttackSpeed;
            System.out.println("Giraffe's attack speed increased! Current: " + attackSpeed + "x");
        }
    }

    @Override
    public String getBulletTexturePath() {
        return "magic_bullet.png";
    }

    public void dispose() {
        heroSheet.dispose();
    }
}
