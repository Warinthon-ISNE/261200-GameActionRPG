package com.ISNE12.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// SoundManager — handles all game audio in one centralized class.
public class SoundManager {

    private static SoundManager instance;

    // === SOUND EFFECTS ===
    private Sound hitSound;

    // === MUSIC ===
    private Music backgroundMusic;

    // === SETTINGS ===
    private float soundVolume = 0.1f;
    private float musicVolume = 0.1f;
    private boolean muted = false;

    private SoundManager() {
        try {
            // Load sound effects
            hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));

            // Load background music
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(musicVolume);
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Error loading sound files: " + e.getMessage());
        }
    }

    /** Get the single instance */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // === PLAY METHODS ===
    public void playHit() {
        if (!muted && hitSound != null)
            hitSound.play(soundVolume);
    }

    public void playMusic() {
        if (!muted && backgroundMusic != null && !backgroundMusic.isPlaying())
            backgroundMusic.play();
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying())
            backgroundMusic.stop();
    }

    // === SETTINGS ===
    public void setMuted(boolean mute) {
        this.muted = mute;
        if (mute) stopMusic();
        else playMusic();
    }

    public void setSoundVolume(float volume) {
        soundVolume = Math.max(0f, Math.min(0.1f, volume));
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(0.1f, volume));
        if (backgroundMusic != null)
            backgroundMusic.setVolume(musicVolume);
    }

    // === CLEANUP ===
    public void dispose() {
        if (hitSound != null) hitSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}
