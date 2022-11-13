package ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Population {
    private RouletteWheelSelection geneticAlgorithm = new RouletteWheelSelection();
    private List<Player> creatures ;
    private int noOfPlayers = 30;

    public List<Player> firstPopulation() {
        creatures = Stream.generate(Player::new).limit(noOfPlayers).collect(Collectors.toList());
        for (Player player : creatures) {
            //System.out.println(player.getId());
        }
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
