package ISNE.lab.preGame.Entities;

public class Zombie extends Enemy {
    //zombie class base stat
    public Zombie(float x, float y, EnemyStat stat, int speed, Character target){
        super(x, y, new EnemyStat(10,10,0), 10, target); //assuming
    }
    @Override
    public void update(float delta){
        super.update(delta); //update movement
    }
    @Override
    public void dispose() {} //remove when died
}
