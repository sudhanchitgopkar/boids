package implementations;

import java.util.Random;
import java.util.Arrays;

import ea.Individual;

public class ESBoidIndividual implements Individual {
    // CONSTANTS
    protected final static double MAX_GENE_RANGE = 5.0;
    protected final static double MIN_GENE_RANGE = 1.0;
	
	//ONLY for use with akley function, run then comment
	// protected final static double PI = 3.14159;
	// protected final static double E = 2.71828;

	protected final static int DIMENSION = 4;
    protected static final int NUM_TRIALS = 5; // Must be <= POPULATION_SIZE in BoidEvolution, may need better way to set
	protected final static double MUTATION_PROBABILITY = 0.8;
	protected final static double CROSSOVER_PROBABILITY = 0.25;
	
	//the learning rate of mutation
	protected final static double TAU = (MAX_GENE_RANGE - MIN_GENE_RANGE)*1/Math.sqrt(DIMENSION);
	//the minimum mutation
	protected final static double EPSILON = 0.015;
	
	protected final static boolean CROSS = true;
	
	protected boolean printed = false;

    // Individual's representation ("genotype")
    // Genes are: SEP_WEIGHT, ALI_WEIGHT, COH_WEIGHT, INS_WEIGHT in that order
    protected double[] genome;

    // Individual's fitness trials - used to calculate individual's total fitness
    protected double[] trials;
	protected double sigma;

    // Flag for whether fitness has been calculated
    // NOT SURE IF NEEDED/SHOULD BE USED IN COEVOLUTION!!!
    protected double totalFitness;
    protected boolean fitnessNeedsUpdate = true;

    // constructor
    public ESBoidIndividual() {
        // initialize genotype
        genome = new double[DIMENSION];
        Random r = new Random();
        for (int i = 0; i < DIMENSION; i++) {
            genome[i] = MIN_GENE_RANGE + (MAX_GENE_RANGE - MIN_GENE_RANGE) * r.nextDouble();
        }

		sigma = r_mut_range();
		
        // initialize trials
        trials = new double[NUM_TRIALS];
		
		//if not declaring from a genome, probably a good warning
		printed = false;
		
		fitnessNeedsUpdate = true;
    }


    // protected ESBoidIndividual(double[] genome) {
        // this.genome = genome;
        // trials = new double[DIMENSION];
        // for (int i = 0; i < DIMENSION; i++) {
            // trials[i] = 0.0;
        // }
    // }
	
	protected ESBoidIndividual(double [] genome, double sigma) { 
		this.genome = Arrays.copyOf(genome, genome.length);//genome.clone()
		this.sigma = sigma;
		trials = new double[NUM_TRIALS];
		for (int i = 0; i < DIMENSION; i++) {
            trials[i] = 0.0;
        }
		
		//if declaring from a genome, likely already printed once
		printed = true;
		
		fitnessNeedsUpdate = true;
	}
	
	//for compilation checking only
	//private double ackleyFunctOff2() {
	//	// Computation of Ackley's function.
	//	double sum1 = 0.0, sum2 = 0.0;
	//	for (int i = 0; i < DIMENSION; i++) { sum1 += (genome[i] -2)* (genome[i] -2); sum2 += Math.cos(2.0 * PI * (genome[i] -2)); }
	//	sum1 = -0.2 * Math.sqrt(sum1 / (double)DIMENSION); sum2 /= (double)DIMENSION;
		
	//	if (!printed){
	//		System.out.println("Using ackley function - be warned");
	//		printed = true;
	//		}
		
	//	return -20.0 * Math.exp(sum1) - Math.exp(sum2) + 20.0 + E;

	//}
	
    // Individual's fitness function - calculates fitness over all trials
    @Override
    public double fitness() {
        // check if fitness needs to be updated (REMOVE???)
        if (fitnessNeedsUpdate) {
            totalFitness = 0.0;
            for (int i = 0; i < NUM_TRIALS; i++) {
                totalFitness += trials[i];
            }
			
			//TODO: only returning Ackley Function
			//totalFitness = ackleyFunctOff2();
			
            fitnessNeedsUpdate = false;
        }

        // return total fitness 
        return totalFitness;
    }
	
	@Override
	public ESBoidIndividual crossover(Individual indiv) {
		//with ES, the crossover does not always need to happen
		//given the population, it will always be called. a clone of the calling parent should be returned
		
		// There is a CROSSOVER_PROBABILITY chance of
		// crossover occurring; if it does not occur then
		// one of the parents is copied at random to be
		// the child.
		
		// check if individuals are of the same type
        
		if (!(indiv instanceof ESBoidIndividual)) {
            throw new IllegalArgumentException("Cannot crossover with a different type of individual");
        }
		
		// convert indiv to ESBoidIndividual
        ESBoidIndividual other = (ESBoidIndividual) indiv;
		
		if (CROSS &&( Math.random() <= CROSSOVER_PROBABILITY)) {
			
			//ESBoidIndividual esbindiv = (ESBoidIndividual)indiv;
			//double[] childGenome = genome.clone();
      double[] childGenome = Arrays.copyOf(genome, genome.length);
			
			for (int i = 0; i < (int)(DIMENSION/2); i++) {
				
        //crossover two of the values from one parent to the other
				int alpha = (int)Math.random()*DIMENSION;
        childGenome[alpha] = other.genome[alpha];
				//childGenome[i] = mix(genome[i], other.genome[i], alpha);
				
			}
			//cross both parent sigmas - mean
			double childSigma = (other.sigma + sigma)/2.0;
			//return new individual
			return new ESBoidIndividual(childGenome, childSigma);
			
		}
		else return new ESBoidIndividual(Arrays.copyOf(genome, genome.length), sigma);//genome.clone()
		
	}

