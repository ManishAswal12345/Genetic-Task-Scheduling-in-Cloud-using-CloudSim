package geneticalgo;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * This class represents a Gene which contains a task and a Vm.
 * Provides setter and getter methods for the same.
 * @author Yuvraj Joshi
 *
 */
public class Gene {
	
	/** Task to be performed */
	private Cloudlet task;
	
	/** VM to run a task */
	private Vm vm;
	
	/**
	 * Create a new Gene object.
	 * @param cl - cloudlet
	 * @param v - virtual machine
	 */
	public Gene(Cloudlet cl, Vm v)
	{
		this.task = cl;
		this.vm = v;
	}
	
	/**
	 * Getter method for Cloudlet
	 * @return Cloudlet
	 */
	public Cloudlet getCloudletFromGene()
	{
		return this.task;
	}
	
	/**
	 * Getter method for Vm
	 * @return Vm
	 */
	public Vm getVmFromGene()
	{
		return this.vm;
	}
	
	/**
	 * Setter method for cloudlet
	 * @param cl - cloudlet
	 */
	public void setCloudletForGene(Cloudlet cl)
	{
		this.task = cl;
	}
	
	/**
	 * Setter method for Vm
	 * @param vm
	 */
	public void setVmForGene(Vm vm)
	{
		this.vm = vm;
	}
}
