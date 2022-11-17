package ml;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import processing.core.PApplet;

public class Main extends PApplet {
    int generation = 0;
    Game game = new Game();

    int every_sec=0;

    public void settings() {
        size(1280, 720);
    }
    public void setup(){

        frameRate(60);
    }

    public void draw(){
        background(0);
        game.update();
        game.display();
        if(millis()-every_sec>2000){
            every_sec=millis();
            game.spawnEnemy();
        }
    }
    public static void main(String[] args) {
        String[] procArgs = {"Game"};
        Main app = new Main();
        PApplet.runSketch(procArgs, app);
    }
    public void keyPressed(){
        if(key == CODED){
            if(keyCode == UP){
                System.out.println("as");
                game.keyPressed("UP");
            }else if(keyCode == DOWN){
                game.keyPressed("DOWN");
            }
        }
    }

    class Game{
        Population population=new Population();
        int speed = 12;
        public List<Dino> dinos = new ArrayList<>();
        Dino player;
        public ArrayList<Cactus> cactus;
        public ArrayList<Bird> bird;
        Game() {
            dinos=population.firstPopulation();
            player = new Dino();
            cactus = new ArrayList<Cactus>();
            bird = new ArrayList<Bird>();
        }

        void update() {
            for(Dino player:dinos){
                if(player.isAlive()){
                    player.update();

                }
            }
            for (Cactus c : cactus) {
                c.update(speed);
            }
            for (Bird b : bird) {
                b.update(speed);
            }
            checkCollision();
            speed+=0.01;

            int lessDistance = 2000;
            Cactus lessCactus = null;
            Bird lessBird = null;
            int less=0;
            for (Cactus c:cactus){
                if((c.x-280)<lessDistance&& !c.passed) {
                    lessDistance = c.x - 280;
                    lessCactus = c;
                    less=1;
                }
            }
            for (Bird b:bird){
                if((b.x-280)<lessDistance && !b.passed) {
                    lessDistance = b.x - 280;
                    lessBird = b;
                    less=2;
                }
            }

            if(lessDistance < 0){
                lessDistance = 2000;
            }

            if(less==1){
                for(Dino dino:dinos){
                    dino.act(new float[]{lessDistance,lessCactus.x,lessCactus.y,lessCactus.w,lessCactus.h,dino.y,speed});
                }
            }else if(less==2){
                for(Dino dino:dinos){
                    dino.act(new float[]{lessDistance,lessBird.x,lessBird.y,lessBird.w,lessBird.h,dino.y,speed});
                }
            } else{
                for(Dino dino:dinos){
                    dino.act(new float[]{lessDistance,0,0,0,0,dino.y,speed});
                }
            }

            //System.out.println(lessDistance);
            /*for (Dino dino : dinos) {
                int lessDistanceCactus=50000;
                Cactus auxCactus=null;
                Bird auxBird=null;
                int lessDistanceBird=50000;
                for (Cactus c : cactus) {
                    if(c.calculateDistance(dino)<lessDistanceCactus){
                        lessDistanceCactus=c.calculateDistance(dino);
                        auxCactus=c;
                    }

                }
                for (Bird b : bird) {
                    if(b.calculateDistance(dino)<lessDistanceBird){
                        lessDistanceBird=b.calculateDistance(dino);
                        auxBird=b;

                    }
                }

                if(auxCactus!=null){

                    if(auxBird==null){
                        dino.act(new float[]{auxCactus.x,auxCactus.y,auxCactus.w,auxCactus.h,dino.y,speed});
                        System.out.println("Cactus 1 ");
                    }else if(lessDistanceBird<lessDistanceCactus){
                        dino.act(new float[]{auxBird.x,auxBird.y,auxBird.w,auxBird.h,dino.y,speed});
                        System.out.println("Cactus 2 ");
                    }else{
                        dino.act(new float[]{auxCactus.x,auxCactus.y,auxCactus.w,auxCactus.h,dino.y,speed});
                        System.out.println("Cactus 3 ");
                    }
                }else {
                    if(auxBird!=null){
                        dino.act(new float[]{auxBird.x,auxBird.y,auxBird.w,auxBird.h,dino.y,speed});
                    }else{
                        System.out.println("No hay obstaculos");
                        dino.act(new float[]{0,0,0,0,dino.y,speed});
                    }
                }
            }*/
        }

