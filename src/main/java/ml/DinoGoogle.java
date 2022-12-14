package ml;
import processing.core.*;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Collections;

public class DinoGoogle extends PApplet{
    PImage dinoRun1;
    PImage dinoRun2;
    PImage dinoJump;
    PImage dinoDuck;
    PImage dinoDuck1;
    PImage smallCactus;
    PImage bigCactus;
    PImage manySmallCactus;
    PImage bird;
    PImage bird1;


    ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    ArrayList<Bird> birds = new ArrayList<Bird>();
    ArrayList<Ground> grounds = new ArrayList<Ground>();

    int obstacleTimer = 0;
    int minTimeBetObs = 60;
    int randomAddition = 0;
    int groundCounter = 0;
    float speed = 10;

    int groundHeight = 50;
    int playerXpos = 100;
    int highScore = 0;
    ArrayList<Playerjeje> dino = new ArrayList<>();

    @Override
    public void settings(){
        size(800, 400);
        frameRate(60);
    }

    @Override
    public void setup(){
        dinoRun1 = loadImage("dinorun0000.png");
        dinoRun2 = loadImage("dinorun0001.png");
        dinoJump = loadImage("dinoJump0000.png");
        dinoDuck = loadImage("dinoduck0000.png");
        dinoDuck1 = loadImage("dinoduck0001.png");
        smallCactus = loadImage("cactusSmall0000.png");
        bigCactus = loadImage("cactusBig0000.png");
        manySmallCactus = loadImage("cactusSmallMany0000.png");
        bird = loadImage("berd.png");
        bird1 = loadImage("berd2.png");

        for(int x=0; x<1000; x++){
            dino.add(new Playerjeje(x));
        }
    }

    @Override
    public void draw(){
        background(250);
        stroke(0);
        strokeWeight(2);
        int score;
        line(0, height - groundHeight - 30, width, height - groundHeight - 30);

        updateObstacles();

        ArrayList<Integer> scores = new ArrayList<>();

        for(int x=0; x<dino.size(); x++){
            scores.add(dino.get(x).score);
        }

        for(int x=0; x<dino.size(); x++){
            score = scores.get(x);
            if(score > highScore){
                highScore = score;
            }
        }


        textSize(20);
        fill(0);
        text("Score: " + Collections.max(scores), 5, 20);
        text("High Score: " + highScore, width - (140 + (str(highScore).length() * 10)), 20);
    }
    public void keyPressed(){
        switch(key){
            case 'w':
                for(int x=0; x<dino.size(); x++){
                    dino.get(x).jump();
                }
                break;
            case 's':
                for(int x=0; x<dino.size(); x++){
                    if(!dino.get(x).dead){
                        dino.get(x).ducking(true);
                    }
                }
                break;
        }
    }

    public void keyReleased(){
        boolean alldead = true;

        switch(key){
            case 's': for(int x=0; x<dino.size(); x++){
                if(!dino.get(x).dead){
                    dino.get(x).ducking(false);
                }
            }
                break;
            case 'r': for(int x=0; x<dino.size(); x++){
                if(!dino.get(x).dead){
                    alldead = false;
                }

            }
                if(alldead){
                    reset();
                }
                break;
        }
    }

    void updateObstacles(){

        boolean alive = false;

        showObstacles();
        for(int x=0; x<dino.size(); x++){
            dino.get(x).show();
        }

        for(int x=0; x<dino.size(); x++){
            if(!dino.get(x).dead){
                alive = true;
            }
        }

        if(alive){
            obstacleTimer++;
            speed += 0.002;
            if(obstacleTimer > minTimeBetObs + randomAddition){
                addObstacle();
            }
            groundCounter++;
            if(groundCounter > 10){
                groundCounter = 0;
                grounds.add(new Ground());
            }
            moveObstacles();
            for(int x=0; x<dino.size(); x++){
                dino.get(x).update();
            }
        }

        else{
            textSize(32);
            fill(0);
            text("Game over :(", 310, 200);
            textSize(16);
            text("(Press 'r' to restart!)", 330, 230);
        }
    }

    void showObstacles(){
        for(int i = 0; i < grounds.size(); i++){
            grounds.get(i).show();
        }
        for(int i = 0; i < obstacles.size(); i++){
            obstacles.get(i).show();
        }
        for(int i = 0; i < birds.size(); i++){
            birds.get(i).show();
        }
    }

    void addObstacle(){
        if(random(1) < 0.15){
            birds.add(new Bird(floor(random(3))));
        }
        else{
            obstacles.add(new Obstacle(floor(random(3))));
        }
        randomAddition = floor(random(50));
        obstacleTimer = 0;
    }

    void moveObstacles(){
        for(int i = 0; i < grounds.size(); i++){
            grounds.get(i).move(speed);
            if(grounds.get(i).posX < -playerXpos){
                grounds.remove(i);
                i--;
            }
        }
        for(int i = 0; i < obstacles.size(); i++){
            obstacles.get(i).move(speed);
            if(obstacles.get(i).posX < -playerXpos){
                obstacles.remove(i);
                i--;
            }
        }
        for(int i = 0; i < birds.size(); i++){
            birds.get(i).move(speed);
            if(birds.get(i).posX < -playerXpos){
                birds.remove(i);
                i--;
            }
        }
    }

