package ea;
import implementations.ESBoidPopulation;

// The purpose of the Selector interface is that the Population
// uses it during parent selection to randomly select
// individuals for mating. It is up to the user to implement
// the Selector interface, since there are many ways one might
// want to do selection.

public interface Selector {
	
	// Given the current state of the population, this method
	// computes any metadata that will be needed for whatever
	// selection algorithm the user writes.
	public void update(ESBoidPopulation population);
	
	// Returns the index of the next individual to be selected
	// for mating.
	public int select();
	
	// Size of the population being operated on.
	public int size();
	
}