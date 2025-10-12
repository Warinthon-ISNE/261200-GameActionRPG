package ISNE12.project;

public class Zombie extends Enemy {

    // zombie class base stat
    public Zombie(float x, float y, Character target) {
        super(x, y, new EnemyStat(10, 10, 0), 1, target); // base stats
    }

    @Override
    public void update(float delta) {
        super.update(delta); // update movement
    }

    @Override
    public void dispose() {
        // remove when dead
    }
}
