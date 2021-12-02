package ea;

// Please implement this Individual interface. I suggest
// that in your implementation you design it in order to
// avoid redundant fitness evaluations, especially if it
// is an expensive function. For instance, you could have
// your implementation track when the individual's genotype
// has changed, and only recompute the fitness if there
// has been a change.

public interface Individual {
	
	public double fitness();
	
	public void mutate();
	
	public Individual crossover(Individual indiv);
	
}
