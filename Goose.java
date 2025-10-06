package com.ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goose extends Character {

    private Texture heroSheet;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> animDown, animUp, animSide;
    private TextureRegion currentFrame;
    private boolean facingRight = true;

    public Goose(float startX, float startY) {
        super(150, 8, 5, startX, startY);

        heroSheet = new Texture("S__28893189-removebg-preview.png");
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

        // Choose animation
        if (moving) {
            switch (direction) {
                case "up": currentFrame = animUp.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = animDown.getKeyFrame(stateTime, true); break;
                case "side": currentFrame = animSide.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (direction) {
                case "up": currentFrame = frames[3][1]; break;
                case "down": currentFrame = frames[0][1]; break;
                case "side": currentFrame = frames[1][1]; break;
            }
        }

        // Normally: facingRight = true → face right
        // Now: facingRight = true → face LEFT
        if (facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);  // flip to face LEFT when moving RIGHT
        } else if (!facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);  // flip to face RIGHT when moving LEFT
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public void applyPassive() {
        if (hp < maxHp) heal(1);
    }

    @Override
    public void useSpecialAbility() {
        defense += 5;
        System.out.println("HeroA used defense buff!");
    }
}
