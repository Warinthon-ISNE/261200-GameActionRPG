# 261200-GameActionRPG

/// *Note for Pond* -- Warinthon or who thats been coding the MainGame. ///

I've been alrady create the Google Drive folder to stored some translucent png file, you and everyone be able to see,
[Jump in Drive](https://drive.google.com/drive/folders/1ufpBIZez97-RrK6dsuVYBR6SP3JCLgV_?usp=sharing)

for using Ememy, please adding Enemy array in Create() before render()
using #import com.badlogic.gdx.utils.Array and don't forget import Enemy or entities package.

You can create the function that 'Spawn' enemies with array enemies
ex.
  enemies.spawn(new Zombie(..stat, speed, target..));
  enemies.spawn(new GasbiSamSi(..stat, speed, target..)); //I will add the code about random spawn each color later

  SpawnTimer += delta;
  if(SpawnTimer > xf){
    enemies.spawn(new //s.th() Math.random * <ScreenWidth>, Math.random * <ScreenLength>, Character)
  }
  then update it using delta or s.th

# Enemy type

  Zombie = normal melee type enemy
  
  GasbySamSi (red and blue) can make decision to choose way to attack -- Gasby pretend to attack by poltergeist (7*7 or lesser) first but it can also using dash attack (4*4 or lesser && have any more that 3 other gasby surround)
  and the last type, purpleGasby only dash attack in range 4*4 or lesser
