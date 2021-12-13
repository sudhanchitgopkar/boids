import ea.Individual;
import ea.Selector;
import implementations.ESSelector;
import implementations.ESBoidPopulation;
import implementations.ESBoidIndividual;
import implementations.FileWritePopulation;

int fit;

final int FRAME_LIMIT = 500;//TODO 500
final int POPULATION_SIZE = 6; // MUST BE SAME IN BoidIndividual
final int MAX_GENERATIONS = 35;
final int NUM_TRIALS = 5; // MUST BE SAME AS IN BoidIndividual
//manually run 3 times

boolean doneSetup = false;
int generationCounter = 0;
int trialCounter = 0;
int simulationCounter = 0;

ESBoidIndividual[] predatorBoids;
ESBoidIndividual[] preyBoids;
ESSelector selector;
FileWritePopulation predatorPopulation;
FileWritePopulation preyPopulation;
ESBoidIndividual[] predatorResults;
ESBoidIndividual[] preyResults;

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
            preyPopulation.shuffle();

            doneSetup = true;
        }

        if (simulationCounter == 0) {
            // create first sim
            s = new Sim(predatorPopulation.getIndividual(0).getGenome(), preyPopulation.getIndividual(0).getGenome());
        }

        if (generationCounter >= MAX_GENERATIONS) {
          
          String file = "ESBoidRuns";
          String extension = ".txt";
          String filePrey = "Prey" + file + extension;
          String filePredator = "Predator" + file + extension;
          
          predatorPopulation.writeData(filePredator);
          preyPopulation.writeData(filePrey);
            
            System.out.println("code finished");
            exit();
        }
    }

    void GA_Setup() {
      
        ESBoidIndividual[] initPredator = new ESBoidIndividual[POPULATION_SIZE];
        ESBoidIndividual[] initPrey = new ESBoidIndividual[POPULATION_SIZE];
        
        predatorBoids = new ESBoidIndividual[POPULATION_SIZE*ESBoidPopulation.getNumOffspring()];
        preyBoids = new ESBoidIndividual[POPULATION_SIZE*ESBoidPopulation.getNumOffspring()];
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            initPredator[i] =  new ESBoidIndividual();
            initPrey[i] =  new ESBoidIndividual();
            
            //predatorBoids[i] = new ESBoidIndividual();
            //preyBoids[i] = new ESBoidIndividual();
        }
        
        for (int i = POPULATION_SIZE; i < predatorBoids.length; i++) {
            //predatorBoids[i] = new ESBoidIndividual();
            //preyBoids[i] = new ESBoidIndividual();
        }
        
        // Create selector and populations
        selector = new ESSelector();
        predatorPopulation = new FileWritePopulation(initPredator, selector, MAX_GENERATIONS);
        preyPopulation = new FileWritePopulation(initPrey, selector, MAX_GENERATIONS);

        predatorResults = new ESBoidIndividual[MAX_GENERATIONS];
        preyResults = new ESBoidIndividual[MAX_GENERATIONS];
        
        System.out.println("PREDATOR SETUP");
        for (int i = 0; i < POPULATION_SIZE; i++) {
            System.out.println(predatorPopulation.at(i).ESString());
        }
        System.out.println("PREY SETUP");
        System.out.println("----------------------------------------------");
        for (int j = 0; j < POPULATION_SIZE; j++) {
            System.out.println(preyPopulation.at(j).ESString());
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
			  text("Prey Genome: " + preyPopulation.at(simulationCounter).printGenome(), 5, 485);  

			
        } else {
        if (simulationCounter < POPULATION_SIZE) {
            // simulation has ended
            // assign trial fitnesses
            fit = s.prey.getBoids().size();
            
            //predatorPopulation.getIndividual(simulationCounter).setTrial(trialCounter, 100 - fit);
            predatorPopulation.setOffTrial(simulationCounter, trialCounter, 100-fit);
            //preyPopulation.getIndividual(simulationCounter).setTrial(trialCounter, fit);
            preyPopulation.setOffTrial(simulationCounter, trialCounter, fit);
            
            //System.out.println("fitness change starting at: " + Integer.toString(fit));
            //System.out.println("predator fitness set to: " + Double.toString(predatorPopulation.getOffTrial(simulationCounter, trialCounter)));
            //System.out.println("predator fitness set to: " + Double.toString(predatorPopulation.getOffspring(simulationCounter).getFitness()));
            //System.out.println("prey fitness set to: " + Double.toString(preyPopulation.getOffTrial(simulationCounter, trialCounter)));
            //System.out.println("prey fitness set to: " + Double.toString(preyPopulation.getOffspring(simulationCounter).getFitness()));
            
            // reset fit, and increment simulation counter
            fit = 0;
            simulationCounter++;

            // reeset simulation
            background(255,0,0);
            frameCount = 0;
            if (simulationCounter <= POPULATION_SIZE - 1) {
				      s = new Sim(predatorPopulation.getOffspring(simulationCounter).getGenome(), preyPopulation.getOffspring(simulationCounter).getGenome());
            }
			  }
			  if (simulationCounter == POPULATION_SIZE){
				  //s = new Sim(predatorPopulation.getIndividual(simulationCounter).getGenome(), preyPopulation.getIndividual(simulationCounter).getGenome());
			    
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

            System.out.println("GENERATION: " + generationCounter);
            System.out.println("PREDATOR");
            for (int i = 0; i < POPULATION_SIZE; i++) {
                //System.out.println(predatorPopulation.at(i).printGenome());
            }
            System.out.println("Avg Fit: " + predatorPopulation.avgFitness());
            System.out.println("Avg Off Fit: " + predatorPopulation.avgOffFitness());
            System.out.println("Max Fit: " + predatorPopulation.maxFitness());
            System.out.println("Max Off Fit: " + predatorPopulation.maxOffFitness());
            ESBoidIndividual bestPred = predatorPopulation.getBestIndividual();
            System.out.println("Best Indiv: " + bestPred.ESString() + "\n");

            
            System.out.println("PREY");
            for (int j = 0; j < POPULATION_SIZE; j++) {
                //System.out.println(preyPopulation.at(j).printGenome());
            }
            System.out.println("Avg Fit: " + preyPopulation.avgFitness());
            System.out.println("Avg Off Fit: " + preyPopulation.avgOffFitness());
            System.out.println("Max Fit: " + preyPopulation.maxFitness());
            System.out.println("Max Off Fit: " + preyPopulation.maxOffFitness());
            ESBoidIndividual bestPrey = preyPopulation.getBestIndividual();
            System.out.println("Best Indiv from preyPop: " + preyPopulation.getBestIndividual().ESString());
            System.out.println("Best Indiv: " + bestPrey.ESString());
            System.out.println("----------------------------------------------");
            
            // reset trial counter
            trialCounter = 0;
            
            //must be after stats, runGeneration creates a new block of children
            
            predatorPopulation.runGeneration(selector);
            preyPopulation.runGeneration(selector);
            
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
