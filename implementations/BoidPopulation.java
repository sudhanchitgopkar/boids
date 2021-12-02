package implementations;
import ea.Individual;
import ea.Population;
import java.util.Random;

public class BoidPopulation extends Population {

    public BoidPopulation(Individual[] population) {
        super(population);
    }

    // getter for individual
    public BoidIndividual getIndividual(int index) {
        return (BoidIndividual) pop[index];
    }

    // shuffle the population
    // Implementing Fisher–Yates shuffle
    public void shuffle() {
        int index;
        Individual temp;
        Random random = new Random();
        for (int i = pop.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = pop[index];
            pop[index] = pop[i];
            pop[i] = temp;
        }
    }  
    
}
