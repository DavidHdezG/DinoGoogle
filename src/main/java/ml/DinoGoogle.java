package ml;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import processing.core.*;
import processing.core.PImage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    boolean firstObstacle=false;
    List<Player> dino = new ArrayList<>();
    Population population = new Population();
    List<Player> pl;
    @Override
    public void settings(){
        size(800, 400);

    }

    @Override
    public void setup(){
        frameRate(60);
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

    public void runGame(){

        //List<Player> pl = population.firstPopulation();
        for (int i = 0; i < 10; i++) {
            population.act(pl.get(i).getId(), new float[]{new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()});
            population.updateScore(pl.get(i).getId(), new Random().nextInt(100));
            population.nextPopulation();
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
        for (Player item : dino) {
            item.show();
        }

        for (Player value : dino) {
            if (!value.dead) {
                alive = true;
                break;
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
            for (Player player : dino) {
                if(firstObstacle){
                    player.update();
                }
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
        firstObstacle=true;
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

        population.nextPopulation();
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
    class Player{
        private String id;
        private MultiLayerNetwork brain;
        private int noInputs=5;
        private int noOutputs=2;
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

        Player(INDArray weights){
            this.createBrain();
            this.brain.init(weights,true);
            this.id = UUID.randomUUID().toString();
        }
        Player(float posYatribute){
            posY = posYatribute;
            this.createBrain();
            this.brain.init();
            this.id = UUID.randomUUID().toString();
        }

        public Player() {
            this.createBrain();
            this.brain.init();
            this.id = UUID.randomUUID().toString();
        }

        private void createBrain(){
            MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(noInputs).nOut(30).activation(Activation.RELU).build())
                    .layer(1, new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.MSE).nIn(30).nOut(noOutputs).activation(Activation.SIGMOID).build())
                    .build();
            this.brain = new MultiLayerNetwork(config);
        }
        public float[] act(float[] inputs){
            float[] outputs = this.brain.output(Nd4j.create(new float[][] { inputs })).toFloatVector();
            if(outputs[0] > outputs[1]){
                this.ducking(true);
            }else{
                this.jump();
            }
            return outputs;
        }

        public Player[] crossover(Player parent2){
            Player parent1 = this;

            long numOfWeights = parent1.brain.numParams();

            INDArray weights1 = Nd4j.create(1,numOfWeights);
            INDArray weights2 = Nd4j.create(1,numOfWeights);

            for (int i = 0; i < numOfWeights; i ++) {
                if (i < Math.floor(numOfWeights/2)) {
                    weights1.putScalar(0,i,parent1.brain.params().getScalar(i).getFloat(0));
                    weights2.putScalar(0,i,parent2.brain.params().getScalar(i).getFloat(0));
                } else {
                    weights1.putScalar(0,i,parent2.brain.params().getScalar(i).getFloat(0));
                    weights2.putScalar(0,i,parent1.brain.params().getScalar(i).getFloat(0));
                }
            }
            return new Player[] {new Player(weights1), new Player(weights2)};
        }

        public void mutate(float mutationRate){
            for (int i = 0; i < this.brain.numParams(); i ++) {
                if (Math.random() < mutationRate) {
                    this.brain.params().putScalar(i, Math.random() * 2 - 1);
                }
            }
        }

        public void setScore(int score){
            this.score = score;
        }

        public int getScore(){
            return this.score;
        }

        public String getId(){
            return this.id;
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
            population.act(this.getId(), this.act(this.getInputs()));
        }

        public float[] getInputs(){
            Obstacle closestObstacle = null;
            for (Obstacle obstacle : obstacles) {
                if (obstacle==null) {
                    break;

                }else{
                    closestObstacle = obstacle;
                }
            }
            assert closestObstacle != null;
            return new float[] {this.posY,this.velY,closestObstacle.w,closestObstacle.h,closestObstacle.posX};
        }

        void incrementCounter(){
            lifespan++;
            if(lifespan % 3 == 0){
                score += 1;
            }
        }
    }
    class Population {
        private final RouletteWheelSelection geneticAlgorithm = new RouletteWheelSelection();
        private List<Player> creatures ;
        private int noOfPlayers = 30;

        public List<Player> firstPopulation() {
            creatures = Stream.generate(Player::new).limit(noOfPlayers).collect(Collectors.toList());
            //System.out.println(player.getId());
            return creatures;
        }

        public void nextPopulation() {
            List<Player> deadPlayers = new ArrayList<>(creatures);
            creatures.clear();

            for (int i = 0; i < deadPlayers.size() / 2; i++) {
                List<Player> parents = geneticAlgorithm.select(deadPlayers, true, 2, new Random());

                Player[] children = parents.get(0).crossover(parents.get(1));

                children[0].mutate((float) 0.05);
                children[1].mutate((float) 0.05);

                for (Player player : children) {
                    System.out.println(player.getId());
                }

                creatures.addAll(Arrays.asList(children));
            }
        }

        public float[] act(String id, float[] inputs) {
            Player player = getById(id);
            return player.act(inputs);
        }

        public void updateScore(String id, int score) {
            Player player = getById(id);
            player.setScore(score);
        }

        public Player getById(String id) {
            return creatures.stream().filter(creature -> creature.getId().equals(id)).findFirst().get();
        }

    }
    public static void main(String[] args) {
        String[] procArgs = {"Game"};
        DinoGoogle app = new DinoGoogle();
        PApplet.runSketch(procArgs, app);
    }
}
