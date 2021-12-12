import ea.ESBoidPopulation;
import ea.ESBoidIndividual;
import ea.ESBoidSelector;

import java.io.FileWriter;
import java.io.IOException;

public class FileWritePopulation extends ESBoidPopulation {
	
	protected int maxGens;
	protected String maxFitness[];
	protected String avgFitness[];
	protected String bestIndividual[];
	
	public FileWritePopulation(ESBoidIndividual[] population, int maxGenerations) { 
		
		super(population); 
		
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
		
		selector.update(this);
		repopulate(selector);
		updateStats();
		
		if (gen < maxGens) {
			
			maxFitness[gen] = Double.toString(maxFit);
			avgFitness[gen] = Double.toString(avgFit);
			bestIndividual[gen] = getBestIndividual().toString();
			
		}
		
		gen++;
		
	}
	
	private static int min(int a, int b) { return (a < b) ? a : b; }
	
}
