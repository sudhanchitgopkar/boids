package implementations;

//import ESBoidPopulation;
import ea.Selector;

// ESSelector selection. 
// canonical ES selection selects any of the population with equal possibility
// the primary force driving ES improvement is the number of children

public class ESSelector implements Selector{
	
	protected ESBoidPopulation curPop = null;
	
	
	@Override 
	public void update(ESBoidPopulation pop) {
		
		curPop = pop;//(ESBoidPopulation)
		
	}
	
	@Override 
	public int select() {
		//return a random number within the pop size
		return (int)Math.floor(curPop.size()*Math.random());
	}
	
	@Override 
	public int size() { if (curPop == null) return 0; return curPop.size(); }
	
	
}
