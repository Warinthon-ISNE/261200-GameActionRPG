import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class BlueGasby extends GasbySamSi{
        private float poltergeistCooldown = 4f;   // Cooldown สำหรับ Poltergeist
        private float poltergeistTimer = 0f;      // Timer สำหรับ Poltergeist
        private boolean canShootPoltergeist = true;  // เช็คว่าใช้ Poltergeist ได้ไหม
        private Array<Enemy> enemies;

        // Dash attack
        private float dashCooldown = 0f;          // คูลดาวน์การพุ่งชน (Dash)
        private float dashSpeed = 6f;
        private boolean isDashing = false;

        // Debuff duration
        private float debuffDuration = 0f;
        private final float maxDebuffDuration = 5f; // Max duration for debuff
        private boolean debuffApplied = false;

        public BlueGasby(float x, float y, Character target, Array<Enemy> enemies) {
            super(x, y, "blue", target);
            this.enemies = enemies;
        }

        @Override
        public void update(float delta) {
            super.update(delta);
            poltergeistTimer += delta;
            dashCooldown += delta;
            debuffDuration += delta;

            if (debuffApplied && debuffDuration >= maxDebuffDuration) {
                removeDebuff(); // reset debuff when times out
            }

            if (!isDead) {
                if (isInPoltergeistRange()) { //Gasby aways pretend to use 'poltergeist'
                    if (countNearbyGasby() < 3) {
                        if (canShootPoltergeist) {
                            shootPoltergeist();
                        }
                    } else {
                        dashAttack(delta); //confirm that has 'gasby' near by (already use poltergeist by default)
                    }
                } else {
                    aiming(delta); // เดินเข้าไป
                }
            }
        }

        // Poltergeist (7x7 from Character)
        private boolean isInPoltergeistRange() {
            float distance = Vector2.dst(position.x, position.y, target.getPosition().x, target.getPosition().y);
            return distance <= 7f;
        }

        // to check is there any Gasby in range -> create for Gasby attack decision (poltergeist or Dash)
        private int countNearbyGasby() {
            int count = 0;
            for (Enemy e : enemies) {
                if (e instanceof GasbySamSi) {
                    float distance = Vector2.dst(e.getPosition().x, e.getPosition().y, position.x, position.y);
                    if (distance <= 7f) {
                        count++;
                    }
                }
            }
            return count;
        }

        // Poltergeist attack (ranged attack)
        private void shootPoltergeist() {
            if (poltergeistTimer >= poltergeistCooldown) {
                Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
                Projectile poltergeist = new GasbyPoltergeist(position.x, position.y, direction, 10f, target);
                poltergeistTimer = 0f; // reset cooldown for next attack
                canShootPoltergeist = false; // stop Poltergeist
            }
        }

        // Dash attack (Melee 3*3)
        private void dashAttack(float delta) {
            if (dashCooldown >= 1f) {
                Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
                position.add(direction.scl(dashSpeed * delta));  // พุ่งเข้าหาผู้เล่น

                if (Vector2.dst(position.x, position.y, target.getPosition().x, target.getPosition().y) <= 3f) {
                    applyDashDebuff();  // ทำให้ Player ติด debuff
                    dashCooldown = 0f;  // รีเซ็ต cooldown ของ Dash
                }
            }
        }

        // Apply debuff (Speed, ATK, DEF)
        private void applyDashDebuff() {
            if (!debuffApplied) {
                if (Math.random() < 0.4) {
                    // Randomly apply debuff to player
                    int debuffType = MathUtils.random(0, 2); // 0 - Speed, 1 - ATK, 2 - DEF

                    if (debuffType == 0) {
                        target.decreaseSpeed(10, 0.5f);  // Decrease speed by 10%
                        System.out.println("Player speed decreased!");
                    } else if (debuffType == 1) {
                        target.decreaseAttack(10, 0.3f); // Decrease ATK by 10%
                        System.out.println("Player attack decreased!");
                    } else {
                        target.decreaseDefense(10, 0.5f); // Decrease DEF by 10%
                        System.out.println("Player defense decreased!");
                    }
                }
                debuffApplied = true;  // Mark debuff as applied
                debuffDuration = 0f;  // Reset debuff timer
            }
        }

        // Remove debuff after its duration
        private void removeDebuff() {
            target.resetDebuff();  // Reset stats to normal
            debuffApplied = false;  // Mark debuff as removed
            System.out.println("Debuff removed from Character!");
        }

        // Handle the death of BlueGasby
        @Override
        protected void die() {
            super.die();
            drop();
        }

        // Override drop function if any special drop
        private void drop() {
            System.out.println("Blue Gasby has dropped something.");
        }

        @Override
        public void render(SpriteBatch batch) {
            super.render(batch);
            // Optional: render animation for Poltergeist and Dash
        }
}