        void display() {
            strokeWeight(2);
            stroke(255, 0, 0);
            line(0, 450 + 86, width, 450 + 86);
            noStroke();
            for (Dino player:dinos){
                if(player.isAlive()){
                    player.display();
                }
            }
            for (Cactus c : cactus) {
                c.display();
            }
            for (Bird b : bird) {
                b.display();
            }

        }

        void keyPressed(String key) {
            if (key.equals("UP")) {
                for (Dino player : dinos) {

                        player.jump();

                }
            } else if (key.equals("DOWN")) {
                if (player.isJumping()) {
                    player.stop_jump();
                }
            }
        }

        void spawnEnemy() {
            if ((int)random(10) <= 4) {
                bird.add(new Bird());
            }else{
                cactus.add(new Cactus());
            }


        }
        void checkCollision() {
            for (Dino player : dinos) {
                int p_x = player.x;
                int p_y = player.y;
                int p_w = player.w;
                int p_h = player.h;
                for (Cactus c : cactus) {
                    if (c.x > p_x + p_w) {
                        break;
                    }
                    if (p_x + p_w > c.x && p_x < c.x + c.w && p_y + p_h > c.y) {
                        dinoDied(player);
                        //speed = 0;
                    }
                }
                for (Bird b : bird) {
                    if (b.x > p_x + p_w) {
                        break;
                    }
                    if (p_x + p_w > b.x && p_x < b.x + b.w && p_y + p_h > b.y && p_y < b.y + b.h) {
                        dinoDied(player);
                        //speed = 0;
                    }
                }
            }




        }
        void dinoDied(Dino dino){
            //dinos.remove(dino);
            boolean allDead=false;
            dino.die();

            for (Dino player : dinos) {
                if(player.isAlive()){
                    allDead=false;
                    break;
                }else{
                    allDead=true;
                }
            }

            if(allDead){
                resetGame();
                //population.nextPopulation();
            }
        }
        void resetGame(){
            speed=12;
            cactus=new ArrayList<Cactus>();
            bird=new ArrayList<Bird>();
            generation++;
            System.out.println("Generación: "+generation);
            dinos=population.nextPopulation();
           /* for (Dino player : dinos) {
                player.alive=true;
            }*/

        }
    }


    class Dino{
        private String id;
        private MultiLayerNetwork brain;
        private int noInputs=7;
        private int noOutputs=2;
        int x,y;
        int w,h;
        boolean jumping;
        float jump_stage;
        boolean alive=true;
        int fitness=0;

