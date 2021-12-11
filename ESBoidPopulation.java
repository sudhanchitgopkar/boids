package implementations;

import java.util.Random;
import ea.Individual;
import ea.Selector;
//import ea.Population;
import java.util.Arrays;
import java.util.Comparator;


public class ESBoidPopulation {//extends Population{
	
	//current two populations in case mulambda is used instead of mu+lambda
	protected ESBoidIndividual[] pop;
	protected ESBoidIndividual[] children;
	protected boolean popSorted = false;
	protected boolean childSorted = false;
	
	protected ESBoidIndividual[] pop_temp;
	protected int popsize;
	protected int gen;
	protected double minFit;
	protected double maxFit;
	protected double avgFit;
	//protected int bestIndiv;
	protected int bestParent;
	protected int bestChild;
	
	protected static final int numoffspring = 2;
	protected static final boolean muplus = false;
	
	// the comparator allows a sorting function during repopulation
	class sortByFitness implements Comparator<Individual>{
		public int compare(Individual a, Individual b){
			return Double.compare(a.fitness(), b.fitness());
		}
	}
	
	// Constructor takes an array of Individuals
	// as an initial population. Please make all
	// the elements in the Individuals array the
	// same class type; otherwise, the algorithm
	// will attempt to do inter-species breeding
	// and a runtime error will most likely occur.
	//@Override
	public ESBoidPopulation(ESBoidIndividual[] population) {
		//super(population);
		
		//probably unnecessary 
		popsize = population.length;
		
		gen = 0;
		pop = new ESBoidIndividual[popsize];
		for (int i = 0; i < popsize; i++) pop[i] = (ESBoidIndividual)population[i];
		updateStats();
		//end probably unnecessary
		
		children = new ESBoidIndividual[popsize*numoffspring];
		pop_temp = new ESBoidIndividual[popsize];//*numoffspring
		
	}
	
	// getter for individual
    public ESBoidIndividual getIndividual(int index) {
        return (ESBoidIndividual) pop[index];
    }
	
	// shuffle the population
    // Implementing Fisher–Yates shuffle
    public void shuffle() {
        int index;
        ESBoidIndividual temp;
        Random r = new Random();
        for (int i = pop.length - 1; i > 0; i--) {
            index = r.nextInt(i + 1);
            temp = pop[index];
            pop[index] = pop[i];
            pop[i] = temp;
        }
		popSorted = false;
    }
	
	// // Simulates one generation using the parent
	// // selection mechanism specificed by selector.
	public void runGeneration(Selector selector) {
		
		selector.update(this);
		repopulate(selector);
		updateStats();
		gen++;
		
	}
	
	// public ESBoidIndividual at(int index) { return pop[index]; }
	
	// public int size() { return popsize; }
	
	// public int generation() { return gen; }
	
	// public double minFitness() { return minFit; }
	
	public double maxFitness() { return maxFit; }
	
	public double avgFitness() { return avgFit; }

  public int size() { return pop.length; }
	
	public int numOffspring() {return numoffspring; }
	
	public ESBoidIndividual at(int pos) {return pop[pos];}

  //best individual is not implemented, but combines a sorted parent and child
	
	
	//TODO - how is the best individual chosen continuously? the list isn't sorted
	//implementation seems to work TODO - possibly improve
	public ESBoidIndividual getBestIndividual() { 
		if (!muplus){return children[bestChild];}
		else{
			double childFit = pop[bestParent].fitness();
			double parentFit = children[bestChild].fitness();
			if (childFit > parentFit){return children[bestChild];}
			else {return pop[bestParent];}
		}
	}
	
	public ESBoidIndividual getBestParent() { return pop[bestParent]; }
	public ESBoidIndividual getBestChild() { return children[bestChild]; }
	
	public ESBoidIndividual getBestIndiv() { 
		if (children[bestChild].fitness() > pop[bestParent].fitness()) {return children[bestChild];}
		else {return pop[bestParent];}
	}
	
	//@Override - this should be inherited from population, but it is not
	protected void repopulate(Selector selector) {
		
		//crossover always exists, but may frequently return the main parent in ES
		for (int i = 0; i < popsize; i++){
			for (int j = 0; j < numoffspring; ++j){
				//just use i as parent 1
				int parent2_idx = selector.select();

        //Arrays.copyOf(genome, genome.length);.clone()
				ESBoidIndividual cGenome = pop[i].crossover(pop[parent2_idx]);
				children[i*numoffspring + j] = new ESBoidIndividual(cGenome.getGenome(), cGenome.getSigma());//cGenome.length);
				
				children[i*numoffspring + j].mutate();
			}
		}
		childSorted = false;
		updateChildStats();
		
		//repopulate based on muplus or lambda selection
		
		if (!popSorted){
			Arrays.sort(pop, new sortByFitness());
			popSorted = true;
		}
		if (!childSorted){
			Arrays.sort(children, new sortByFitness());
			childSorted = true;
		}
		
		double childFit;
		double parentFit;
		// ESBoidIndividual bestChild;
		// ESBoidIndividual bestParent;
		
		int parPos = 0;
		int childPos = 0;
		if (!muplus){
			for (int i=0; i < popsize; i++){
				pop_temp[i] = children[i];
			}
		}
		else{
			for (int i=0; i < popsize; i++){
				//check if one has finished the array
				if (parPos < pop.length){
					parentFit = pop[parPos].fitness();
				}
				else{
					pop_temp[i] = children[childPos];
					childPos++;
					continue;
				}
				if (childPos < children.length){
					childFit = children[childPos].fitness();
				}
				else{
					pop_temp[i] = pop[parPos];
					parPos++;
					continue;
				}
				
				if (childFit < parentFit){
					pop_temp[i] = pop[parPos];
					parPos++;
				}
				else{
					pop_temp[i] = children[childPos];
					childPos++;
				}
				
			}
		}
		
		pop = pop_temp;
		
		updateStats();
	}
	
	protected void updateChildStats() {
		
		double minChildFit = children[0].fitness();
		double maxChildFit = children[0].fitness();
		
		bestChild = 0;
		for (int i = 1; i < popsize*numoffspring; i++) {
			
			if (children[i].fitness() < minChildFit) {
				
				minChildFit = children[i].fitness();
				//bestChild = i;
				
			}
			if (children[i].fitness() > maxChildFit) {
				
				maxChildFit = children[i].fitness();
				bestChild = i;
				
			}
			
		}
		
		if (minChildFit < minFit){ minFit = minChildFit; }
		if (maxChildFit > maxFit){ maxFit = maxChildFit; }
		
	}
	
	protected void updatePopStats() {
		
		double minParFit = pop[0].fitness();
		double maxParFit = pop[0].fitness();
		
		avgFit = 0.0;
		bestParent = 0;
		
		for (int i = 1; i < popsize; i++) {
			if (pop[i].fitness() > minParFit) {
				
				minParFit = pop[i].fitness();
				//bestParent = i;
				
			}
			else if (pop[i].fitness() < maxParFit) {
				
				maxParFit = pop[i].fitness();
				bestParent = i;
				
			}
			
			avgFit += pop[i].fitness();
			
		}
		
		//child fit comes after parent, so parent should set, with child possibly replacing
		minFit = minParFit;
		maxFit = maxParFit;
		
		// if (minParFit < minFit){ minFit = minParFit;}
		// if (maxParFit > maxFit){ maxFit = maxParFit;}
		
		avgFit /= (double)popsize;
	}
	
	//@Override
	protected void updateStats() {
		if (pop != null){updatePopStats();}
		if (children != null && children[0] != null) {updateChildStats();}
		
	}
	
}
