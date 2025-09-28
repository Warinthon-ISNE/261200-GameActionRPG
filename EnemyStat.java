package ISNE.lab.preGame.Entities;

public class EnemyStat {
    public float HP;
    public int ATK;
    public float DEF;

    //constructor
    public EnemyStat(float HP, int ATK, float DEF) {
        this.HP = HP;
        this.ATK = ATK;
        this.DEF = DEF;
    }

    public  void gotDamage(int damage){
        if(DEF > 0) {
            DEF -= damage;
            if(DEF <= 0){
                HP += DEF;
                DEF = 0;
            }
        } else {
            HP -= damage; //in case enemy no DEF: HP - damage
        }
        if (HP <= 0) HP = 0;
    }
    public float getHP() {
        return HP;
    }
}
