package ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Population population = new Population();
        List<Player> pl= population.firstPopulation();
        for (int i = 0; i < 10; i++) {
            population.act(pl.get(i).getId(), new float[]{new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()});
            population.updateScore(pl.get(i).getId(), new Random().nextInt(100));
            population.nextPopulation();
        }
    }
}