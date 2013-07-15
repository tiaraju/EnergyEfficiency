package cpu;

import java.io.IOException;
import java.security.InvalidParameterException;


/**
 * The CPUWorkloadGenerator class provides some support to generate load for the CPUs in your computer system.
 * It is possible to customize the amount of CPU load and for how long to maintain such load. It was considered
 * N levels of CPU load, where N corresponds to the number of CPUs in the system. 
 * In this case, CPU is the term to represent both processor and cores.
 * 
 * <p>
 * For example, considering one computer with a dual core processor, there are 4 CPUs and 4 levels of load
 * represented by the percentage of CPU utilization (25%, 50%, 75%, 100%). Note that, the load levels is not related to
 * each CPU itself but the overall CPU utilization, then, one CPU with maximum load corresponds to 25% of CPU utilization in
 * this system.
 * 
 * <p> In order to generate CPU load it was used the stress program, a workload generator for POSIX systems. See more
 * information about the stress program in: {@link} <a href="http://weather.ou.edu/~apw/projects/stress/">stress</a>
 * 
 * @author Efficiency Energetic Project
 * @version 1.0
 * 
 */
public class CPUWorkloadGenerator{
	
	CPUMonitor monitor;

	/**
	 * Generates an amount of CPU load, as specified by the numberOfCPUs passed as parameter. Each CPU is stressed
	 * with the maximum load possible. The percentage of
	 * CPU utilization resulting from such an amount of CPU load is given by (numberOfCPUs * 100/N), where N is the number
	 * of CPUs in your computer. 
	 * 
	 * This percentage of CPU utilization will be maintained for an UNLIMITED time. 
	 * 
	 * @param number of CPUs to generate load for.
	 * @throws InvalidParameterException if numberOfCPUs > N e numberOfCPUs < 0, where N is the number of CPUs in your computer.
	 * 
	 */
	public void stressMaximumCPU(int numberOfCPUs)throws InvalidParameterException {
		monitor=new CPUMonitor();
		if(numberOfCPUs<0 || numberOfCPUs>monitor.getNumberOfCPUs() ){
			throw new InvalidParameterException();
		}
		try {
			Runtime.getRuntime().exec("stress --cpu " + numberOfCPUs);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
	/**
	 * Generates an amount of CPU load, as specified by the numberOfCPUs passed as parameter, during a limited time. Each CPU is stressed
	 * with the maximum load possible. The percentage of
	 * CPU utilization resulting from such an amount of CPU load is given by (numberOfCPUs * 100/N), where N is the number
	 * of CPUs in your computer.
	 * 
	 * This percentage of CPU utilization will be maintained for a LIMITED time.
	 *
	 * @param number of CPUs to generate load for.
	 * @param time, in seconds, the CPU load will last.
	 * @throws InvalidParameterException if numberOfCPUs > N AND numberOfCPUs < 0 OR time < 0, where N is the number of CPUs in your computer.
	 */
	public void stressMaximumCPU(int numberOfCPUs,int time)throws InvalidParameterException{
		monitor=new CPUMonitor();
		if(numberOfCPUs<0 || numberOfCPUs>monitor.getNumberOfCPUs()||time<0 ){
			throw new InvalidParameterException();
		}
		try {
			Runtime.getRuntime().exec("stress --cpu "+ numberOfCPUs + " --timeout "+ time+"s");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	
	}
	/**
	 * Generates the maximum amount of CPU load possible. This means that the percentage of CPU utilization
	 * will be 100%. This percentage of CPU utilization will be maintained for an UNLIMITED time.
	 */
	public void stressMaximumCPU(){
		monitor = new CPUMonitor();
		try {
			Runtime.getRuntime().exec("stress --cpu "+ ((monitor.getNumberOfCPUs())+1));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
	/**
	 *  Finishes all processes from the stress program running. The objective is to reset the CPU utilization to 0%.
	 *  Note that such a percentage of CPU utilization will be guaranteed if there are only CPU load resulting from the
	 *  stress program. 
	 */
	public void resetCPUStress(){
		try {
			Runtime.getRuntime().exec("sudo killall stress").waitFor();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

	}

}
