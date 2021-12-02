import ea.Individual;
import ea.Selector;
import implementations.ProportionalSelector;
import implementations.BoidPopulation;
import implementations.BoidIndividual;

import java.util.Arrays;


public class BoidEvolution {
    // CONSTANTS
    protected static final int POPULATION_SIZE = 5;    
    protected static final int RUNS = 1;
    protected static final int MAX_GENERATIONS = 10;

    public void setup() {
        noLoop();
    }

    // Why make global variables when we have noLoop() in setup()??????

    public void draw() {
        // First we initialize all the individuals.
        Individual[] predatorBoids = new Individual[POPULATION_SIZE];
        Individual[] preyBoids = new Individual[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            predatorBoids[i] = new BoidIndividual();
            preyBoids[i] = new BoidIndividual();
        }
        
        // Create selector and populations
        ProportionalSelector selector = new ProportionalSelector();
        BoidPopulation predatorPopulation = new BoidPopulation(predatorBoids);
        BoidPopulation preyPopulation = new BoidPopulation(preyBoids);

        Individual[] predatorResults = new Individual[MAX_GENERATIONS];
        Individual[] preyResults = new Individual[MAX_GENERATIONS];

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            
            //#################### 
            // START COEVOLUTION
            //#################### 
            
            // perform for number of trials
            for (int trial = 0; trial < BoidIndividual.getNumTrials(); trial++) {
                // shuffle all individuals
                predatorPopulation.shuffle();
                preyPopulation.shuffle();
    
    
                // simulate competitions/trials between matching predator and prey
                for (int i = 0; i < POPULATION_SIZE; i++) {
                    Simulation sim = new Simulation(predatorPopulation.getIndividual(i).getGenome(), preyPopulation.getIndividual(i).getGenome());
                    sim.draw();
                    Double simResult = sim.TRIAL_RESULT;
    
                    // posibly manipulatae simResult if not handled in sim.simulate()
    
                    // update trial fitnesses; prey and predator fitnesses are opposite
                    predatorPopulation.getIndividual(i).setTrial(trial, 100 - simResult);
                    preyPopulation.getIndividual(i).setTrial(trial, simResult);
                    
                }
            }
    
    
            //####################
            // END COEVOLUTION
            //####################
    
            // Update individual populations
            predatorPopulation.runGeneration(selector);
            preyPopulation.runGeneration(selector);
    
            predatorResults[generation] = predatorPopulation.getBestIndividual(); 
            preyResults[generation] = preyPopulation.getBestIndividual();
        }

        // Print Results
        System.out.println("Predator Results:");
        for (int i = 0; i < MAX_GENERATIONS; i++) {
            BoidIndividual ind = (BoidIndividual)predatorResults[i];
            System.out.println("Generation " + i + ": " + Arrays.toString(ind.getGenome()));
        }

        System.out.println("Prey Results:");
        for (int i = 0; i < MAX_GENERATIONS; i++) {
            BoidIndividual ind = (BoidIndividual)preyResults[i];
            System.out.println("Generation " + i + ": " + Arrays.toString(ind.getGenome()));
        }


    }
}
