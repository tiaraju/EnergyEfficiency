package cpu;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The CPUMonitor class provides descriptive information about the CPUs in your computer system, such as
 * the number of CPUs and the frequencies supported. In this case, processor and cores are treated alike, then,
 * one computer with a dual core processor are considered to have 2 CPUs.
 * 
 * @author Efficiency Energetic Project
 * @version 1.0
 *
 * 
 */
public class CPUMonitor{


	private String[] frequencies;


	/**
	 * Returns the total number of CPUs that exist in your computer.
	 * 
	 * @return the number of CPUs that exists in your computer.
	 * 
	 */
	public int getNumberOfCPUs(){
		Process p=null;
		String[] processadores=null;

		try {
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/related_cpus");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		try {
			processadores = reader.readLine().split(" ");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return processadores.length;
	}

	/**
	 * Returns an array that contains the CPU frequency levels supported in your computer system.
	 * 
	 * @return an array of the CPU frequencies supported in the system.
	 * 
	 */
	public double[] getCPUFrequencyLevels(){
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			frequencies = reader.readLine().split(" ");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		double[] temp=new double[frequencies.length];
		for(int i=0;i<frequencies.length;i++){
			temp[i]=Double.parseDouble(frequencies[i]);
		}
		return temp;
	}
	/**
	 * Returns the current percentage of CPU utilization in your computer. In notebooks, it need to be unplugged.
	 *  
	 * @return the percentage of CPU utilization in your computer.
	 * 
	 */
	public double getCPULoad(){
		String linha=null;
		Process p=null;

		File dir = new File("/proc/acpi/battery");
		String bat=dir.listFiles()[0].getName();
		double[] dados = new double[2];

		try {
			p = Runtime.getRuntime().exec("cat /proc/acpi/battery/"+bat+"/state");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream input = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		List<String> state = new ArrayList<String>();

		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		while (linha != null) {
			state.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		dados[0] = Double.parseDouble(state.get(3).split(" ")[13]);
		dados[1] = Double.parseDouble(state.get(5).split(" ")[10]);

		return dados[0]*dados[1];
	}
	/**
	 * Returns an array of size "n" with the frequencies in use for each of the "n" CPUs in your computer. 
	 * Each position in the array is associated with one CPU in the system; it is not possible to distinguish which
	 * CPU has each frequency.
	 * 
	 * @return a array of CPU frequencies in use, one for each CPU in your computer.
	 * @throws IOException.
	 */
	public double[] getcurrentCPUFrequencyLevels(){
		Process p=null;
		String linha=null;

		double[] currentfreq = new double[getNumberOfCPUs()];
		List<String> temp = new ArrayList<>();
		for(int i=0;i<getNumberOfCPUs();i++){

			try {
				p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu"+i+"/cpufreq/scaling_cur_freq");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			InputStream input = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			while (linha != null) {
				temp.add(linha);
				try {
					linha = reader.readLine();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			try {
				reader.close();
				input.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			p.destroy();		
		}
		for(int i=0;i<currentfreq.length;i++){
			currentfreq[i]=Double.parseDouble(temp.get(i));
		}

		return currentfreq;
	}

}
