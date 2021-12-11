package implementations;

import java.util.Random;
import java.lang.Math;
import java.util.Arrays;

import ea.Individual;

public class BoidIndividual {
    // CONSTANTS
    protected final static double MAX_GENE_RANGE = 5.0;
    protected final static double MIN_GENE_RANGE = 1.0;

    protected final static double POP_SIZE = 10.0;
  	protected final static int DIMENSION = 4;
    protected static final int NUM_TRIALS = 1; // Must be <= POPULATION_SIZE in BoidEvolution, may need better way to set
  	protected final static double MUTATION_PROBABILITY = 1.0 / POP_SIZE;
  	protected final static double CROSSOVER_PROBABILITY = 1.0;

    // Individual's representation ("genotype")
    // Genes are: SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT in that order
    protected double[] genome;

    // Individual's fitness trials - used to calculate individual's total fitness
    protected double[] trials;

    // Flag for whether fitness has been calculated
    // NOT SURE IF NEEDED/SHOULD BE USED IN COEVOLUTION!!!
    protected double totalFitness;
    protected boolean fitnessNeedsUpdate = true;

    // constructor
    public BoidIndividual() {
        // initialize genotype
        genome = new double[DIMENSION];
        Random r = new Random();
        for (int i = 0; i < DIMENSION; i++) {
            genome[i] = MIN_GENE_RANGE + (MAX_GENE_RANGE - MIN_GENE_RANGE) * r.nextDouble();
        }

        // initialize trials
        trials = new double[NUM_TRIALS];
        totalFitness = 0.0;
    }


    protected BoidIndividual(double[] genome) {
        this.genome = genome;
        trials = new double[NUM_TRIALS];
        for (int i = 0; i < NUM_TRIALS; i++) {
            trials[i] = 0.0;
        }
    }

    // Individual's fitness function - calculates fitness over all trials
    public double fitness() {
        // check if fitness needs to be updated (REMOVE???)
        if (fitnessNeedsUpdate) {
            totalFitness = 0.0;
            for (int i = 0; i < NUM_TRIALS; i++) {
                totalFitness += trials[i];
            }
            fitnessNeedsUpdate = true;
        }

        totalFitness = totalFitness / NUM_TRIALS;
        // return total fitness 
        return totalFitness;
    }

    // Individual's crossover function - returns a new individual
    public BoidIndividual crossover(BoidIndividual indiv) {
        // There is a CROSSOVER_PROBABILITY chance of
		// crossover occurring; if it does not occur then
        // parent with higher fitness is returned

        // check if individuals are of the same type
        if (!(indiv instanceof BoidIndividual)) {
            throw new IllegalArgumentException("Cannot crossover with a different type of individual");
        }

        // convert indiv to BoidIndividual
        BoidIndividual other = indiv;

        // perform crossover (randomly)
        if (Math.random() < CROSSOVER_PROBABILITY) {
            double[] childGenome = new double[DIMENSION];

            // perform mutation 
  			for (int i = 0; i < DIMENSION; i++) {
  				
  				double alpha = Math.random();
  				childGenome[i] = mix(genome[i], other.genome[i], alpha);
  				
  			}

            // return new individual
            return new BoidIndividual(childGenome);
        } else {
            // return parent with the higher fitness
            if (fitness() > other.fitness()) {
                return this;
            } else {
                return other;
            }
        }
    }

    // Individual's mutation function
    public void mutate() {
        // Uniform random mutation within mutation radius.
		// MUTATION_PROBABILITY is the probability that any
		// given entry in this individual's genotype will be
		// mutated.

        for (int i = 0; i < DIMENSION; i++) {

            Random r = new Random();
            if (r.nextDouble() < MUTATION_PROBABILITY) {
                double newValue = MIN_GENE_RANGE + (MAX_GENE_RANGE - MIN_GENE_RANGE) * r.nextDouble();
                genome[i] = newValue;

                // reset fitness flag since fitness has changed
                fitnessNeedsUpdate = true;
            }
        }
    }

    // NUM_TRIALS getter
    public static int getNumTrials() { return NUM_TRIALS; }

    // genotype getter
    public double[] getGenome() { return genome; }
    
    public String printGenome() {
      String s = "[ ";
      for (int i = 0; i < genome.length; i++) {
         Double roundedGene = Math.round(genome[i] * 100.0) / 100.0;
         s += roundedGene + " ";
      }
       return s + "]";
    } 

    // trial setter
    public void setTrial(int trial, double fitness) {
        trials[trial] = fitness;
    }

    // Intermeiate crossover
	protected double mix(double start, double end, double t) { return end * t + start * (1.0 - t); }
}
