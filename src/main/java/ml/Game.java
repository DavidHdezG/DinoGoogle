package ml;

import processing.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends PApplet {
    public static Game processing;
    static PImage dinoRun1;
    static PImage dinoRun2;
    static PImage dinoJump;
    static PImage dinoDuck;
    static PImage dinoDuck1;
    static PImage smallCactus;
    static PImage bigCactus;
    static PImage manySmallCactus;
    static PImage bird;
    static PImage bird1;

    static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    static ArrayList<Bird> birds = new ArrayList<Bird>();
    static ArrayList<Ground> grounds = new ArrayList<Ground>();

    int obstacleTimer = 0;
    int minTimeBetObs = 60;
    int randomAddition = 0;
    int groundCounter = 0;
    float speed = 10;

    static int groundHeight = 50;
    static int playerXpos = 100;
    int highScore = 0;
    int generation = 1;
    List<Dino> dino = new ArrayList<>();
    Population population = new Population();

    public void settings(){
        size(800, 400);
        processing = this;
    }

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

        dino=population.firstPopulation();
    }

    public void draw(){
        background(250);
        stroke(0);
        strokeWeight(2);
        int score;
        int alive=0;
        line(0, height - groundHeight - 30, width, height - groundHeight - 30);

        updateObstacles();

        ArrayList<Integer> scores = new ArrayList<>();

        for (Dino value : dino) {
            scores.add(value.score);
            if(!value.dead){
                alive++;
            }
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
        text("Generation: " + generation, 5, 40);
        text("Alive: " + alive, 5, 60);
    }
    public void keyPressed(){
       /* switch(key){
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
        }*/
    }

    public void keyReleased(){
        /*boolean alldead = true;

        switch(key){
            case 's': for(int x=0; x<dino.size(); x++){
                if(!dino.get(x).dead){
                    dino.get(x).ducking(false);
                }
            }
                break;
            case 'r':
                for (Dino value : dino) {
                    if (!value.dead) {
                        alldead = false;
                    }

                }
                if(alldead){
                    reset();
                }
                break;
        }*/
    }

    void updateObstacles(){

        boolean alive = false;
        //int aliveCount = 0;

        showObstacles();
        for(int x=0; x<dino.size(); x++){
            dino.get(x).show();
           /* if(!dino.get(x).dead){
                aliveCount++;
            }*/
        }
       // System.out.println(aliveCount);
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

            float lessDistance=2000;
            Obstacle lessObstacle = null;
            Bird lessBird = null;
            int less=0;

            for(Obstacle o:obstacles){
                if(o.posX<lessDistance){
                    lessDistance=o.posX;
                    lessObstacle=o;
                    less=1;
                }
            }

            for(Bird b:birds){
                if(b.posX<lessDistance){
                    lessDistance=b.posX;
                    lessBird=b;
                    less=2;
                }
            }

            if(lessDistance < 0){
                lessDistance = 2000;
            }
            if(less==1){
                for(Dino dino:dino){
                    dino.update();
                    dino.act(new float[]{lessDistance,lessObstacle.posX,lessObstacle.w,lessObstacle.h,dino.posY,speed});

                }
            }else if(less==2){
                for(Dino dino:dino){
                    dino.update();
                    dino.act(new float[]{lessDistance,lessBird.posX/*,lessBird.posY*/,lessBird.w,lessBird.h,dino.posY,speed});
                }
            } else{
                for(Dino dino:dino){
                    dino.update();
                    dino.act(new float[]{lessDistance,0,0,0,dino.posY,speed});
                }
            }
        }

        else{

                reset();

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
        if(random(1) < 0.3){
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
        dino=population.nextPopulation();
        obstacles = new ArrayList<Obstacle>();
        birds = new ArrayList<Bird>();
        grounds = new ArrayList<Ground>();
        generation++;
        obstacleTimer = 0;
        randomAddition = floor(random(50));
        groundCounter = 0;
        speed = 10;
    }
}