	//mutation of the sigma
	public void sigmaMutate() {
		if (Math.random() < 0.5){sigma -= TAU;}
		else{ sigma += TAU;}
		sigma = clamp(sigma, EPSILON, MAX_GENE_RANGE);
		//alternate sigma minimum: if (Math.abs(sigma) < EPSILON) {sigma = Math.abs(sigma)*EPSILON/sigma;}
		
	}
	
	@Override
	public void mutate() {
		
		//for ES implementation, sigma mutation should proceed before normal mutation - the best mutation genes are more likely to be passed on
		
		
		// Uniform random mutation within mutation radius.
		// MUTATION_PROBABILITY is the probability that any
		// given entry in this individual's genotype will be
		// mutated.
		
		//double r = Math.random();
		Random r = new Random();
		if (r.nextDouble() < MUTATION_PROBABILITY) {
			//double mutr = Math.random();
			
			//mutate sigma before mutating the genes, it's possible sigma may not mutate
			if (r.nextDouble() < MUTATION_PROBABILITY){
				sigmaMutate();
			}
			
			int mutPos = (int)(r.nextDouble()*DIMENSION);
			//positive or negative mutation
			if (r.nextDouble() < 0.5) {genome[mutPos] = clamp(genome[mutPos] + sigma, MIN_GENE_RANGE, MAX_GENE_RANGE);}
			else {genome[mutPos] = clamp(genome[mutPos] - sigma, MIN_GENE_RANGE, MAX_GENE_RANGE);}
			
			
			
		}
		fitnessNeedsUpdate = true;
		
	}

    // NUM_TRIALS getter
    public static int getNumTrials() { return NUM_TRIALS; }

    // trial setter
    public void setTrial(int trial, double fitness) {
        trials[trial] = fitness;
    }

    // genotype getter
    public double[] getGenome() { return genome; }
	  
    public double getSigma() { return sigma; }

	public double getFitness() { return totalFitness; }
    
	public String ESString(){
		String rep = "fitness: " + String.valueOf(fitness()) + ", genome: [";
		for(int i=0; i<DIMENSION - 1; ++i){
			rep = rep + String.valueOf(genome[i]) + ", ";
			}
		rep = rep + String.valueOf(genome[DIMENSION - 1]);
		sigma = r_mut_range();
		rep = rep + "] " + String.valueOf(sigma);
		return rep;
	}
	
    public String printGenome() {
      String s = "[ ";
      for (int i = 0; i < genome.length; i++) {
         s += genome[i] + " ";
      }
       return s + "]";
    }

	public void center_vars(){
		//this function just centers the variables since all variables are based on relations between each other
		
		//TODO: for center_vars (non-simple method) include the multiplier so that the spread does not affect the ratio of variables before/after realignment
		
		//acquire the spread of the variables
		double min_dimen = genome[0];
		double max_dimen = genome[0];
		double [] temp_genome = Arrays.copyOf(genome, genome.length);//genome.clone();
		int pivot = 0;
		
		for (int i = 1; i < DIMENSION; ++i){
			if (genome[i] < min_dimen){min_dimen = genome[i];}
			else if (genome[i] > max_dimen){min_dimen = genome[i];}
		}
		
		double var_mid = (max_dimen - min_dimen)/2.;
		double var_center = (max_dimen + min_dimen)/2.;
    double absolute_mid = (MAX_GENE_RANGE-MIN_GENE_RANGE)/2.;
    double absolute_center = (MAX_GENE_RANGE+MIN_GENE_RANGE)/2.;
		
		//centers the values, simple multiplier applied (multiplier could make the result off-center)
		//the formula could be improved to make completely centered
		System.out.println("before: " + printGenome());
    temp_genome[0] = genome[0] + (absolute_center - var_center);
    for (int i = 1; i < DIMENSION; ++i){
			//centers the value
			
      //modifies the value to be in line with ratio
			temp_genome[i] = temp_genome[i]/(temp_genome[pivot]/genome[pivot]);
		}
		genome = temp_genome;
    System.out.println("after: " + printGenome());
	}

    // Intermediate crossover
	protected double mix(double start, double end, double t) { return end * t + start * (1.0 - t); }
	protected double clamp(double x, double min, double max) { if (x < min) x = min; if (x > max) x = max; return x; }
	
	protected double r_range(){return Math.random()*(MAX_GENE_RANGE - MIN_GENE_RANGE) + MIN_GENE_RANGE;}
	protected double r_mut_range(){return Math.random()*(MAX_GENE_RANGE - MIN_GENE_RANGE)/2.0;}
	
}
