import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class GasbyPoltergeist extends Projectile {
    private Texture texture;

    // poltergeist shot
    private static final float SIZE = 0.6f;
    private float shootCooldown = 4f;  // cooldown for next poltergeist attack
    private float shootTimer = 0f;
    private float shotDelay = 0.05f;  // delay each shot (overall 3 shot)
    private float shotTimer = 0f;     //
    private int shotCount = 0;

    // attack range
    private static final float SHOOT_RANGE = 7f;

    public GasbyPoltergeist(float x, float y, Vector2 direction, float speed, int damage) {
        super(x, y, direction, speed, damage, SIZE, 8f);
        this.texture = new Texture("<poltergeist.png>"); // ใส่ texture จริงตอนหลัง
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //check poltergeist attack & each shot cooldown
        shootTimer += delta;
        shotTimer += delta;

        if (shootTimer >= shootCooldown && shotCount >= 3) { //prapare next poltergeist
            shootTimer = 0f;
            shotCount = 0;
        }

        if (shotCount < 3 && shotTimer >= shotDelay) {
            shootNextProjectile();
            shotTimer = 0f;
        }
    }

    private void shootNextProjectile() {
        float dx = target.getPosition().x - position.x;
        float dy = target.getPosition().y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= SHOOT_RANGE) {
            Vector2 direction = new Vector2(dx, dy).nor();  // ทิศทางไปหาผู้เล่น

            GasbyPoltergeist newProjectile = new GasbyPoltergeist(position.x, position.y, direction, 10f, 3); // ปรับความเร็วและดาเมจตามต้องการ
/*
//use for mainGame
            enemies.add(newProjectile);
*/

            shotCount++;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;
        batch.draw(texture, position.x, position.y, SIZE, SIZE);
    }

    // check is poltergeist hit or not
    public void checkHit(Character target) {
        if (!active) return;
        if (bounds.overlaps(target.getBounds())) {
            target.gotDamage(damage);
            disappear();
        }
    }

    @Override
    public void disappear() {
        active = false;
    }
}
