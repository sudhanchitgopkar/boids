package implementations;

import ea.Population;
import ea.Selector;

// Proportional selection. Works by first mapping the population's
// fitness values to the range [0, 1], then applying the usual
// proportional selection algorithm.

public class ProportionalSelector implements Selector {
	
	protected double[] cumulativeProbs = null;
	protected Population curPop = null;
	
	private double epsilon = 0.00001;
	
	@Override
	public void update(Population pop) {
		
		if (cumulativeProbs == null) reallocate(pop.size());
		if (cumulativeProbs.length != pop.size()) reallocate(pop.size());
		curPop = pop;
		
		// Calculate min and max fitness so the population's
		// fitness values can be mapped to [0, 1].
		
		double minFitness = pop.at(0).fitness();
		double maxFitness = minFitness;
		
		for (int i = 1; i < pop.size(); i++) {
			
			if (pop.at(i).fitness() < minFitness) minFitness = pop.at(i).fitness();
			if (pop.at(i).fitness() > maxFitness) maxFitness = pop.at(i).fitness();
			
		}
		
		// Map the fitness values to cumulativeProbs, so that
		// the ith element of cumulativeProbs is equal to the
		// fitness of the ith individual in pop, mapped to
		// [0, 1]. Also calculates total fitness for use in the
		// next part.
		
		double totalFitness = 0.0;
		double fitnessRange = maxFitness - minFitness;
		if (fitnessRange < epsilon) fitnessRange = epsilon;
		
		for (int i = 0; i < pop.size(); i++) {
			
			cumulativeProbs[i] = (pop.at(i).fitness() - minFitness) / fitnessRange;
			totalFitness += cumulativeProbs[i];
			
		}
		
		// At the ith iteration, cumulativeFitness holds the
		// sum of the (mapped) fitnesses of individuals 0 to i.
		// Then, cumulativeProbs[i] gets this cumulative fitness
		// value divided by the total fitness, so that the
		// proportional selection method we learned in class can
		// be easily used.
		
		double cumulativeFitness = 0.0;
		
		for (int i = 0; i < pop.size(); i++) {
			
			cumulativeFitness += cumulativeProbs[i];
			cumulativeProbs[i] = cumulativeFitness / totalFitness;
			
		}
		
	}

	@Override
	public int select() {
		
		// Randomly selects an array index for the next parent.
		// Implements the algorithm we learned in class.
		
		double r = Math.random();
		for (int i = 0; i < curPop.size(); i++) if (r <= cumulativeProbs[i]) return i;
		return curPop.size() - 1;
		
	}

	@Override
	public int size() { if (cumulativeProbs == null) return 0; return cumulativeProbs.length; }
	
	protected void reallocate(int memsize) { cumulativeProbs = new double[memsize]; }
	
}
