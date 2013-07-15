package memory;

import java.io.IOException;

public class MemoryWorkLoadGenerator {
	
	MemoryMonitor monitor = new MemoryMonitor();
	
	/**
	 * 
	 */
	public void stressMaximumMemory(){
		try {
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+monitor.getFreeMemory()+"K --vm-keep");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	/**
	 * 
	 * @param memory
	 */
	public void stressMemory(double memory){
		try {
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memory+"K --vm-keep");
		} catch (IOException e) {
			System.out.println("nao estressa");
		}
	}
	/**
	 * 
	 * @param time
	 * @param memory
	 */
	public void stressMemory(int time, double memory){
		try {
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memory+"K --vm-keep --timeout "+time+"s");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	/**
	 * 
	 */
	public void resetStress(){
		try {
			Runtime.getRuntime().exec("sudo killall stress").waitFor();
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

}
