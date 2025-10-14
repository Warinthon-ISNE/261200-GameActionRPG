import java.util.Random;

public class GasbySamSi extends Enemy { //wah.. now we have just two color--
    private String color;
    private Random random = new Random();

    // attack
    protected float attackCooldown = 4f;     // Cooldown
    protected float attackTimer = 0f;
    protected float dashRange = 4f;          // Dash attack range (from character)
    protected float shootRange = 7f;         // Poltergeist attack range (from character)
    protected float dashSpeed = 6f;
    protected boolean isDashing = false;      //All of Gasby must "Range" at first, if match the *condition* it will DASH

    public GasbySamSi(float x, float y, String color, Character target) {
        super(x, y, getHP(color), getATK(color), 30f, target);
        this.color = color.toLowerCase();
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            super.update(delta);
            return;
        }

        attackTimer += delta;

        if (shouldAttack()) {
            performAttack(delta); //Poltergeist (range) or DASH (melee)
        } else {
            super.aiming(delta); // not in attack range -> walk to character
        }

        super.update(delta); // to animation + update bounds
    }

    protected boolean shouldAttack() {
        return false;
    }

    protected void performAttack(float delta) {}

    //We have 2 different type of Gasby -- Red & Blue
    //update Stat bt each color
    private static int getHP(String color){
        switch (color.toLowerCase()) {
            case "blue": return 50;
            case "red": return 50;
            default: return 50;
        }
    }
    private static int getATK(String color){ //for DASH damage
        switch (color.toLowerCase()) {
            case "blue" : return 15; //normal Gasby -> BlueGasby
            case "red" : return 35; //RedGasby, Damage dealer gasby
            default: return 15;
        }
    }

    /*
    for idea of this Enemy, each different color will give s.th of Character, after they're died
    the red one gain Character HP x% from total (random chance) -- random number between 1-100)/*
     */

    @Override
    protected void die(){
        super.die();
        drop(this.color);
    }

    private void drop(String color){
        int dropChance = random.nextInt(100);
        switch (color.toLowerCase()) {
            case "blue": //don't drop anything
                break;

            case "red":
                if (dropChance > 50) {
                    heal(); //use in Character
                }
                break;
        }
    }

    protected void heal(){
        int healAmount = (int) (target.getMaxHp() * 0.05f); //5% OF maxhp
        target.heal(healAmount);
        System.out.println("Player healed by " + healAmount + " from Red Gasby drop!");
    }

    @Override
    public void dispose() {}
}
