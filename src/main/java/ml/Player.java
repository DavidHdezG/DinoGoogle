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

import java.util.UUID;

public class Player {
    private String id;
    private MultiLayerNetwork brain;
    private int score;
    private int noInputs=4;
    private int noOutputs=2;
    public Player(){
        this.createBrain();
        this.brain.init();
        this.id = UUID.randomUUID().toString();
    }
    public Player(INDArray weights){
        this.createBrain();
        this.brain.init(weights,true);
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
        return this.brain.output(Nd4j.create(new float[][] { inputs })).toFloatVector();
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
}
