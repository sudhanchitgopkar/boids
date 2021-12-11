import ea.Individual;
import ea.Selector;
import implementations.ProportionalSelector;
import implementations.BoidPopulation;
import implementations.BoidIndividual;

int fit;

final int FRAME_LIMIT = 500;
final int POPULATION_SIZE = 10;  //MUST BE SAME IN BoidIndividual   
final int MAX_GENERATIONS = 50;
final int NUM_TRIALS = 1;       //MUST BE SAME IN BoidIndividual
final double[] PREY_GENOME = {1.5, 1.5, 1.0, 1.0};

boolean doneSetup = false;
int generationCounter = 0;
int trialCounter = 0;
int simulationCounter = 0;

BoidIndividual[] predatorBoids;
ProportionalSelector selector;
BoidPopulation predatorPopulation;
BoidIndividual[] predatorResults;

Sim s;
    
    public void settings() {
      size(500,500);
      //fullScreen();
    } 
    
    void setup() {
      // Uncomment below for zoomies
      frameRate(100000000);
      if (!doneSetup) {
            GA_Setup();
            // shuffle the populations
            predatorPopulation.shuffle();
           // preyPopulation.shuffle();

            doneSetup = true;
        }

        if (simulationCounter == 0) {
            // create first sim
            s = new Sim(predatorPopulation.at(0).getGenome(), PREY_GENOME);
        }

        if (generationCounter >= MAX_GENERATIONS) {
            exit();
        }
    } 

    void GA_Setup() {
        predatorBoids = new BoidIndividual[POPULATION_SIZE];
       // preyBoids = new BoidIndividual[POPULATION_SIZE];
       
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            predatorBoids[i] = new BoidIndividual();
           // preyBoids[i] = new BoidIndividual();
        }
        
        // Create selector and populations
        selector = new ProportionalSelector();
        predatorPopulation = new BoidPopulation(predatorBoids);
       // preyPopulation = new BoidPopulation(preyBoids);
        

        predatorResults = new BoidIndividual[MAX_GENERATIONS];
       // preyResults = new Individual[MAX_GENERATIONS];
       
        System.out.println("PREDATOR SETUP");
        for (int i = 0; i < POPULATION_SIZE; i++) {
            System.out.println(predatorPopulation.at(i).printGenome());
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
          
            text("Pred Genome: " + predatorPopulation.at(simulationCounter).printGenome(), 5, 470);
           // text("Prey Genome: " + preyPopulation.at(simulationCounter).printGenome(), 5, 485);
        } else {
          if (simulationCounter < POPULATION_SIZE) {
            // simulation has ended
            // assign trial fitnesses
            fit = s.prey.getBoids().size();
            
            predatorPopulation.at(simulationCounter).setTrial(trialCounter, 100 - fit);
            // preyPopulation.at(simulationCounter).setTrial(trialCounter, fit);

            // reset fit, and increment simulation counter
            fit = 0;
            simulationCounter++;

            // reeset simulation
            background(255,0,0);
            frameCount = 0;
            if (simulationCounter <= POPULATION_SIZE - 1) 
                s = new Sim(predatorPopulation.at(simulationCounter).getGenome(),PREY_GENOME);
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
            predatorPopulation.runGeneration(selector);
           // preyPopulation.runGeneration(selector);
            
            System.out.println("GENERATION: " + generationCounter);
            System.out.println("PREDATOR");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                System.out.println(predatorPopulation.at(i).printGenome());
            }
            System.out.println("Avg Fit: " + predatorPopulation.avgFitness());
            System.out.println("Max Fit: " + predatorPopulation.maxFitness());
            BoidIndividual bestPred = predatorPopulation.getBestIndividual();
            System.out.println("Best Indiv: " + bestPred.printGenome() + "\n");
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
