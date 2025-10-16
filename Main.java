package com.ISNE12.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Main entry point for the game.
 * Responsible for initializing the SpriteBatch and managing screens.
 */
public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        setScreen(new FirstScreen(this)); // start at splash screen

    }

    @Override
    public void render() {
        // Delegates rendering to the active screen
        super.render();
    }

    @Override
    public void dispose() {
        // Proper cleanup
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
