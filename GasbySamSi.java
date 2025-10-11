import java.util.Random;

public class GasbySamSi extends Enemy {
    private String color;
    private Random random = new Random();

    public GasbySamSi(float x, float y, String color, Character target) {
        super(x, y, getHP(color), getATK(color), getDEF(color), 30f, target);
        this.color = color.toLowerCase();
    }
    //We have 3 different type of Gasby -- Red, Blue & Purple
    //update Stat bt each color
    private static int getHP(String color){
        switch (color.toLowerCase()) {
            case "blue": return 30;
            case "red": return 30;
            case "purple": return 60;
            default: return 30;
        }
    }
    private static int getATK(String color){
        switch (color.toLowerCase()) {
            case "blue" : return 10; //normal Gasby -> BlueGasby
            case "red" : return 25; //RedGasby, Damage dealer gasby
            case "purple" : return 20; //purpleGasby, more Def & HP
            default: return 10;
        }
    }
    private static int getDEF(String color){
        switch (color.toLowerCase()) {
            case "blue" : return 20;
            case "red" : return 20;
            case "purple" : return 30;
            default: return 20;
        }
    }

    /*
    for idea of this Enemy, each different color will give s.th of Character, after they're died
    the red one gain Character HP x% from total (random chance)
    purple will give Character DEF (random chance 50% -- random number between 1-100)/*
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

            case "purple":
                if (dropChance > 50) {
                    gainDEF(); //use in Character
                }
                break;
        }
    }

    private void heal(){
        int healAmount = (int) (target.getMaxHp() * 0.05f); //5% OF maxhp
        target.heal(healAmount);
        System.out.println("Player healed by " + healAmount + " from Red Gasby drop!");
    }
    private void gainDEF() {
        int defAmount = (int) (target.getMaxDEF() * 0.10f); //10% OF maxDEF
        target.gainDEF(defAmount);
        System.out.println("Player gained +" + defAmount + " DEF from Purple Gasby drop!");
    }

    @Override
    public void update(float delta){
        super.update(delta);
    }
    @Override
    public void dispose() {}
}
