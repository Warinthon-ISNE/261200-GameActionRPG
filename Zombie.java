package ISNE.lab.preGame.Entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Zombie extends Enemy {
    //zombie class base stat
    public Zombie(float x, float y, EnemyStat stat, int speed, Character target){
        super(x, y, new EnemyStat(10,10,0), 10, target); //assuming
    }

    private Texture spriteSheet;

    /*
    Texture spriteSheet = new Texture(Gdx.files.internal("...")); //for Left/Right
    //Texture spriteSheet = new Texture(Gdx.files.internal("...")); //up/down (later)
    TextureRegion[][] frames = TextureRegion.spilts(spriteSheet, .., ..);

    //Animations
    walkLeft = new Animation<>(0.15f, frames[][]);
    walkRight = new Animation<>(0.15f, frames[][]);
    
    attackAnimation = new Animation<>(0.1f, frames[][]);
    damagedAnimation = new Animation<>(0.1f, frames[][]);
    deathAnimation = new Animation<>(0.1f, frames[][]);
     */

    @Override
    public void update(float delta){
        super.update(delta); //update movement
    }
    @Override
    public void dispose() {
        super.dispose();
        spriteSheet.dispose();
    } //remove when died
}
