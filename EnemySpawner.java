import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.lang.Character;
/*import class zombie, Enemy, GasbySamSi*/

public class EnemySpawner {
    private Array<Enemy> enemies;
    private float worldWidth, worldHeight;
    public Character target;

    // Spawn control
    private static final float SAVEZONE_RANGE = 15f;  // limits enemy spawn area (far from Character 15*15)
    private float spawnTimer = 0f;
    private float spawnInterval = 1.5f;

    // Monster wave
    private int currentWave = 1;
    private float timeSinceLastWave = 0f;
    private float nextWaveDelay = MathUtils.random(40f, 60f); // random delay for next wave
    private float warningTime = 5f;  // Show "Monster Wave Coming in 5 seconds" before the wave starts
    private boolean showWaveWarning = false;

    public EnemySpawner(Array<Enemy> enemies, Character target, float worldWidth, float worldHeight) {
        this.enemies = enemies;
        this.target = target;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(float delta) {
        spawnTimer += delta;
        timeSinceLastWave += delta;

            // Trigger wave after time elapsed
            if (timeSinceLastWave >= nextWaveDelay) {
                showWaveWarning = true;  // Show wave warning
                spawnWave();
                currentWave++;
                timeSinceLastWave = 0f;
                nextWaveDelay = MathUtils.random(35f, 50f);  // Randomize next wave delay
            }

            // Show "Monster Wave coming" message
            if (showWaveWarning) {
                if (timeSinceLastWave >= nextWaveDelay - warningTime) {
                    System.out.println("Monster Wave coming! Prepare yourself.");
                }
            }
            // Regular enemy spawn (1-3 enemies every time)
            if (spawnTimer >= spawnInterval) {
                spawnEnemy();
                spawnTimer = 0f;  // Reset timer after spawning enemies
            }
            // Update all enemies
            for (Enemy e : enemies) {
                e.update(delta);
            }
    }

    // Generate random enemies for the regular spawn
    private void spawnEnemy() {
        int numEnemies = MathUtils.random(1, 3); // spawn 1-3 enemies each time
        for (int i = 0; i < numEnemies; i++) {
            Enemy e = generateRandomEnemy(0);  // 0 represents normal case
            enemies.add(e);
        }
    }

    //wave starts
    private void spawnWave() {
        // Number of enemies to spawn based on wave
        int numEnemies = MathUtils.random(10, 20);  // spawn 10-20 enemies per wave

        // Add enemies based on wave type
        for (int i = 0; i < numEnemies; i++) {
            Enemy e = generateRandomEnemy(currentWave);
            enemies.add(e);
        }

        System.out.println("Wave " + currentWave + " spawned: " + numEnemies + " enemies.");
        showWaveWarning = false;  // Hide warning after spawning wave
    }

    private Enemy generateRandomEnemy(int wave) {
        Enemy newEnemy = null;

        // Wave spawning logic
        if (wave == 1) {
            newEnemy = new Zombie(0, 0, 50, 10, 20, 20, target);  // Zombie only
        } else if (wave == 2) {
            newEnemy = MathUtils.random(0, 1) == 0 ? new Zombie(0, 0, 50, 10, 20, 20, target) : new GasbySamSi(0, 0, "blue", target);
        } else if (wave == 3) {
            newEnemy = MathUtils.random(0, 1) == 0 ? new Zombie(0, 0, 50, 10, 20, 30, target) : new GasbySamSi(0, 0, "blue", target) :
            new GasbySamSi(0, 0, "red", target);
        } else if (wave == 4) {
            // Wave 4: A mix of everything
            newEnemy = MathUtils.random(0, 1) == 0 ? new Zombie(0, 0, 50, 10, 20, 20, target) : new GasbySamSi(0, 0, "blue", target)
                : new GasbySamSi(0, 0, "red", target) : new GasbySamSi(0, 0, "purple", target);
        } else {
            // Wave 5: A mix of everything and some surprises
            newEnemy = MathUtils.random(0, 1) == 0 ? new Zombie(0, 0, 50, 10, 20, 30, target) : new GasbySamSi(0, 0, "blue", target) : new GasbySamSi(0, 0, "red", target) : new GasbySamSi(0, 0, "purple", target);
        }

        // Random spawn location (spawn away from the player)
        float x = MathUtils.random(SAVEZONE_RANGE, worldWidth - SAVEZONE_RANGE);
        float y = MathUtils.random(SAVEZONE_RANGE, worldHeight - SAVEZONE_RANGE);
        newEnemy.setPosition(x, y);

        return newEnemy;
    }

    // Helper spawn: Red Gasbie when Player HP <= 30%
    private void spawnEmergency() {
        if (target.getHpPercent() < 0.3f && spawnTimer >= spawnInterval) {
            spawnEmergency();
            spawnTimer = 0f;
        }
        // Add Red Gasbie to help the player
        Enemy redGasby = new GasbySamSi(0, 0, "red", target);
        enemies.add(redGasby);
    }

    // Getter methods
    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}
