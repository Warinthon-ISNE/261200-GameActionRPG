package com.ISNE12.project;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Handles all enemy spawning logic — normal spawns and wave progression.
 * (Now limited to 3 waves total, with capped spawns on final wave.)
 */
public class EnemySpawner {

    private final Array<Enemy> enemies;
    private final Character target;
    private final float worldWidth;
    private final float worldHeight;

    // === Spawn settings ===
    private static final float SAFEZONE_RANGE = 12f;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f;

    // === Wave system ===
    private int currentWave = 1;
    private float waveTimer = 0f;
    private float nextWaveDelay = MathUtils.random(40f, 60f);
    private static final float WARNING_TIME = 5f;
    private boolean showWaveWarning = false;

    // === Final wave control ===
    private boolean spawningStopped = false;
    private int wave3SpawnedCount = 0;
    private final int wave3MaxEnemies = 25; // limit total spawns in wave 3

    // Optional projectiles list for ranged enemies
    private final Array<Projectile> projectiles;

    public EnemySpawner(Array<Enemy> enemies, Character target,
                        float worldWidth, float worldHeight,
                        Array<Projectile> projectiles) {
        this.enemies = enemies;
        this.target = target;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.projectiles = projectiles;
    }

    public void update(float delta) {
        if (spawningStopped) return; // ✅ stop completely after final wave is cleared

        spawnTimer += delta;
        waveTimer += delta;

        // --- Regular spawns (only before final wave cap) ---
        if (spawnTimer >= spawnInterval) {
            if (currentWave < 3) {
                spawnEnemy();
            } else if (currentWave == 3 && wave3SpawnedCount < wave3MaxEnemies) {
                spawnEnemy();
                wave3SpawnedCount++;
            }
            spawnTimer = 0f;
        }

        // --- Wave trigger ---
        if (waveTimer >= nextWaveDelay && currentWave < 3) {
            showWaveWarning = true;
            spawnWave();
            waveTimer = 0f;
            nextWaveDelay = MathUtils.random(35f, 50f);
            currentWave++;
        }

        // --- Warning message ---
        if (showWaveWarning && waveTimer >= nextWaveDelay - WARNING_TIME) {
            System.out.println("⚠️ Monster Wave incoming! Prepare yourself!");
        }

        // --- Update all enemies ---
        for (Enemy e : enemies) e.update(delta);

        // --- Remove dead enemies ---
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDying()) {
                enemies.removeIndex(i);
            }
        }

        // --- Stop all spawning after final wave is finished ---
        if (currentWave == 3 && wave3SpawnedCount >= wave3MaxEnemies && enemies.size == 0) {
            spawningStopped = true;
            System.out.println("✅ Wave 3 cleared — all enemies defeated!");
        }
    }

    // === Regular Spawn ===
    private void spawnEnemy() {
        int count = MathUtils.random(1, 3);
        for (int i = 0; i < count; i++) {
            Enemy e = createEnemyForWave(currentWave);
            setSafeSpawnPosition(e);
            enemies.add(e);
        }
    }

    // === Wave Spawn ===
    private void spawnWave() {
        int count = MathUtils.random(10, 20);
        for (int i = 0; i < count; i++) {
            Enemy e = createEnemyForWave(currentWave);
            setSafeSpawnPosition(e);
            enemies.add(e);
        }
        System.out.println("🌊 Wave " + currentWave + " spawned with " + count + " enemies!");
        showWaveWarning = false;
    }

    // === Enemy Factory ===
    private Enemy createEnemyForWave(int wave) {
        int roll = MathUtils.random(0, 10);
        Enemy enemy;

        switch (wave) {
            case 1:
                enemy = new Zombie(0, 0, target);
                break;

            case 2:
                if (roll < 3) enemy = new GasbySamSi(0, 0, "blue", target);
                else enemy = new Zombie(0, 0, target);
                break;

            case 3:
                if (roll < 3) enemy = new GasbySamSi(0, 0, "red", target);
                else if (roll < 6) enemy = new GasbySamSi(0, 0, "blue", target);
                else enemy = new Zombie(0, 0, target);
                break;

            default:
                enemy = new Zombie(0, 0, target);
                break;
        }
        return enemy;
    }

    // === Utility ===
    private void setSafeSpawnPosition(Enemy enemy) {
        float x, y;
        do {
            x = MathUtils.random(0f, worldWidth);
            y = MathUtils.random(0f, worldHeight);
        } while (Vector2.dst(x, y, target.getPosition().x, target.getPosition().y) < SAFEZONE_RANGE);
        enemy.setPosition(x, y);
    }

    // === Getters ===
    public float getWorldWidth() { return worldWidth; }
    public float getWorldHeight() { return worldHeight; }
    public int getCurrentWave() { return currentWave; }

    // === Victory helper ===
    public boolean isFinalWaveCleared() {
        return currentWave == 3 && wave3SpawnedCount >= wave3MaxEnemies && enemies.size == 0 && spawningStopped;
    }
}
