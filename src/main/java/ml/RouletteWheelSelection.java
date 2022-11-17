package ml;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * <p>Implements selection of <i>n</i> candidates from a population by selecting
 * <i>n</i> candidates at random where the probability of each candidate getting
 * selected is proportional to its fitness score.  This is analogous to each
 * candidate being assigned an area on a roulette wheel proportionate to its fitness
 * and the wheel being spun <n>i</n> times.  Candidates may be selected more than
 * once.</p>
 *
 * <p>In some instances, particularly with small population sizes, the randomness
 * of selection may result in excessively high occurrences of particular candidates.
 * If this is a problem,  provides an alternative
 * fitness-proportionate strategy for selection.</p>
 *
 * @author Daniel Dyer
 */

public class RouletteWheelSelection
{
    /**
     * Selects the required number of candidates from the population with
     * the probability of selecting any particular candidate being proportional
     * to that candidate's fitness score.  Selection is with replacement (the same
     * candidate may be selected multiple times).
     *
     * @param population           The candidates to select from.
     * @param naturalFitnessScores True if higher fitness scores indicate fitter
     *                             individuals, false if lower fitness scores indicate fitter individuals.
     * @param selectionSize        The number of selections to make.
     * @param rng                  A source of randomness.
     * @return The selected candidates.
     */
    public List<Main.Dino> select(List<Main.Dino> population,
                                  boolean naturalFitnessScores,
                                  int selectionSize,
                                  Random rng)
    {
        // Record the cumulative fitness scores.  It doesn't matter whether the
        // population is sorted or not.  We will use these cumulative scores to work out
        // an index into the population.  The cumulative array itself is implicitly
        // sorted since each element must be greater than the previous one.  The
        // numerical difference between an element and the previous one is directly
        // proportional to the probability of the corresponding candidate in the population
        // being selected.
        double[] cumulativeFitnesses = new double[population.size()];
        cumulativeFitnesses[0] = getAdjustedFitness(population.get(0).getFitness(),
                naturalFitnessScores);
        for (int i = 1; i < population.size(); i++)
        {
            double fitness = getAdjustedFitness(population.get(i).getFitness(),
                    naturalFitnessScores);
            cumulativeFitnesses[i] = cumulativeFitnesses[i - 1] + fitness;
        }

        List<Main.Dino> selection = new ArrayList<>(selectionSize);
        for (int i = 0; i < selectionSize; i++)
        {
            double randomFitness = rng.nextDouble() * cumulativeFitnesses[cumulativeFitnesses.length - 1];
            int index = Arrays.binarySearch(cumulativeFitnesses, randomFitness);
            if (index < 0)
            {
                // Convert negative insertion point to array index.
                index = Math.abs(index + 1);
            }
            selection.add(population.get(index));
        }
        return selection;
    }

    // MANEJAR CUANDO ES 0, creo que siempre usa 0 como distancia

    private double getAdjustedFitness(double rawFitness,
                                      boolean naturalFitness)
    {
        if (naturalFitness)
        {
            return rawFitness;
        }
        else
        {
            // If standardised fitness is zero we have found the best possible
            // solution.  The evolutionary algorithm should not be continuing
            // after finding it.
            return rawFitness == 0 ? Double.POSITIVE_INFINITY : 1 / rawFitness;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Roulette Wheel Selection";
    }
}