    void reset(){
        dino.clear();
        for(int x=0; x<1000; x++){
            dino.add(new Playerjeje(0));
        }
        obstacles = new ArrayList<Obstacle>();
        birds = new ArrayList<Bird>();
        grounds = new ArrayList<Ground>();

        obstacleTimer = 0;
        randomAddition = floor(random(50));
        groundCounter = 0;
        speed = 10;
    }
    class Bird{
        float w = 60;
        float h = 50;
        float posX, posY;
        int flapCount = 0;

        Bird(int t){
            posX = width;
            switch(t){
                case 0: posY = 10 + h / 4;
                    break;
                case 1: posY = 60;
                    break;
                case 2: posY = 130;
                    break;
            }
        }

        void show(){
            flapCount++;
            if(flapCount < 0){
                image(bird, posX - bird.width / 2, height - groundHeight - (posY + bird.height - 20));
            }
            else{
                image(bird1, posX - bird1.width / 2, height - groundHeight - (posY + bird1.height - 20));
            }
            if(flapCount > 15){
                flapCount = -15;
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
                float playerUp = playerY + playerHeight / 2;
                float thisUp = posY + h / 2;
                float thisDown = posY - h / 2;
                if(playerDown <= thisUp && playerUp >= thisDown){
                    return true;
                }
            }
            return false;
        }
    }
    class Ground{
        float posX = width;
        float posY = height - floor(random(groundHeight - 20, groundHeight + 30));
        int w = floor(random(1, 10));

        Ground(){
        }

        void show(){
            stroke(0);
            strokeWeight(3);
            line(posX, posY, posX + w, posY);
        }

        void move(float speed){
            posX -= speed;
        }
    }

    class Obstacle{
        float posX;
        int w, h;
        int type;

        Obstacle(int t){
            posX = width;
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
                case 0: image(smallCactus, posX - smallCactus.width / 2, height - groundHeight - smallCactus.height);
                    break;
                case 1: image(bigCactus, posX - bigCactus.width / 2, height - groundHeight - bigCactus.height);
                    break;
                case 2: image(manySmallCactus, posX - manySmallCactus.width / 2, height - groundHeight - manySmallCactus.height);
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
    class Playerjeje{
        float posY = 0;
        float velY = 0;
        float gravity = 1.2f;
        int size = 20;
        boolean duck = false;
        boolean dead = false;

        public int runCount = -5;
        public int lifespan;
        public int score;
        public int gene;

        Playerjeje(float posYatribute){
            posY = posYatribute;
        }

        void jump(){
            if(posY == 0){
                gravity = (float) 1.2;
                velY = 16;
            }
        }

        void show(){
            if(duck && posY == 0){
                if(runCount < 0){
                    image(dinoDuck, playerXpos - dinoDuck.width / 2, height - groundHeight - (posY + dinoDuck.height));
                }
                else{
                    image(dinoDuck1, playerXpos - dinoDuck1.width / 2, height - groundHeight - (posY + dinoDuck1.height));
                }
            }
            else{
                if(posY == 0){
                    if(runCount < 0){
                        image(dinoRun1, playerXpos - dinoRun1.width / 2, height - groundHeight - (posY + dinoRun1.height));
                    }
                    else{
                        image(dinoRun2, playerXpos - dinoRun2.width / 2, height - groundHeight - (posY + dinoRun2.height));
                    }
                }
                else{
                    image(dinoJump, playerXpos - dinoJump.width / 2, height - groundHeight - (posY + dinoJump.height));
                }
            }

            if(!dead){
                runCount++;
            }
            if(runCount > 5){
                runCount = -5;
            }
        }

        void move(){
            posY += velY;
            if(posY > 0){
                velY -= gravity;
            }
            else{
                velY = 0;
                posY = 0;
            }

            for(int i = 0; i < obstacles.size(); i++){
                if(dead){
                    if(obstacles.get(i).collided((float) playerXpos, posY + dinoDuck.height / 2, (float) (dinoDuck.width * 0.5), dinoDuck.height)){
                        dead = true;
                    }
                }
                else{
                    if(obstacles.get(i).collided((float) playerXpos, posY + dinoRun1.height / 2, (float) (dinoRun1.width * 0.5), dinoRun1.height)){
                        dead = true;
                    }
                }
            }

            for(int i = 0; i < birds.size(); i++){
                if(duck && posY == 0){
                    if(birds.get(i).collided((float) playerXpos, posY + dinoDuck.height / 2, (float) (dinoDuck.width * 0.5), dinoDuck.height)){
                        dead = true;
                    }
                }
                else{
                    if(birds.get(i).collided((float) playerXpos, posY + dinoRun1.height / 2, (float) (dinoRun1.width * 0.5), dinoRun1.height)){
                        dead = true;
                    }
                }
            }
        }

        void ducking(boolean isDucking){
            if(posY != 0 && isDucking){
                gravity = 3;
            }
            duck = isDucking;
        }

        void update(){
            incrementCounter();
            move();
        }

        void incrementCounter(){
            lifespan++;
            if(lifespan % 3 == 0){
                score += 1;
            }
        }
    }

}
