package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Basic melee zombie enemy.
 * Slowly walks toward the player and attacks on contact.
 */
public class Zombie extends Enemy {

    private Texture spriteSheet;
    private TextureRegion[][] frames;

    public Zombie(float x, float y, Character target) {
        // (x, y, HP, ATK, speed, target)
        super(x, y, 60, 10, 0.5f, target);

        // Load sprite sheet (make sure the file exists in assets/)
        spriteSheet = new Texture(Gdx.files.internal("zombie.png"));

        // Split sheet into 4 rows (down, side, up, death) and 3 columns per row
        frames = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / 3,
            spriteSheet.getHeight() / 4);

        // Animations
        walkDown  = new Animation<>(0.2f, frames[0]);
        walkLeft  = new Animation<>(0.2f, frames[1]);
        walkRight = new Animation<>(0.2f, flipFrames(frames[1]));
        walkUp    = new Animation<>(0.2f, frames[2]);
        deathAnimation = new Animation<>(0.3f, frames[3]);

        // Start facing down
        currentFrame = frames[0][1];
    }

    /** Flips frames horizontally for right-facing animation. */
    private TextureRegion[] flipFrames(TextureRegion[] row) {
        TextureRegion[] flipped = new TextureRegion[row.length];
        for (int i = 0; i < row.length; i++) {
            flipped[i] = new TextureRegion(row[i]);
            flipped[i].flip(true, false);
        }
        return flipped;
    }

    @Override
    public void update(float delta) {
        super.update(delta); // handles AI + animation updates
    }

    @Override
    protected void die() {
        super.die();
        System.out.println("💀 Zombie died at " + position);
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
            spriteSheet = null;
        }
    }
}
