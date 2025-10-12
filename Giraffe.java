package ISNE12.project;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Giraffe extends Character {

    private Texture heroSheet;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> animDown, animUp, animSide;
    private TextureRegion currentFrame;
    private boolean facingRight = true;

    public Giraffe(float startX, float startY) {
        super(100, 15, 3, startX, startY); // Lower HP, Higher ATK, Lower DEF

        // Use giraffe sprite sheet
        heroSheet = new Texture("giraffe.png"); // 👈 PUT YOUR GIRAFFE PNG FILE HERE
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

    @Override
    public void applyPassive() {
        // Mage passive: Increase attack every 5 seconds
        if (stateTime % 5 < 0.016f) { // roughly every 5 seconds
            setAttack(getAttack() + 1);
        }
    }

    @Override
    public void useSpecialAbility() {
        // Mage special: Temporary massive attack boost
        setAttack(getAttack() + 10);
        System.out.println("Mage used Magic Burst! ATK boosted!");
    }
}
