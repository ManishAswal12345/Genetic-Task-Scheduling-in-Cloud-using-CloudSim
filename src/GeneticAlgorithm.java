package geneticalgo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * Contains main method with the body for starting the project. 
 * Also contains utility functions for creating and printing objects.
 * 
 * @author Yuvraj Joshi
 *
 */
public class GeneticAlgorithm {
	
	/** The list of Cloudlets */
	private static List<Cloudlet> cloudletList;

	/** The list of VMs */
	private static List<Vm> vmlist;
	
	
	/**
	 * Creates a container to store VMs. This list is passed to the broker later.
	 * @param userId - the id of user
	 * @param vms - number of Vms
	 * @return list of Vms
	 */
	private static List<Vm> createVM(int userId, int vms) {

		LinkedList<Vm> list = new LinkedList<Vm>();

		// VM Parameters
		long size = 10000; 		// image size (MB)
		int ram = 512; 			// vm memory (MB)
		int mips = 500;			// Million Instructions Per Sec
		long bw = 10;			// bandwidth
		int pesNumber = 4;		// number of cpus
		String vmm = "Xen"; 	// VMM name
		Random rOb = new Random();
		
		// Create VMs
		Vm[] vm = new Vm[vms];
		for (int i = 0; i < vms; i++) {
			
			vm[i] = new Vm(i, userId, mips + rOb.nextInt(500), 
							pesNumber, ram, 
							bw, size, vmm, 
							new CloudletSchedulerSpaceShared());
			
			list.add(vm[i]);
		}

		return list;
	}
	
	/**
	 * Creates a container to store cloudlets
	 * @param userId - id of user
	 * @param cloudlets - number of cloudlets
	 * @return list of Cloudlets
	 */
	private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		// Cloudlet parameters
		long length = 1000;			// million instructions(MI)
		long fileSize = 300;		// file size before submitting (in bytes)
		long outputSize = 300;		// file size after submitting (in bytes)
		int pesNumber = 1;			// cpu
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for (int i = 0; i < cloudlets; i++) {
			
			int x = (int) (Math.random() * ((1000 - 1) + 1)) + 1;
			cloudlet[i] = new Cloudlet(i, (length + x), pesNumber, fileSize,
											outputSize, utilizationModel, 
											utilizationModel, utilizationModel);
			
			// Setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}

	/**
	 * Main method
     * @param args
	 */
	public static void main(String[] args) {
		Log.printLine("Starting Genetic Algorithm...");

		try {
			
			// Initialize the CloudSim package.
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();	// for recording starting time of simulation
			boolean trace_flag = false; // trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			//Create Datacenters
			@SuppressWarnings("unused")
			Datacenter datacenter0 = GADatacenterCreator.createDatacenter("Datacenter_0");

			//Create Broker
			GADatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Create VMs and Cloudlets and send them to broker
			vmlist = createVM(brokerId, 10); // creating 10 vms
			cloudletList = createCloudlet(brokerId, 50); // creating 50 cloudlets
			
			// Create a list to contain Cloudlets in sorted order of length
			List<Cloudlet> sortedCloudletList = new ArrayList<Cloudlet>();
			for(Cloudlet cloudlet : cloudletList)
				sortedCloudletList.add(cloudlet);
			
			// Sort sortedCloudletList based on length in ascending order
			Collections.sort(sortedCloudletList, new Comparator<Cloudlet>(){
				public int compare(Cloudlet c1, Cloudlet c2) {
					return (int)(c1.getCloudletLength() - c2.getCloudletLength());
				}
			});
			
			// Create a list to contain the VMs in sorted order by of mips(descending)
			ArrayList<Vm> sortedVmList = new ArrayList<Vm>();		
			for(Vm vm : vmlist)
				sortedVmList.add(vm);
			
			// Sorting sortedVmList based on mips, descending order			
			Collections.sort(sortedVmList, new Comparator<Vm>() {
				public int compare(Vm v1, Vm v2) {
					return (int)(v2.getMips() - v1.getMips());
				}
			});
			
			int numVms = sortedVmList.size();	
			int numCloudlets = sortedCloudletList.size();
			
			GeneticFunctions gf = new GeneticFunctions();
			
			/*
			 * INITIALISE POPULATION
			 */
			ArrayList<Chromosomes> initialPopulation = gf.initialisePopulation(numCloudlets, numVms, 
																				sortedVmList, sortedCloudletList);
			
			int firstFitIndex = 0;		// denotes the most fit chromosome in the population
			int secondFitIndex = 0;		// denotes the second most fit chromosome in the population
			double time = 1000000;		// upper bound of time to process all cloudlets
			int populationSize = initialPopulation.size();
			
			/*
			 * INITIAL FITNESS CHECK
			 */
			double[] parameters = gf.calculateFitness(initialPopulation, firstFitIndex, 
														secondFitIndex, time, 
														numCloudlets, populationSize);
			firstFitIndex = (int)parameters[0];	
			secondFitIndex = (int)parameters[1];
			time = parameters[2];
			
			/*
			 * START PRODUCING GENERATIONS 
			 * run for "numVms" number of generations
			 */
			for(int itr = 0; itr < numCloudlets; itr++){				
				
				/*
				 * SELECTION AND CROSSOVER
				 */
				gf.selectionAndCrossOver(initialPopulation, firstFitIndex, 
											secondFitIndex, numCloudlets);
			    
				double rangeMin = 0.0f;
			    double rangeMax = 1.0f;
			    Random r = new Random();
				double mutProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
				
				/*
				 * MUTATION
				 */
				if(mutProb < 0.5)
					gf.mutation(r, initialPopulation, populationSize, 
								sortedVmList, numCloudlets);
				
				/*
				 * UPDATE FITNESS CHECK
				 */
				parameters = gf.calculateFitness(initialPopulation, firstFitIndex, 
													secondFitIndex, time, 
													numCloudlets, populationSize);
				
				firstFitIndex = (int)parameters[0];	
				secondFitIndex = (int)parameters[1];
				time = parameters[2];
				System.out.println("Time at "+ itr + "-th " + time);
			}			
			System.out.println("Final time: " + time);
			
			// Store most fit chromosome's genelist
			ArrayList<Gene> final_Gene_list = new ArrayList<Gene>();
			final_Gene_list = initialPopulation.get(firstFitIndex).getGeneList();
		
			List<Cloudlet> final_Cloudlet_list = new ArrayList<Cloudlet>();		//for storing final cloudlets in order
			List<Vm> final_Vm_list = new ArrayList<Vm>();						//for storing final Vms in order
			
			// Store the most fit cloudlets and Vms into respective lists
			for(Gene g : final_Gene_list){
				final_Cloudlet_list.add(g.getCloudletFromGene());
				final_Vm_list.add(g.getVmFromGene());
			}
			
			// Submit to broker
			broker.submitVmList(final_Vm_list);
			broker.submitCloudletList(final_Cloudlet_list);

			// Start the simulation
			CloudSim.startSimulation();

			// Print results when simulation is over
			List<Cloudlet> result = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(result);

			Log.printLine("Process finished!");
		} 
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	/**
	 * Creates a new Broker
	 * @return broker
	 */
	private static GADatacenterBroker createBroker() {

		GADatacenterBroker broker = null;
		try {
			broker = new GADatacenterBroker("Broker");
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return broker;
	}

	
	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list - list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + indent
				+ "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}

	}
}
