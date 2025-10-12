package ISNE12.project;

import static com.badlogic.gdx.math.MathUtils.random;

public class GasbySamSi extends Enemy {

    // We have 3 different types of Gasby -- Red, Blue & Purple
    public GasbySamSi(float x, float y, String color, float speed, Character target) {
        super(x, y, new EnemyStat(getHP(color), getATK(color), getDEF(color)), speed, target);
    }

    // update Stat by each color
    private static int getHP(String color) {
        switch (color.toLowerCase()) {
            case "blue": return 30;   // normal Gasby
            case "red": return 30;    // fast Gasby
            case "purple": return 60; // tank Gasby
            default: return 30;
        }
    }

    private static int getATK(String color) {
        switch (color.toLowerCase()) {
            case "blue": return 10;
            case "red": return 20;    // damage dealer
            case "purple": return 10;
            default: return 10;
        }
    }

    private static int getDEF(String color) {
        switch (color.toLowerCase()) {
            case "blue": return 20;
            case "red": return 20;
            case "purple": return 30;
            default: return 20;
        }
    }

    /*
    // For future upgrade idea: each color gives Character something after death
    @Override
    protected void die() {
        super.die();
        drop();
    }

    private void drop() {
        // RedGasby: chance to heal
        // PurpleGasby: chance to increase DEF
        if (color.equals("red") && random.nextInt(100) >= 50) {
            target.heal(...);
        }
        if (color.equals("purple") && random.nextInt(100) >= 40) {
            target.gainDEF(...);
        }
    }
    */

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void dispose() {
        // remove from game
    }
}
