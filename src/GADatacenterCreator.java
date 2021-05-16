package geneticalgo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * This class' purpose is to provide method for creating a power datacenter.
 * @author Yuvraj Joshi
 *
 */
public class GADatacenterCreator {
	
	/**
	 * Creates a power datacenter using characteristics like host, architecture, cost, os etc.
	 * @param name - name of the datacenter
	 * @return Datacenter
	 */
	public static Datacenter createDatacenter(String name) {

		
		List<Host> hostList = new ArrayList<Host>();			// a list to store our machines

		List<Pe> peList1 = new ArrayList<Pe>();					// a list to store PEs or CPUs/Cores

		int mips = 10000;

		// Create PEs and add these into the list.
		for(int id = 0; id < 7; id++)							// an octa-core machine
			peList1.add(new Pe(id, new PeProvisionerSimple(mips)));

		List<Pe> peList2 = new ArrayList<Pe>();					// an octa-core machine
		for(int id = 0; id < 7; id++)
			peList2.add(new Pe(id, new PeProvisionerSimple(mips)));
		
		List<Pe> peList3 = new ArrayList<Pe>();					// a quad-core machine
		for(int id = 0; id < 7; id++)
			peList3.add(new Pe(id, new PeProvisionerSimple(mips)));

		// Parameters for Host
		int hostId = 0;
		int ram = 24800; 				// host memory (MB)
		long storage = 10000000; 		// host storage
		int bw = 100000;				// bandwidth

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList1,
				new VmSchedulerTimeShared(peList1))); // This is our first machine

		hostId++;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // Second machine
		
		hostId++;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList3,
				new VmSchedulerTimeShared(peList3))); // Third machine

		// Create a DatacenterCharacteristics object that stores the properties of a data center
		String arch = "x86"; 				// system architecture
		String os = "Linux"; 				// operating system
		String vmm = "Xen";					// virtual machine manager
		double time_zone = 10.0; 			// time zone this resource located
		double cost = 3.0; 					// the cost of using processing in this resource
		double costPerMem = 0.05; 			// the cost of using memory in this resource
		double costPerStorage = 0.1; 		// the cost of using storage in this resource
		double costPerBw = 0.1; 			// the cost of using bw in this resource
		
		LinkedList<Storage> storageList = new LinkedList<Storage>(); 

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
										arch, os, vmm, hostList, time_zone, cost, costPerMem,
										costPerStorage, costPerBw);

		// Create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics,
					new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
}
