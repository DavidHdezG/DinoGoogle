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

public class Dino {
    private MultiLayerNetwork brain;
    private int noInputs=6;
    private int noOutputs=3;
    float posY = 0;
    float velY = 0;
    float gravity = (float) 1.2;
    int size = 20;
    boolean duck = false;
    boolean dead = false;

    public int runCount = -5;
    public int lifespan;
    public int score;
    public int gene;

    Dino(){
        posY = 0;
        this.createBrain();
        this.brain.init();
    }

    Dino(INDArray weights){
        posY = 0;
        this.createBrain();
        this.brain.init(weights,true);
    }

    void jump(){
        if(posY == 0){
            gravity = (float) 1.2;
            velY = 16;
        }
    }

    void show(){


        if(!dead){
            if(duck && posY == 0){
                if(runCount < 0){
                    Game.processing.image(Game.dinoDuck, Game.playerXpos - Game.dinoDuck.width / 2, Game.processing.height - Game.groundHeight - (posY + Game.dinoDuck.height));
                }
                else{
                    Game.processing.image(Game.dinoDuck1, Game.playerXpos - Game.dinoDuck1.width / 2, Game.processing.height - Game.groundHeight - (posY + Game.dinoDuck1.height));
                }
            }
            else{
                if(posY == 0){
                    if(runCount < 0){
                        Game.processing.image(Game.dinoRun1, Game.playerXpos - Game.dinoRun1.width / 2, Game.processing.height - Game.groundHeight - (posY + Game.dinoRun1.height));
                    }
                    else{
                        Game.processing.image(Game.dinoRun2, Game.playerXpos - Game.dinoRun2.width / 2, Game.processing.height - Game.groundHeight - (posY + Game.dinoRun2.height));
                    }
                }
                else{
                    Game.processing.image(Game.dinoJump, Game.playerXpos -Game. dinoJump.width / 2, Game.processing.height - Game.groundHeight - (posY + Game.dinoJump.height));
                }
            }
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

        for(int i = 0; i < Game.obstacles.size(); i++){
            if(dead){
                if(Game.obstacles.get(i).collided((float) Game.playerXpos, posY + Game.dinoDuck.height / 2, (float) (Game.dinoDuck.width * 0.5), Game.dinoDuck.height)){
                    dead = true;
                }
            }
            else{
                if(Game.obstacles.get(i).collided((float) Game.playerXpos, posY + Game.dinoRun1.height / 2, (float) (Game.dinoRun1.width * 0.5), Game.dinoRun1.height)){
                    dead = true;
                }
            }
        }

        for(int i = 0; i < Game.birds.size(); i++){
            if(duck && posY == 0){
                if(Game.birds.get(i).collided((float) Game.playerXpos, posY + Game.dinoDuck.height / 2, (float) (Game.dinoDuck.width * 0.5), Game.dinoDuck.height)){
                    dead = true;
                }
            }
            else{
                if(Game.birds.get(i).collided((float) Game.playerXpos, posY + Game.dinoRun1.height / 2, (float) (Game.dinoRun1.width * 0.5), Game.dinoRun1.height)){
                    dead = true;
                }
            }
        }
    }

    private void createBrain(){
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .list()
                .layer(0, new DenseLayer.Builder().nIn(noInputs).nOut(14).activation(Activation.RELU).build()) //RELU
                .layer(1, new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).nIn(14).nOut(noOutputs).activation(Activation.SIGMOID).build())  // SIGMOID
                .build();
        this.brain = new MultiLayerNetwork(config);
    }
    public void act(float[] inputs){
        float[] outputs = this.brain.output(Nd4j.create(new float[][] { inputs })).toFloatVector();
        if(outputs[0] > outputs[1]&& outputs[0] > outputs[2]){

            this.jump();
        }else if(outputs[1] > outputs[0] && outputs[1] > outputs[2]){

            this.ducking(true);
        }else{

            this.ducking(false);
        }
        // Soltar S si esta agachado
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

    public int getFitness() {
        return score;
    }

    public void setFitness(int score) {
        this.score = score;
    }
    public Dino[] crossover(Dino parent2) {
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

    public void mutate(float v) {
        for (int i = 0; i < this.brain.numParams(); i ++) {
            if (Math.random() < v) {
                this.brain.params().putScalar(i, Math.random() * 2 - 1);
            }
        }
    }
}
