package ml;

public class Obstacle{
    float posX;
    int w, h;
    int type;

    Obstacle(int t){
        posX = Game.processing.width;
        type = t;
        switch(type){
            case 0: w = 20;
                h = 40;
                break;
            case 1: w = 30;
                h = 60;
                break;
            case 2: w = 60;
                h = 40;
                break;
        }
    }

    void show(){
        switch(type){
            case 0: Game.processing.image(Game.smallCactus, posX - Game.smallCactus.width / 2, Game.processing.height - Game.groundHeight - Game.smallCactus.height);
                break;
            case 1: Game.processing.image(Game.bigCactus, posX - Game.bigCactus.width / 2, Game.processing.height  - Game.groundHeight - Game.bigCactus.height);
                break;
            case 2: Game.processing.image(Game.manySmallCactus, posX - Game.manySmallCactus.width / 2, Game.processing.height   - Game.groundHeight - Game.manySmallCactus.height);
                break;
        }
    }

    void move(float speed){
        posX -= speed;
    }

    boolean collided(float playerX, float playerY, float playerWidth, float playerHeight){
        float playerLeft = playerX - playerWidth / 2;
        float playerRight = playerX + playerWidth / 2;
        float thisLeft = posX - w / 2;
        float thisRight = posX + w / 2;

        if(playerLeft < thisRight && playerRight > thisLeft){
            float playerDown = playerY - playerHeight / 2;
            float thisUp = h;
            if(playerDown < thisUp){
                return true;
            }
        }
        return false;
    }
}

