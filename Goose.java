package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goose extends Character {

    // === Animation ===
    private Texture heroSheet;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> animDown, animUp, animSide;
    private TextureRegion currentFrame;
    private boolean facingRight = true;
    private float stateTime = 0f;

    // === Passive tracking ===
    private int lastKillThreshold = 0;
    private int extraShots = 0;
    private final int maxExtraShots = 3; // +3 max → 4 total bullets
    private boolean nextDiagonalRight = true; // alternates every 5 kills

    public Goose(float startX, float startY) {
        // (maxHp, attack, attackSpeed, startX, startY)
        super(200, 20, 1.5f, startX, startY);

        heroSheet = new Texture("goose.png");
        frames = TextureRegion.split(heroSheet, heroSheet.getWidth() / 3, heroSheet.getHeight() / 4);

        animDown = new Animation<>(0.15f, frames[0]);
        animSide = new Animation<>(0.15f, frames[1]);
        animUp   = new Animation<>(0.15f, frames[3]);

        currentFrame = frames[0][1];
    }

    @Override
    public void updateAnimation(float delta, boolean moving, String direction, boolean facingRight) {
        this.stateTime += delta;
        this.facingRight = facingRight;

        // --- Choose animation based on direction ---
        if (moving) {
            switch (direction) {
                case "up":
                    currentFrame = animUp.getKeyFrame(stateTime, true);
                    break;
                case "down":
                    currentFrame = animDown.getKeyFrame(stateTime, true);
                    break;
                case "side":
                    currentFrame = animSide.getKeyFrame(stateTime, true);
                    break;
            }
        } else {
            // idle frame for each direction
            switch (direction) {
                case "up":
                    currentFrame = frames[3][1];
                    break;
                case "down":
                    currentFrame = frames[0][1];
                    break;
                case "side":
                    currentFrame = frames[1][1];
                    break;
            }
        }

        // --- Flip horizontally like old version ---
        if (facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    // Passive — every 5 kills, Goose gains +1 bullet (max 4 total). Alternates the diagonal shooting direction each time.
    @Override
    public void applyPassive() {
        int threshold = (kills / 5) * 5;
        if (kills > 0 && kills % 5 == 0 && threshold != lastKillThreshold) {
            lastKillThreshold = threshold;
            increaseExtraShots();
        }
    }

    private void increaseExtraShots() {
        if (extraShots < maxExtraShots) {
            extraShots++;
            nextDiagonalRight = !nextDiagonalRight; // alternate pattern
            System.out.println("Goose multishot +1 (" + (extraShots + 1) +
                " bullets) → next side: " + (nextDiagonalRight ? "right" : "left"));
        }
    }

    // === Getters ===
    public int getExtraShots() {
        return extraShots;
    }

    // Whether the next diagonal spread should go right or left
    public boolean isDiagonalRight() {
        return nextDiagonalRight;
    }

    @Override
    public String getBulletTexturePath() {
        return "goose_bullet.png";
    }

    public void dispose() {
        heroSheet.dispose();
    }
}
