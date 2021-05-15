package geneticalgo;

import java.util.ArrayList;
import org.cloudbus.cloudsim.Vm;

/**
 * This class denotes a Chromosome containing a list of Genes.
 * Provides getter method for list of genes and a method to update a gene.
 * @author Yuvraj Joshi
 *
 */
public class Chromosomes {
	
	/** A list of Gene object */
	protected ArrayList<Gene> geneList;
	
	/**
	 * Create a new Chromosome.
	 * @param geneList
	 */
	public Chromosomes(ArrayList<Gene> geneList){
		this.geneList = geneList;		
	}
	
	/**
	 * Getter method for Gene list.
	 * @return array - list of genes
	 */
	public ArrayList<Gene> getGeneList(){
		return this.geneList;
	}
	
	/**
	 * For replacing a Vm with a new one at an index.
	 * @param index - index at which to update the Vm
	 * @param vm - new Vm
	 */
	public void updateGene(int index, Vm vm){
		Gene gene = this.geneList.get(index);
		gene.setVmForGene(vm);
		this.geneList.set(index, gene);
	}
}