        Dino() {
            x = 200;
            y = 450;
            w = 80;
            h = 86;
            this.createBrain();
            this.brain.init();
            this.id = UUID.randomUUID().toString();
        }
        Dino(INDArray weights){
            x = 200;
            y = 450;
            w = 80;
            h = 86;
            this.createBrain();
            this.brain.init(weights,true);
            this.id = UUID.randomUUID().toString();
        }
        private void createBrain(){
            MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(noInputs).nOut(14).activation(Activation.RELU).build()) //RELU
                    .layer(1, new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).nIn(14).nOut(noOutputs).activation(Activation.SIGMOID).build())  // SIGMOID
                    .build();
            this.brain = new MultiLayerNetwork(config);
        }
        public float[] act(float[] inputs){

            float[] outputs = this.brain.output(Nd4j.create(new float[][] { inputs })).toFloatVector();
            if(outputs[0] > outputs[1]){
                this.jump();
            }else{
                this.stop_jump();
            }
            return outputs;
        }

        public Dino[] crossover(Dino parent2){
            Dino parent1 = this;

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
            return new Dino[] {new Dino(weights1), new Dino(weights2)};
        }
        public void mutate(float mutationRate){
            for (int i = 0; i < this.brain.numParams(); i ++) {
                if (Math.random() < mutationRate) {
                    this.brain.params().putScalar(i, Math.random() * 2 - 1);
                }
            }
        }

        float f(float x) {
            return( -4 * x * (x - 1)) * 172;
        }
        void update() {
            fitness+=1;
            if (jumping) {
                y = 448 - (int)f(jump_stage);
                jump_stage += 0.03;
                if (jump_stage > 1) {
                    jumping = false;
                    jump_stage = 0;
                    y = 450;
                }
            }


        }
        public String getId(){
            return this.id;
        }
        void jump() {
            jumping = true;
        }

        void stop_jump(){
            jumping = false;
            jump_stage = 0;
            y = 450;
        }

        void display() {
            rect(x,y,w,h);
        }

        boolean isJumping(){
            return jumping;
        }

        void die(){
            alive = false;
        }
        boolean isAlive(){
            return alive;

        }

        public void setFitness(int score) {
            this.fitness = score;
        }

        public int getFitness() {
            return fitness;
        }
    }
    class Cactus{
        boolean passed=false;
        int distance;
        int x,y,w,h;

        Cactus(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            distance=1350;
        }

        Cactus() {
            x = 1350;
            distance=1350;
            int type = (int)random(6);
            if (type < 3) {
                h = 66;
                y = 470;
            } else {
                h = 96;
                y = 440;
            }

            switch(type) {
                case 0:
                    w = 30;
                    break;
                case 1:
                    w = 64;
                    break;
                case 2:
                    w = 98;
                    break;
                case 3:
                    w = 46;
                    break;
                case 4:
                    w = 96;
                    break;
                case 5:
                    w = 146;
                    break;
            }
        }
        void update(int speed){
            if (!passed) {
                x -= speed;
            }
            if(x< -w){
                passed=true;
            }
        }

        int calculateDistance(Dino dino){
            distance = x - dino.x;
            if(distance<0){
                distance=0;
            }
            return distance;
        }


        void display(){
            fill(255, 0, 0);
            rect(x,y,w,h);
        }
    }
    class Bird{
        boolean passed=false;
        int x,y,w,h;
        int distance=1350;

        Bird(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        Bird() {
            x = 1350;
            w=84;
            h=40;
            y=350;
            int type = (int)random(3);
            if (type == 0) {
                y = 350;
            } else if (type == 1) {
                y = 400;
            } else if (type == 2) {
                y = 450;
            }
            //400
            //450

        }
        void update(int speed){
            if (!passed) {
                x -= speed;
            }

            if(x< -w){
                passed=true;
            }
        }
        int calculateDistance(Dino dino){
            /*if(x-dino.x<0){
                distance=0;}*/

            return distance;
        }
        void display(){
            fill(255, 0, 0);
            rect(x,y,w,h);
        }
    }
    class Population {
        private final RouletteWheelSelection geneticAlgorithm = new RouletteWheelSelection();
        private List<Dino> creatures ;
        private final int noOfPlayers = 100;

        public List<Dino> firstPopulation() {
            creatures = Stream.generate(Dino::new).limit(noOfPlayers).collect(Collectors.toList());
            //System.out.println(player.getId());
            return creatures;
        }

        public List<Dino> nextPopulation() {
            List<Dino> deadPlayers = new ArrayList<>(creatures);
            creatures= new ArrayList<>();
            int maxScore=0;
            for (int i = 0; i < deadPlayers.size() / 2; i++) {
                List<Dino> parents = geneticAlgorithm.select(deadPlayers, true, 2, new Random());
                if(deadPlayers.get(i).getFitness()>maxScore){
                    maxScore=deadPlayers.get(i).getFitness();
                }
                Dino[] children = parents.get(0).crossover(parents.get(1));

                children[0].mutate((float) 0.05);
                children[1].mutate((float) 0.05);
                creatures.addAll(Arrays.asList(children));
            }
            System.out.println("Max score: "+maxScore);
            return creatures;
        }

        public float[] act(String id, float[] inputs) {
            Dino player = getById(id);
            for(float input : inputs){
                System.out.println(input);
            }
            return player.act(inputs);
        }

        public void updateScore(String id, int score) {
            Dino player = getById(id);
            player.setFitness(score);
        }

        public Dino getById(String id) {
            return creatures.stream().filter(creature -> creature.getId().equals(id)).findFirst().get();
        }

    }
}