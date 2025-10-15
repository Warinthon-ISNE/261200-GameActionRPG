import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BlueGasby extends GasbySamSi {
    private Array<Projectile> projectiles;
    private Array<Enemy> enemies;

    // Poltergeist attack
    private float poltergeistCooldown = 4f;
    private float poltergeistTimer = 0f;

    // Dash attack
    private float dashCooldown = 0f;
    private static final float DASH_CD_TIME = 8f;
    private float dashSpeed = 15f;
    private static final float DASH_RANGE = 3f;
    private boolean isDashing = false;

    // Debuff logic
    private static final float DEBUFF_CHANCE = 0.4f;
    private float debuffDuration = 0f;
    private final float maxDebuffDuration = 5f;

    // BlueGasby TextureSheets
    private final Texture blueGasbySide;
    private final Texture blueGasbyUp;
    private final Texture blueGasbyDown;

    // Custom animations
    private Animation<TextureRegion> prepareDashAnimation;
    private Animation<TextureRegion> dashAttackAnimation;
    private Animation<TextureRegion> poltergeistAnimation;

    // Custom state for BlueGasby
    private GasbyState gasbyState = GasbyState.WALK;

    // Constructor
    public BlueGasby(float x, float y, Character target, Array<Projectile> projectiles, Array<Enemy> enemies) {
        super(x, y, "blue", target);
        this.projectiles = projectiles;
        this.enemies = enemies;
        this.HP = 70;
        this.maxHP = 70;
        this.ATK = 15;
        this.speed = 10;

        // Load sprite sheets
        blueGasbySide = new Texture(Gdx.files.internal("Side_BlueGasby.png"));
        blueGasbyUp = new Texture(Gdx.files.internal("Back_BlueGasby.png"));
        blueGasbyDown = new Texture(Gdx.files.internal("Front_BlueGasby.png"));

        // Split sprite sheets (assuming 5x6 format as per RedGasby)
        TextureRegion[][] framesSide = TextureRegion.split(blueGasbySide, 104, 129);
        TextureRegion[][] framesUp = TextureRegion.split(blueGasbyUp, 104, 129);
        TextureRegion[][] framesDown = TextureRegion.split(blueGasbyDown, 104, 129);

        // Define animations
        walkRight = new Animation<>(0.15f, framesSide[0]);
        walkLeft = new Animation<>(0.15f, createFlippedFrames(framesSide[0]));
        walkUp = new Animation<>(0.15f, framesUp[0]);
        walkDown = new Animation<>(0.15f, framesDown[0]);

        prepareDashAnimation = new Animation<>(0.1f, framesSide[1]);
        dashAttackAnimation = new Animation<>(0.1f, framesSide[2]);
        poltergeistAnimation = new Animation<>(0.1f, framesSide[3]);
        damagedAnimation = new Animation<>(0.1f, framesSide[4]);
        deathAnimation = new Animation<>(0f, new TextureRegion());

        currentFrame = walkDown.getKeyFrame(0);
    }

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
        poltergeistTimer += delta;
        dashCooldown += delta;

        if (isDead) {
            // Already handled by Enemy superclass
            return;
        }

        // --- Logic to determine state ---
        int nearbyGasby = countNearbyGasby();
        float distanceToPlayer = position.dst(target.getPosition());

        if (gasbyState != GasbyState.DAMAGED) {
            if (nearbyGasby >= 4 && distanceToPlayer <= DASH_RANGE && dashCooldown >= DASH_CD_TIME) {
                if (gasbyState != GasbyState.PREPARE_DASH) {
                    gasbyState = GasbyState.PREPARE_DASH;
                    stateTime = 0;
                }
            } else if (distanceToPlayer <= 7f) {
                if (poltergeistTimer >= poltergeistCooldown) {
                    if (gasbyState != GasbyState.POLTERGEIST) {
                        gasbyState = GasbyState.POLTERGEIST;
                        stateTime = 0;
                    }
                }
            } else {
                gasbyState = GasbyState.WALK;
            }
        }

        // --- State machine execution ---
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
                    stateTime = 0;
                    isDashing = true;
                }
                break;
            case DASH_ATTACK:
                updateAnimation(dashAttackAnimation);
                dashToTarget(delta);
                if (dashAttackAnimation.isAnimationFinished(stateTime)) {
                    isDashing = false;
                    gasbyState = GasbyState.WALK;
                    dashCooldown = 0f;
                }
                break;
            case POLTERGEIST:
                updateAnimation(poltergeistAnimation);
                if (poltergeistAnimation.isAnimationFinished(stateTime)) {
                    shootPoltergeist();
                    poltergeistTimer = 0f; // Reset cooldown after shooting
                    gasbyState = GasbyState.WALK;
                }
                break;
            case DAMAGED:
                updateAnimation(damagedAnimation);
                if (damagedAnimation.isAnimationFinished(stateTime)) {
                    gasbyState = GasbyState.WALK;
                }
                break;
        }

        // Apply debuff duration logic if debuff is active
        // This is handled in the Character class, not here.
    }

    private void updateAnimation(Animation<TextureRegion> animation) {
        currentFrame = animation.getKeyFrame(stateTime, gasbyState == GasbyState.WALK);
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

    private int countNearbyGasby() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e instanceof GasbySamSi && e != this) {
                if (e.getPosition().dst(this.position) <= 7f) {
                    count++;
                }
            }
        }
        return count;
    }

    private void shootPoltergeist() {
        Vector2 dir = new Vector2(target.getPosition()).sub(position).nor();
        projectiles.add(new GasbyPoltergeist(position.x, position.y, dir, 10f, 3));
    }

    private void dashToTarget(float delta) {
        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        position.add(direction.scl(dashSpeed * delta));

        if (isDashing && bounds.overlaps(target.getBounds())) {
            applyDashDebuff();
            isDashing = false; // Prevents multiple debuffs per dash
        }
    }

    private void applyDashDebuff() {
        if (Math.random() < DEBUFF_CHANCE) {
            int debuffType = MathUtils.random(0, 5);
            if (debuffType == 0) {
                target.decreaseSpeed(10, 1.5f);  // Decrease speed by 10%
                System.out.println("Gasby use the tricky effective curse!!! Player speed longer decreased!");
            } else if (debuffType >= 1 && debuffType <= 3) {
                target.decreaseSpeed(10, 0.5f);
                System.out.println("Player speed decreased!");
            } else if (debuffType == 6) {
                target.decreaseAttack(20, 0.5f); // Decrease ATK by 10%
                System.out.println("Gasby use the tricky effective curse!!! Player's attack multiple decreased!");
            } else {
                target.decreaseAttack(10, 0.3f); // Decrease ATK by 10%
                System.out.println("Player attack decreased!");
            }
            debuffDuration = 0f;  // Reset debuff timer
        }
    }

    @Override
    protected void die() {
        isDead = true;
        Dying = true;
        dispose();
    }

    @Override
    public void render (SpriteBatch batch){
        super.render(batch);
    }

    @Override
    public void dispose() {
        blueGasbySide.dispose();
        blueGasbyUp.dispose();
        blueGasbyDown.dispose();
    }
}
