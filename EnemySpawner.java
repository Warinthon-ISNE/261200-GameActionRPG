import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.lang.Character;

/*import all enemy type here!!*/

public class EnemySpawner {
    private Array<Enemy> enemies;
    private float worldWidth, worldHeight;
    public Character target;

    private static final float SAVEZONE_RANGE = 12f;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f;

    private int currentWave = 1;
    private float timeSinceLastWave = 0f;
    private float nextWaveDelay = MathUtils.random(40f, 60f);
    private float warningTime = 5f;
    private boolean showWaveWarning = false;

    // --- for surprising wave condision (after wave 4) ---
    boolean EmergencyCall = false;
    boolean surpriseWaveTriggered = false;
    private int waveFourEnemies; //all spawned enemy in wave 4
    private int killStack = 0; //Character kill stack in wave 4

    public EnemySpawner(Array<Enemy> enemies, Character target, float worldWidth, float worldHeight) {
        this.enemies = enemies;
        this.target = target;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(float delta) {
        spawnTimer += delta;
        timeSinceLastWave += delta;

        // Regular spawn
        if (spawnTimer >= spawnInterval) {
            spawnEnemy();
            spawnTimer = 0f;
        }

        // Wave trigger
        if (timeSinceLastWave >= nextWaveDelay) {
            showWaveWarning = true;
            spawnWave();
            currentWave++;
            timeSinceLastWave = 0f;
            nextWaveDelay = MathUtils.random(35f, 50f);
        }

        if (showWaveWarning && timeSinceLastWave >= nextWaveDelay - warningTime) {
            System.out.println("Monster Wave coming! Prepare yourself.");
        }

        // Emergency support spawn
        spawnEmergency();

        // Update all enemies
        for (Enemy e : enemies) {
            e.update(delta);
        }

        // Clean dead enemies
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDying()) {
                if(currentWave == 3){
                    killStack++;  // Increment when an enemy dies
                }
                enemies.removeIndex(i);
            }
        }

        // Check for Surprise Wave condition in wave 5
        if (currentWave == 4 && !surpriseWaveTriggered && killStack >= waveFourEnemies * 0.7 && target.getHpPercent() >= 0.4f && !EmergencyCall) {
            triggerSurpriseWave();
        }
    }

    private void spawnEnemy() { //not in monster wave
        int numEnemies = MathUtils.random(1, 3);
        for (int i = 0; i < numEnemies; i++) {
            Enemy e = generateRandomEnemy(currentWave);
            setSafeSpawnPosition(e);
            enemies.add(e);
        }
    }

    private void spawnWave() {
        int numEnemies = MathUtils.random(10, 20);  // spawn 10-20 enemies per wave

        // Add enemies based on wave type
        for (int i = 0; i < numEnemies; i++) {
            Enemy e = generateRandomEnemy(currentWave);
            setSafeSpawnPosition(e);
            if(currentWave == 4){
                waveFourEnemies++;
            }
            enemies.add(e);
        }

        System.out.println("Wave " + currentWave + " spawned: " + numEnemies + " enemies.");
        showWaveWarning = false;  // Hide warning after spawning wave
    }

    private Enemy generateRandomEnemy(int wave) {
        Enemy newEnemy = null;
        int type;

        if (wave == 1) {
            newEnemy = new Zombie(0, 0, 50, 10, 5, 10, target);

        } else if (wave == 2) {
            // Wave 3: Introduce Red Gasby
            type = MathUtils.random(0, 5);
            if (type == 0) newEnemy = new GasbySamSi(0, 0, "red", target);
            else if (type == 1 || type == 2) newEnemy = new GasbySamSi(0, 0, "blue", target);
            else newEnemy = new Zombie(0, 0, 60, 15, 10, 12, target);

        } else if (wave == 3) {
            // Wave 4: A mix of everything
            type = MathUtils.random(0, 6);
            if (type == 1 || type == 4) newEnemy = new GasbySamSi(0, 0, "red", target);
            else if (type == 1 || type == 2 || type == 3) newEnemy = new GasbySamSi(0, 0, "blue", target);
            else newEnemy = new Zombie(0, 0, 60, 15, 10, 20, target);
        } else if (wave == 4) {
            // Wave 4: A mix of everything
            type = MathUtils.random(0, 10);
            if (type == 1 || type == 5) newEnemy = new GasbySamSi(0, 0, "red", target);
            else if (type <= 5) newEnemy = new GasbySamSi(0, 0, "blue", target);
            else newEnemy = new Zombie(0, 0, 60, 15, 10, 20, target);

        } else {
            // Wave 5: A mix of everything and some surprises
            type = MathUtils.random(0, 10);
            if (type <= 4) newEnemy = new GasbySamSi(0, 0, "red", target);
            else if (type <= 5) newEnemy = new GasbySamSi(0, 0, "blue", target);
            else if (type >= 3 && type <= 10) newEnemy = new Zombie(0, 0, 100, 20, 12, 20, target);
        }

        // Random spawn location (spawn away from the player)
        float x = MathUtils.random(SAVEZONE_RANGE, worldWidth - SAVEZONE_RANGE);
        float y = MathUtils.random(SAVEZONE_RANGE, worldHeight - SAVEZONE_RANGE);
        newEnemy.setPosition(x, y);

        return newEnemy;
    }

    private void triggerSurpriseWave() {
        System.out.println("Surprise wave coming! Get ready!");
        int numEnemies = MathUtils.random(15, 25);  // Spawn more enemies in surprise wave

        for (int i = 0; i < numEnemies; i++) {
            Enemy e = generateRandomEnemy(5);  // Wave 5 enemies
            enemies.add(e);
        }
        surpriseWaveTriggered = true;  // Mark that the surprise wave was triggered
    }

    private void spawnEmergency() {
        if (target.getHpPercent() < 0.3f && spawnTimer >= spawnInterval) {
            Enemy redGasby = new GasbySamSi(0, 0, "red", target);
            setSafeSpawnPosition(redGasby);
            enemies.add(redGasby);
            spawnTimer = 0f;
            EmergencyCall = true;
        }
    }

    private void setSafeSpawnPosition(Enemy enemy) {
        float x, y;
        do {
            x = MathUtils.random(0f, worldWidth);
            y = MathUtils.random(0f, worldHeight);
        } while (Vector2.dst(x, y, target.getPosition().x, target.getPosition().y) < SAVEZONE_RANGE);
        enemy.setPosition(x, y);
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
