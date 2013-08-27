package cpu;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
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
		String line=null;
		int numberOfCpus=0;
		String userName = System.getProperty("user.name");	
		try {
			FileWriter writer = new FileWriter("proc.sh");
			writer.append("egrep \"^processor\" /proc/cpuinfo | wc -l > /home/"+userName+"/data.txt");
			writer.close();
			Runtime.getRuntime().exec("sh proc.sh").waitFor();
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new FileReader("/home/"+userName+"/data.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			line=reader.readLine();
			numberOfCpus = Integer.parseInt(line);
			reader.close();
		} catch (IOException e) {
			System.out.println("nao deu");
		}

		return numberOfCpus;
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
	 * Returns the current percentage of CPU utilization in your computer.
	 *  
	 * @return the percentage of CPU utilization in your computer.
	 * 
	 */
	public double getCPULoad(){

		String userName = System.getProperty("user.name");	
		FileWriter writer;
		String line=null;
		try {
			writer = new FileWriter("t.sh");
			writer.write("top -b -d 0.9 -n2 | grep 'Cpu(s)' | tail -1 > /home/"+userName+"/top.txt");
			writer.close();
			Runtime.getRuntime().exec("sh t.sh").waitFor();
			BufferedReader r = new BufferedReader(new FileReader("/home/"+userName+"/top.txt"));
			line = r.readLine();
			r.close();
			writer.close();
		} catch (IOException | InterruptedException e) {
			System.out.println("nao atualizou a linha");
		}
		if(line != null){
			String percentage = line.split(",")[0].split(":")[1];
			System.out.println(percentage);
			int indexOfPercent = percentage.indexOf("u");
			return Double.parseDouble(percentage.substring(0,indexOfPercent-1));
		}
		return -5;

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

	public double getActualFreq(int cpu){

		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu"+cpu+"/cpufreq/scaling_cur_freq");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line=null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Double.parseDouble(line);
	}

	public static void main(String[] args) {
		CPUMonitor m = new CPUMonitor();
		System.out.println(m.getCPULoad());
	}
}
