import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RedGasby extends GasbySamSi {
    private Array<Projectile> projectiles;
    private Array<Enemy> enemies;

    // DASH attack
    private float dashSpeed = 20f;
    private float dashCooldown = 0f;
    private static final float DASH_CD_TIME = 1.2f;

    // Poltergeist range attack
    private static final float SHOOT_RANGE = 7f;
    private static final float DASH_RANGE = 3f;

    // Animation frames and states
    private final Texture redGasbySide;
    private final Texture redGasbyUp;
    private final Texture redGasbyDown;

    // Declare animations for new states
    private Animation<TextureRegion> prepareDashAnimation;
    private Animation<TextureRegion> dashAttackAnimation;
    private Animation<TextureRegion> poltergeistAnimation;

    // Current state, using the custom enum
    private GasbyState gasbyState = GasbyState.WALK;

    public RedGasby(float x, float y, Character target, Array<Projectile> projectiles, Array<Enemy> enemies) {
        super(x, y, "red", target);
        this.projectiles = projectiles;
        this.enemies = enemies;

        // Load sprite sheets
        redGasbySide = new Texture(Gdx.files.internal("Side_RedGasby.png"));
        redGasbyUp = new Texture(Gdx.files.internal("Back_RedGasby.png"));
        redGasbyDown = new Texture(Gdx.files.internal("Front_RedGasby.png"));

        // Split sprite sheets into 2D arrays (5 rows, 6 columns for side)
        TextureRegion[][] framesSide = TextureRegion.split(redGasbySide, 104, 129);
        TextureRegion[][] framesUp = TextureRegion.split(redGasbyUp, 104, 129);
        TextureRegion[][] framesDown = TextureRegion.split(redGasbyDown, 104, 129);

        // Extract frames for each animation and create Animation objects
        walkRight = new Animation<>(0.15f, framesSide[0]);
        walkLeft = new Animation<>(0.15f, createFlippedFrames(framesSide[0]));
        walkUp = new Animation<>(0.15f, framesUp[0]);
        walkDown = new Animation<>(0.15f, framesDown[0]);

        prepareDashAnimation = new Animation<>(0.1f, framesSide[1]);
        dashAttackAnimation = new Animation<>(0.1f, framesSide[2]);
        poltergeistAnimation = new Animation<>(0.1f, framesSide[3]);
        damagedAnimation = new Animation<>(0.1f, framesSide[4]);

        // No death animation, it just disappears from the array.
        deathAnimation = new Animation<>(0f, new TextureRegion());

        currentFrame = walkDown.getKeyFrame(0);
    }

    // Helper method to create flipped frames for left direction
    private Array<TextureRegion> createFlippedFrames(TextureRegion[] frames) {
        Array<TextureRegion> flippedArray = new Array<>();
        for (TextureRegion frame : frames) {
            TextureRegion flippedFrame = new TextureRegion(frame);
            flippedFrame.flip(true, false);
            flippedArray.add(flippedFrame);
        }
        return flippedArray;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        dashCooldown -= delta;

        // Condition check for attack type
        int nearbyGasby = countNearbyGasby();
        float distanceToPlayer = position.dst(target.getPosition());

        // Update state based on behavior logic
        if (HP <= 0) {
            die();
            return;
        } else if (gasbyState == GasbyState.DAMAGED) {
            updateDamagedAnimation();
            return;
        } else if (nearbyGasby >= 4 && distanceToPlayer <= DASH_RANGE && dashCooldown <= 0) {
            if (gasbyState != GasbyState.PREPARE_DASH) {
                gasbyState = GasbyState.PREPARE_DASH;
                stateTime = 0;
            }
        } else if (distanceToPlayer <= SHOOT_RANGE) {
            if (gasbyState != GasbyState.POLTERGEIST) {
                gasbyState = GasbyState.POLTERGEIST;
                stateTime = 0;
            }
        } else {
            gasbyState = GasbyState.WALK;
        }

        // Handle logic and animation based on the current state
        switch (gasbyState) {
            case WALK:
                aiming(delta);
                updateDirection();
                updateWalkAnimation();
                break;
            case PREPARE_DASH:
                updateAnimation(prepareDashAnimation);
                if (prepareDashAnimation.isAnimationFinished(stateTime)) {
                    gasbyState = GasbyState.DASH_ATTACK;
                    stateTime = 0; // Reset timer for dash
                }
                break;
            case DASH_ATTACK:
                updateAnimation(dashAttackAnimation);
                dashToTarget(delta);
                if (dashAttackAnimation.isAnimationFinished(stateTime)) {
                    gasbyState = GasbyState.WALK;
                    dashCooldown = DASH_CD_TIME;
                }
                break;
            case POLTERGEIST:
                updateAnimation(poltergeistAnimation);
                // Call shootPoltergeist() logic here
                if (poltergeistAnimation.isAnimationFinished(stateTime)) {
                    shootPoltergeist();
                    gasbyState = GasbyState.WALK;
                }
                break;
        }
    }

    private int countNearbyGasby() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e instanceof GasbySamSi && e != this) {
                if (e.getPosition().dst(this.position) <= SHOOT_RANGE) {
                    count++;
                }
            }
        }
        return count;
    }

    private void shootPoltergeist() {
        Vector2 dir = new Vector2(target.getPosition()).sub(position).nor();
        GasbyPoltergeist shot = new GasbyPoltergeist(position.x, position.y, dir, 10f, 3);
        projectiles.add(shot);
    }

    private void dashToTarget(float delta) {
        Vector2 dir = new Vector2(target.getPosition()).sub(position).nor();
        position.add(dir.scl(dashSpeed * delta));
        if (bounds.overlaps(target.getBounds())) {
            target.takeDamage(ATK);
        }
    }

    @Override
    public void gotDamage(int damage) {
        if (!isDead) {
            HP -= damage;
            if (HP <= 0) {
                die();
            } else {
                gasbyState = GasbyState.DAMAGED;
                stateTime = 0;
            }
        }
    }

    private void updateAnimation(Animation<TextureRegion> animation) {
        currentFrame = animation.getKeyFrame(stateTime, gasbyState == GasbyState.WALK);
    }

    @Override
    protected void updateWalkAnimation() {
        switch (facing) {
            case UP:
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case DOWN:
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                break;
            case LEFT:
                currentFrame = walkLeft.getKeyFrame(stateTime, true);
                break;
            case RIGHT:
                currentFrame = walkRight.getKeyFrame(stateTime, true);
                break;
        }
    }

    protected void updateDamagedAnimation() {
        if (damagedAnimation == null) return;
        currentFrame = damagedAnimation.getKeyFrame(stateTime, false);
        if (damagedAnimation.isAnimationFinished(stateTime)) {
            gasbyState = GasbyState.WALK;
        }
    }

    @Override
    protected void die() {
        isDead = true;
        Dying = true;
        target.heal(10);
        dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        // The Enemy superclass already has a render so... We just call it.
        super.render(batch);
    }

    @Override
    public void dispose() {
        redGasbySide.dispose();
        redGasbyUp.dispose();
        redGasbyDown.dispose();
    }
}
