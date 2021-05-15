package geneticalgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * Contains methods involved in Genetic Algorithm.
 * @author Yuvraj Joshi
 *
 */
public class GeneticFunctions {
	
	/**
	 * Creates a list of Chromosomes. Each chromosomes contains a list of Genes.
	 * Each gene contains = {Cloudlet, Vm}
	 * No. of genes in a genelist = numCloudlets
	 * No. of chromosomes in population = numCloudlets
	 * 
	 * @param numCloudlets - number of cloudlets
	 * @param numVms - number of vms
	 * @param sortedVmList - a list of sorted Vms
	 * @param sortedCloudletList - a list of sorted Cloudlets
	 * 
	 * @return an ArrayList of Chromosomes
	 */
	public ArrayList<Chromosomes> initialisePopulation(int numCloudlets, int numVms, 
																ArrayList<Vm> sortedVmList, List<Cloudlet> sortedCloudletList) {
		
		ArrayList<Chromosomes> initialPopulation = new ArrayList<Chromosomes>();
		
		for(int j = 0; j < numCloudlets; j++){
			ArrayList<Gene> firstChromosome = new ArrayList<Gene>();
			
			for(int i = 0; i < numCloudlets; i++){
				
				int k = (i + j) % numVms;
				k = (k + numCloudlets) % numCloudlets;
				
				// j-th chromosome's gene list will start with j-th vm from the sortedVmList.
				Gene geneObj = new Gene(sortedCloudletList.get(i), sortedVmList.get(k));
				
				firstChromosome.add(geneObj);		// add gene to genelist
			}
			
			Chromosomes chromosome = new Chromosomes(firstChromosome);
			initialPopulation.add(chromosome);		// add chromosome to population
		}
		return initialPopulation;
	}
	
	/**
	 * Calculates fitness of every chromosome in the population based on the genes of that chromosome.
	 * Assigns fit index to those genelist in which total time taken by all Cloudlets to process on a Vm is the least.
	 * 
	 * @param initialPopulation	- list of chromosomes
	 * @param firstFitIndex - index denoting most fit chromosome
	 * @param secondFitIndex - index denoting second most fit chromosome
	 * @param time - upper bound of total process time
	 * @param numCloudlets - number of cloudlets
	 * @param populationSize - number of chromosomes in the population
	 * 
	 * @return an array of parameters
	 */
	public double[] calculateFitness(ArrayList<Chromosomes> initialPopulation, int firstFitIndex, int secondFitIndex,
											double time, int numCloudlets, int populationSize) {
		
		for(int i = 0; i < populationSize; i++){
			
			ArrayList<Gene> l = new ArrayList<Gene>();	// get the i-th chromosome's gene list
			l = initialPopulation.get(i).getGeneList();
			double sum = 0;								// total time taken by all the cloudlets for the i-th chromosome
			
			//for every gene in genelist
			for(int j = 0; j < numCloudlets; j++){
				
				Gene g = l.get(j);
				Cloudlet c = g.getCloudletFromGene();
				Vm v = g.getVmFromGene();
				
				//calculate the time taken by cloudlet to process on the given Vm
				double temp = c.getCloudletLength() / v.getMips();
				sum += temp;		
			}
			
			//if total time taken by this i-th chromosome is least, then mark it the fittest
			if(sum < time){
				time = sum;
				secondFitIndex = firstFitIndex;
				firstFitIndex = i;
			}
		}
		
		return new double[] {firstFitIndex, secondFitIndex, time};
	}
	
	/**
	 * Selects the first and second, most fit Chromosomes and cross-breeds them.
	 * 
	 * @param initialPopulation - list of chromosomes
	 * @param firstFitIndex - index denoting most fit chromosome
	 * @param secondFitIndex - index denoting second most fit chromosome
	 * @param numCloudlets - number of cloudlets
	 */
	public void selectionAndCrossOver(ArrayList<Chromosomes> initialPopulation, int firstFitIndex, int secondFitIndex,
												int numCloudlets) {
		// SELECTION
		int index1,index2;			
		index1 = firstFitIndex;
		index2 = secondFitIndex;
		
		// Store their chromosomes and genelists
		ArrayList<Gene> l1 = new ArrayList<Gene>();
		l1 = initialPopulation.get(index1).getGeneList();
		Chromosomes chromosome1 = new Chromosomes(l1);
		
		ArrayList<Gene> l2 = new ArrayList<Gene>();
		l2 = initialPopulation.get(index2).getGeneList();
		Chromosomes chromosome2 = new Chromosomes(l2);
		
		// CROSSOVER			
		Random random = new Random();
		int swap_index_limit;
		swap_index_limit = random.nextInt(numCloudlets) % numCloudlets;	// generate a random point

		// Swap VMs among two genelist
		for(int j = 0; j <= swap_index_limit; j++) {
		
			Vm vm1 = l1.get(j).getVmFromGene();
			Vm vm2 = l2.get(j).getVmFromGene();
			// Update the two chromosomes
			chromosome1.updateGene(j, vm2);
			chromosome2.updateGene(j, vm1);
		}
		
		// Update population
		initialPopulation.set(index1, chromosome1);
		initialPopulation.set(index2, chromosome2);
	}
	
	/**
	 * Mutates a random Chromosome by replacing a random VM by the VM with most mips.
	 * 
	 * @param rand - an object of class Random
	 * @param initialPopulation - list of chromosomes
	 * @param populationSize - number of chromosomes in the population
	 * @param sortedVmList - a list of sorted Vms
	 * @param numCloudlets - number of cloudlets
	 */
	public void mutation(Random rand, ArrayList<Chromosomes> initialPopulation, int populationSize,
									ArrayList<Vm> sortedVmList, int numCloudlets) {
		
		int m;
		m = rand.nextInt(populationSize) % populationSize;
		
		// Mutation Chromosome
		ArrayList<Gene> l = new ArrayList<Gene>();
		l = initialPopulation.get(m).getGeneList();
		Chromosomes mutchromosome = new Chromosomes(l);	
		
		// VM with highest mips
		Vm highestMipsVM = sortedVmList.get(0);
		
		// Update
		int n;
		n = rand.nextInt(numCloudlets) % numCloudlets; 
		mutchromosome.updateGene(n, highestMipsVM);		
	}
}
