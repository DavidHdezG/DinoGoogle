package ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Population {
    private final RouletteWheelSelection geneticAlgorithm=new RouletteWheelSelection();
    private final List<Dino> creatures= new ArrayList<>();
    private final int noOfPlayers = 100;

    public List<Dino> firstPopulation() {

        for (int i = 0; i < noOfPlayers; i++) {
            creatures.add(new Dino());
        }
        //creatures = Stream.generate(Dino::new).limit(noOfPlayers).collect(Collectors.toList());
        return creatures;
    }

    public List<Dino> nextPopulation() {
        List<Dino> deadPlayers = new ArrayList<>(creatures);
        creatures.clear();

        for (int i = 0; i < deadPlayers.size()/2; i ++) {
            List<Dino> parents = geneticAlgorithm.select(deadPlayers, true, 2, new Random());

            Dino[] children = parents.get(0).crossover(parents.get(1));

            children[0].mutate((float) 0.05);
            children[1].mutate((float) 0.05);

            creatures.addAll(Arrays.asList(children));
        }
        return creatures;
    }
}
