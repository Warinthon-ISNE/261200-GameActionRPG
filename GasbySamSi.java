package ISNE.lab.preGame.Entities;

import static com.badlogic.gdx.math.MathUtils.random;

public class GasbySamSi extends Enemy {
    public GasbySamSi(float x, float y, String color, int speed, Character target){
        super(x, y, new EnemyStat(getHP(color),getATK(color),getDEF(color)), 30, target);
    }
    //We have 3 different type of Gasby -- Red, Blue & Purple

    //update Stat bt each color
    private static int getHP(String color){
        switch(color){
            case "blue" : return 30; //normal Gasby -> BlueGasby
            case "red" : return 30; //RedGasby, fast gasby
            case "Purple" : return 60; //purpleGasby, more Def & HP
            default: return 30;
        }
    }
    private static int getATK(String color){
        switch(color){
            case "blue" : return 10; //normal Gasby -> BlueGasby
            case "red" : return 20; //RedGasby, Damage dealer gasby
            case "Purple" : return 10; //purpleGasby, more Def & HP
            default: return 10;
        }
    }
    private static int getDEF(String color){
        switch(color){
            case "blue" : return 20;
            case "red" : return 20;
            case "Purple" : return 30;
            default: return 20;
        }
    }

    /*
    //for idea of this Enemy, each different color will give s.th of Character, after they're died
    //the red one gain Character HP x% from total (random chance)
    //purple will give Character DEF (random chance)
    @Override
    protected void die(){
        super.die();
        drop();
    }

    private void drop(){
        int dropChance = random.nextInt(100); //from 100%
        if() blueGasby = not drop anything
        else if() redGasby = drop Character Hp x% of total by chance 50-100%
        else if() purpleGasby = drop Character DEF y% of total by chance 60-100%

        check Chance for each,
        using (random.nextInt(100) //each time after Gasby has been slain, random 0 to 100
        //for red, chance 50 to 100 -> don't be random less than 50
        if(random.nextInt(100) >= 50){
            heal(); //use in Character
        }
        //for purple, chance 60 to 100 -> don't be random less than 40
        if(random.nextInt(100) >= 40){
            gainDEF(); //use in Character
        }
    }

    private void heal(){
        int healAmount = //calculate maxHP * x;
        target.heal(healAmount);

        //future may be add texture HP bar for Character and Enemy
    }
    private void gainDEF() {
        int armor = //calculate maxDEF * x;
        target.gainDEF(armor);
    }
*/
    @Override
    public void update(float delta){
        super.update(delta);
    }
    @Override
    public void dispose() {}
}
