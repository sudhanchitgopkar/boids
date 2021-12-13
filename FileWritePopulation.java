package implementations;

import ea.Selector;
import implementations.ESBoidPopulation;
import implementations.ESBoidIndividual;
import implementations.ESSelector;

import java.io.FileWriter;
import java.io.IOException;

public class FileWritePopulation extends ESBoidPopulation {
	
	protected int maxGens;
	protected String maxFitness[];
	protected String avgFitness[];
	protected String bestIndividual[];
	
	public FileWritePopulation(ESBoidIndividual[] population, Selector selector, int maxGenerations) { 
		
		super(population, selector); 
		
		maxGens = maxGenerations;
		maxFitness = new String[maxGens];
		avgFitness = new String[maxGens];
		bestIndividual = new String[maxGens];
		
	}
	
	public void writeData(String outputFile) { writeData(outputFile, 1); }
	
	public void writeData(String outputFile, int generationPrintGap) {
		
		if (generationPrintGap < 1) generationPrintGap = 1;
		
		try {
			
			FileWriter writer = new FileWriter(outputFile);
			fileWriteHelper(writer, "MAX FITNESS", maxFitness, generationPrintGap);
			fileWriteHelper(writer, "AVG FITNESS", avgFitness, generationPrintGap);
			fileWriteHelper(writer, "BEST INDIVIDUAL", bestIndividual, generationPrintGap);
			System.out.println("Written file to " + outputFile);
			writer.close();
			
		}
		catch (IOException e) {
			
			System.out.println("Failed to write content to output file.");
			e.printStackTrace();
			
		}
		
	}
	
	private void fileWriteHelper(FileWriter writer, String msg, String[] toPrint, int generationPrintGap) throws IOException {
		
		writer.write("GEN\t\t" + msg + "\n");
		
		for (int g = 0; g < min(gen, maxGens); g += generationPrintGap) {
			
			writer.write(Integer.toString(g));
			writer.write("\t\t");
			writer.write(toPrint[g]);
			writer.write("\n");
			
		}
		
		writer.write("\n");
		
	}
	
	@Override
	public void runGeneration(Selector selector) {
		
    updateStats();
    if (gen < maxGens) {
      
      //given the reliance on Offspring population, the offspring are the generation
      //with more time, this should be updated to the current population instead
      maxFitness[gen] = Double.toString(maxOffFit);
      avgFitness[gen] = Double.toString(avgOffFit);
      bestIndividual[gen] = getBestIndividual().ESString();//from toString
      
    }
		selector.update(this);
		repopulate(selector);
		
		
		
		
		gen++;
		
	}
	
	private static int min(int a, int b) { return (a < b) ? a : b; }
	
}
