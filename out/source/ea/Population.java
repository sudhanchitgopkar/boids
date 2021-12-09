package ea;

// Population class.

public class Population {
	
	protected Individual[] pop;
	protected Individual[] pop_temp;
	protected int popsize;
	protected int gen;
	protected double maxFit;
	protected double avgFit;
	protected int bestIndiv;
	
	// Constructor takes an array of Individuals
	// as an initial population. Please make all
	// the elements in the Individuals array the
	// same class type; otherwise, the algorithm
	// will attempt to do inter-species breeding
	// and a runtime error will most likely occur.
	public Population(Individual[] population) {
		
		popsize = population.length;
		gen = 0;
		pop = new Individual[popsize];
		pop_temp = new Individual[popsize];
		
		for (int i = 0; i < popsize; i++) pop[i] = population[i];
		
		updateStats();
		
	}
	
	// Simulates one generation using the parent
	// selection mechanism specificed by selector.
	public void runGeneration(Selector selector) {
		
		selector.update(this);
		repopulate(selector);
		updateStats();
		gen++;
		
	}
	
	public Individual at(int index) { return pop[index]; }
	
	public int size() { return popsize; }
	
	public int generation() { return gen; }
	
	public double maxFitness() { return maxFit; }
	
	public double avgFitness() { return avgFit; }
	
	public Individual getBestIndividual() { return pop[bestIndiv]; }
	
	protected void repopulate(Selector selector) {
		
		pop_temp[0] = getBestIndividual();
		
		for (int i = 1; i < popsize; i++) {
			
			int parent1_idx = selector.select();
			int parent2_idx = selector.select();
			
			pop_temp[i] = pop[parent1_idx].crossover(pop[parent2_idx]);
			
		}
		
		Individual[] temp = pop;
		pop = pop_temp;
		pop_temp = temp;
		
		for (int i = 1; i < popsize; i++) pop[i].mutate();
		
	}
	
	protected void updateStats() {
		
		maxFit = pop[0].fitness();
		avgFit = 0.0;
		bestIndiv = 0;
		
		for (int i = 1; i < popsize; i++) {
			
			if (pop[i].fitness() > maxFit) {
				
				maxFit = pop[i].fitness();
				bestIndiv = i;
				
			}
			
			avgFit += pop[i].fitness();
			
		}
		
		avgFit /= (double)popsize;
		
	}
	
}
