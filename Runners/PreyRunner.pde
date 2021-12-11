import ea.Individual;
import ea.Selector;
import implementations.ProportionalSelector;
import implementations.BoidPopulation;
import implementations.BoidIndividual;

int fit;

final int FRAME_LIMIT = 500;
final int POPULATION_SIZE = 10;    //MUST BE SAME IN BoidIndividual
final int MAX_GENERATIONS = 10;
final int NUM_TRIALS = 5;
final double[] PRED_GENOME = {1.5, 1.5, 1.0, 1.0};

boolean doneSetup = false;
int generationCounter = 0;
int trialCounter = 0;
int simulationCounter = 0;

BoidIndividual[] preyBoids;
ProportionalSelector selector;
BoidPopulation preyPopulation;
BoidIndividual[] preyResults;

Sim s;
    
    public void settings() {
      size(500,500);
      //fullScreen();
    } 
    
    void setup() {
      // Uncomment below for zoomies
      // frameRate(100000000);
        if (!doneSetup) {
            GA_Setup();
            // shuffle the populations
           // predatorPopulation.shuffle();
            preyPopulation.shuffle();

            doneSetup = true;
        }

        if (simulationCounter == 0) {
            // create first sim
            s = new Sim(PRED_GENOME, preyPopulation.at(0).getGenome());
        }

        if (generationCounter >= MAX_GENERATIONS) {
            exit();
        }
    } 

    void GA_Setup() {
       // predatorBoids = new Individual[POPULATION_SIZE];
        preyBoids = new BoidIndividual[POPULATION_SIZE];
       
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
           // predatorBoids[i] = new BoidIndividual();
            preyBoids[i] = new BoidIndividual();
        }
        
        // Create selector and populations
        selector = new ProportionalSelector();
        //predatorPopulation = new BoidPopulation(predatorBoids);
        preyPopulation = new BoidPopulation(preyBoids);
        

        //predatorResults = new Individual[MAX_GENERATIONS];
        preyResults = new BoidIndividual[MAX_GENERATIONS];
        System.out.println("PREY SETUP");
        System.out.println("----------------------------------------------");
        for (int j = 0; j < POPULATION_SIZE; j++) {
            System.out.println(preyPopulation.at(j).printGenome());
        }
        System.out.println("----------------------------------------------");
    }
    
    void drawHandler() {
    
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        if (frameCount < FRAME_LIMIT) {
            // performing a simulation
            s.exec();
            text("Frame Count: " + frameCount, 10, 15);
            
            text("Gen Count: " + generationCounter, 400, 15);
            text("Trial Count: " + trialCounter, 400, 30);
            text("Sim Count: " + simulationCounter, 400, 45);
            text("Prey Genome: " + preyPopulation.at(simulationCounter).printGenome(), 5, 485);
        } else {
          if (simulationCounter < POPULATION_SIZE) {
            // simulation has ended
            // assign trial fitnesses
            fit = s.prey.getBoids().size();
            
           // predatorPopulation.getIndividual(simulationCounter).setTrial(trialCounter, 100 - fit);
            preyPopulation.at(simulationCounter).setTrial(trialCounter, fit);

            // reset fit, and increment simulation counter
            fit = 0;
            simulationCounter++;

            // reeset simulation
            background(255,0,0);
            frameCount = 0;
            if (simulationCounter <= POPULATION_SIZE - 1) 
                s = new Sim(PRED_GENOME, preyPopulation.at(simulationCounter).getGenome());
            }
            if (simulationCounter == POPULATION_SIZE) {
              // END OF TRIAL
              // reset simulation counter
              simulationCounter = 0;
              // increment trial counter
              trialCounter++;

              // shuffle populations
              // predatorPopulation.shuffle();
              // preyPopulation.shuffle();

              // reset sim stuff
              // text("Trial: " + trialCounter + "\nGeneration: " + generationCounter, 50,50);
              background(255,0,0);
              frameCount = 0;
          }
    // ###### CODE FOR SIMULATION FOR LOOP ###### //
        
    // ###### CODE FOR GENERATION FOR LOOP ###### // 
          if (trialCounter >= NUM_TRIALS) {
            // only perform runGeneration after the set number of trials have been completed
           // predatorPopulation.runGeneration(selector);
            preyPopulation.runGeneration(selector);
            
            System.out.println("GENERATION: " + generationCounter);
            System.out.println("PREY");
            for (int j = 0; j < POPULATION_SIZE; j++) {
                System.out.println(preyPopulation.at(j).printGenome());
            }
            System.out.println("Avg Fit: " + preyPopulation.avgFitness());
            System.out.println("Max Fit: " + preyPopulation.maxFitness());
            BoidIndividual bestPrey = preyPopulation.getBestIndividual();
            System.out.println("Best Indiv: " + bestPrey.printGenome());
            System.out.println("----------------------------------------------");

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
