# 261200-GameActionRPG

/// *Note for Pond* -- Warinthon or who thats been coding the MainGame.
for using Ememy, please adding Enemy array in Create() before render()
using #import com.badlogic.gdx.utils.Array and don't forget import Enemy or entities package. ///

You can create the function that 'Spawn' enemies with array enemies
ex.
  enemies.spawn(new Zombie(..stat, speed, target..));
  enemies.spawn(new GasbiSamSi(..stat, speed, target..)); //I will add the code about random spawn each color later

  SpawnTimer += delta;
  if(SpawnTimer > xf){
    enemies.spawn(new //s.th() Math.random * <ScreenWidth>, Math.random * <ScreenLength>, Character)
  }
  then update it using delta or s.th
