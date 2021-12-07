import ea.Individual;
import ea.Selector;
import implementations.ProportionalSelector;
import implementations.BoidPopulation;
import implementations.BoidIndividual;

int fit;

final int FRAME_LIMIT = 500;
final int POPULATION_SIZE = 2;    
final int RUNS = 1;
final int MAX_GENERATIONS = 50;
final int NUM_TRIALS = 3;

boolean doneSetup = false;
int generationCounter = 1;
int trialCounter = 0;
int simulationCounter = 0;

Individual[] predatorBoids;
Individual[] preyBoids;
ProportionalSelector selector;
BoidPopulation predatorPopulation;
BoidPopulation preyPopulation;
Individual[] predatorResults;
Individual[] preyResults;

Sim s;
    
    public void settings() {
      size(500,500);
      //fullScreen();
    } 
    
    void setup() {
        if (!doneSetup) {
            GA_Setup();
            // shuffle the populations
            predatorPopulation.shuffle();
            preyPopulation.shuffle();

            doneSetup = true;
        }

        if (simulationCounter == 0) {
            // create first sim
            s = new Sim(predatorPopulation.getIndividual(0).getGenome(), preyPopulation.getIndividual(0).getGenome());
        }

        if (generationCounter >= MAX_GENERATIONS) {
            exit();
        }
    } 

    void GA_Setup() {
        predatorBoids = new Individual[POPULATION_SIZE];
        preyBoids = new Individual[POPULATION_SIZE];
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            predatorBoids[i] = new BoidIndividual();
            preyBoids[i] = new BoidIndividual();
        }
        
        // Create selector and populations
        selector = new ProportionalSelector();
        predatorPopulation = new BoidPopulation(predatorBoids);
        preyPopulation = new BoidPopulation(preyBoids);
        

        predatorResults = new Individual[MAX_GENERATIONS];
        preyResults = new Individual[MAX_GENERATIONS];
    }
    
    void drawHandler() {
    
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        if (frameCount < FRAME_LIMIT) {
            // performing a simulation
            s.exec();
        } else {
          if (simulationCounter < POPULATION_SIZE) {
            // simulation has ended
            // assign trial fitnesses
            fit = s.prey.getBoids().size();
            
            predatorPopulation.getIndividual(simulationCounter).setTrial(trialCounter, 100 - fit);
            preyPopulation.getIndividual(simulationCounter).setTrial(trialCounter, fit);

            // reset fit, and increment simulation counter
            fit = 0;
            simulationCounter++;

            // reeset simulation
            background(255,0,0);
            frameCount = 0;
            if (simulationCounter <= POPULATION_SIZE - 1) 
                s = new Sim(predatorPopulation.getIndividual(simulationCounter).getGenome(), preyPopulation.getIndividual(simulationCounter).getGenome());
          } else {
            // END OF TRIAL
            // reset simulation counter
            simulationCounter = 0;
            // increment trial counter
            trialCounter++;

            // shuffle populations
            predatorPopulation.shuffle();
            preyPopulation.shuffle();

            // reset sim stuff
            text("Trial: " + trialCounter + "\nGeneration: " + generationCounter, 50,50);
            background(255,0,0);
            frameCount = 0;
          }
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        
    // ###### CODE FOR GENERATION FOR LOOP ###### // 
          if (trialCounter >= NUM_TRIALS) {
            // only perform runGeneration after the set number of trials have been completed
            predatorPopulation.runGeneration(selector);
            preyPopulation.runGeneration(selector);
            
            System.out.println(predatorPopulation.getIndividual(0).printGenome());
            System.out.println(preyPopulation.getIndividual(0).printGenome());

            // reset trial counter
            trialCounter = 0;

            // increase generation counter
            generationCounter++;
          }

          setup();
    // ###### CODE FOR GENERATION FOR LOOP ###### // 

        }
    } //drawHandler

    void draw() {
        //System.out.println("HERE");
        drawHandler();
    }


/*
public class Runner {
    public static void main(String[] args) {
        // create instance of BoidEvolution
        BoidEvolution boidEvolution = new BoidEvolution();
        boidEvolution.run();
    }
}

*/
