package cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wattage.EnergyMeter;




/**
 * The CPUEnergyMeter class represents a software meter that gives information about the power consumption
 * of the CPUs in your computer system. With an object of CPUEnergyMeter,
 * it is possible to obtain some important information like the CPU power consumption with maximum LOAD 
 * or the CPU power when considering different frequencies. <br>
 * <p>
 * It is used a workload generator to produce CPU load. The maximum CPU load means 100% of CPU utilization.
 * <p>
 * The power consumption information is presented as a matrix where lines represent the number of CPUs in use 
 * and columns represent the number of frequencies supported.
 * Consider the example below:
 * {{a b c}, {d e f}}
 * <p>
 * There are 2 CPUs in this system and 3 possible frequencies. The values in the first line represents the average CPU power
 * with one CPU overloaded (50% of CPU utilization in the system) and a different frequency per column. In the same sense,
 * the values in the second line represents the CPU power with the two CPUs overloaded (100% of CPU utilization in the system). 
 * The power information is obtained by means of experiments whose results are associated with a low percentage error.
 * 
 * @author Efficiency Energetic Project
 * @version 1.0
 * 
 */

public class CPUEnergyMeter{

	private CPUMonitor monitor;
	private CPUWorkloadGenerator workGenerator;
	private double[][] matrixOfConsumption;
	private FileReader reader = null;
	private FileWriter writer= null;
	private List<Double> average;
	private List<String> overallAverage;
	private String[] frequencies;
	private int numProcessors;
	private EnergyMeter getWattage;


	public CPUEnergyMeter(int type,String hostName,String userName,String password){
		try {
			this.getWattage = new EnergyMeter(type, hostName, userName, password);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}


	/**
	 * If the matrix of consumption already exists in your computer, it will be returned. To generate another one,
	 * you should call the  getPowerCPUInUse method again.
	 * @return a matrix of consumption that already was generated in your computer.
	 */
	public double[][] getMatrixOfConsumption(){
		monitor = new CPUMonitor();
		String userName = System.getProperty("user.name");	
		double[][] matrix;
		if(new File("/home/"+userName+"/JMGProject/cpu.txt").exists()){
			try {
				matrix = new double[monitor.getNumberOfCPUs()][monitor.getCPUFrequencyLevels().length];
				BufferedReader reader = new BufferedReader(new FileReader("/home/"+userName+"/JMGProject/cpu.txt"));
				String line = reader.readLine();
				while(line != null){
					for(int i=0;i<matrix.length;i++){
						if(line == null){
							break;
						}
						for(int k=0;k<matrix[i].length;k++){
							if(line != " "){
								if(!(line.split(" ")[k].equals(" "))){
									matrix[i][k]=Double.valueOf(line.split(" ")[k]);
								}
							}
						}
						line=reader.readLine();

					}
				}
				reader.close();
				return matrix;

			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		return null;
	}
	/**
	 * Generates a matrix m x n of the average power consumption by CPU in different scenarios.
	 * Power consumption information is obtained through 
	 * experiments (m*n) that explore the maximum CPU load in scenarios of 1..m CPUs in use with different frequencies (n is the number of frequencies supported). 
	 * Each experiment consists of 10 measurements at every 3 seconds, with a guarantee of ERROR_PERCENTUAL of error.
	 * 
	 * @return a matrix m x n where each value represents the average power consumed by using "m" CPUs with "n" frequencies.
	 * 
	 */
	public double[][] getPowerCPUInUse(){

		String userName = System.getProperty("user.name");		
		monitor= new CPUMonitor();
		Process p=null;		

		try {
			if(!(new File("/home/"+userName+"/JMGProject").exists())){
				Runtime.getRuntime().exec("mkdir home/"+userName+"/JMGProject");
			}
			matrixOfConsumption = new double[monitor.getNumberOfCPUs()][monitor.getCPUFrequencyLevels().length];
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			frequencies = reader.readLine().split(" ");
			//Runtime.getRuntime().exec("xset +dpms");
			//Runtime.getRuntime().exec("xset dpms 0 0 1");
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
		}		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		try {
			numProcessors = getNumberOfProcessors();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		for (int i = 1; i <= numProcessors; i++) {
			overallAverage = new ArrayList<String>();
			try {
				Runtime.getRuntime().exec("stress --cpu " + i);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			try {
				measures(i,300);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			try {
				Runtime.getRuntime().exec("sudo killall stress").waitFor();
				getData(i-1,overallAverage,matrixOfConsumption);
			} catch (InterruptedException | IOException e) {
				System.err.println(e.getMessage());
			}	
		}
		try {
			Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return matrixOfConsumption;
	}

	/**
	 * Generates a matrix m x n of the average power consumption by CPU in different scenarios, during a limited time.
	 * Power consumption information is obtained through 
	 * experiments (m*n) that explore the maximum CPU load in scenarios of 1..m CPUs in use with different frequencies (n is the number of frequencies supported). 
	 * Each experiment consists of 10 measurements at every "time" seconds (as specified by the user), with a guarantee of ERROR_PERCENTUAL of error.
	 * 
	 * @param time in seconds of each measurement (time >= 3), where 3 is the minimum default value.
	 * @return a matrix m x n where each value represents the average power consumed by using "m" CPUs with "n" frequencies.
	 * @throws InvalidParameterException if the time passed as parameter is invalid (time < 3s).
	 * 
	 */
	public double[][] getPowerCPUInUse(int time)throws InvalidParameterException {


		monitor= new CPUMonitor();
		Process p = null;

		if(time<3){
			throw new InvalidParameterException();
		}

		try {
			matrixOfConsumption = new double[monitor.getNumberOfCPUs()][monitor.getCPUFrequencyLevels().length];
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			frequencies = reader.readLine().split(" ");
			Runtime.getRuntime().exec("xset +dpms");
			Runtime.getRuntime().exec("xset dpms 0 0 1");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		try {
			numProcessors = getNumberOfProcessors();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		for (int i = 1; i <= numProcessors; i++) {
			overallAverage = new ArrayList<String>();
			try {
				Runtime.getRuntime().exec("stress --cpu " + i);
			} catch (IOException e1) {
				System.err.println(e1.getMessage());
			}
			try {
				measures(i,(time*1000));
			} catch (Exception e1) {
				System.err.println(e1.getMessage());
			}
			try {
				Runtime.getRuntime().exec("sudo killall stress").waitFor();
				getData(i-1,overallAverage,matrixOfConsumption);	
			} catch (InterruptedException | IOException e) {
				System.err.println(e.getMessage());
			}

		}
		try {
			Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return matrixOfConsumption;
	}

	private  void measures(int num,int time) throws Exception {
		for (int i = frequencies.length - 1; i >= 0; i--) {
			average = new ArrayList<Double>();
			double freq = Integer.parseInt(frequencies[i]) * 0.000001;
			analyzeFreq(String.valueOf(freq),time);
		}
	}

	private int getNumberOfProcessors() throws Exception {
		Process p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/related_cpus");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String[] processadores = reader.readLine().split(" ");
		return processadores.length;
	}
	private void analyzeFreq(String freq,int intervaloDeTempo) throws Exception  {
		final int INTERVALO_DE_TEMPO = intervaloDeTempo;
		try {
			List<Double> medicoes = new ArrayList<Double>();
			long timeSleep;
			changeFreq(freq);
			Thread.sleep(2000);	
			System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				timeSleep = System.currentTimeMillis();
				if((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0){
					Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
				}	

				medicoes.add(takeData());		
			} 
			double media = getAverage(medicoes);
			average.add(media);
			if (average.size() == 1) {
				analyzeFreq(freq,intervaloDeTempo);
			} else {
				if (isRecursive()) {
					analyzeFreq(freq,intervaloDeTempo);
				}
				else{
					overallAverage.add(String.valueOf(average.get(average.size() - 1)));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}


	private void changeFreq(String freq) throws Exception {
		for (int i = 0; i <=numProcessors; i++) {
			String comando = "sudo cpufreq-set c " + i + " -g userspace && sudo cpufreq-set c " + i + " -r -f " + freq + "Ghz";
			writeSH(comando);
			Runtime.getRuntime().exec("sh teste.sh");
		}
	}
	private boolean isRecursive() {
		int indice = average.size() - 1;
		double last = average.get(indice);
		for (int i = 0; i < indice; i++) {
			if (Math.abs(last - average.get(i)) < 0.6) {
				return false;
			}
		}
		return true;
	}

	private double getAverage(List<Double> medicoes) {
		double soma = 0;
		Iterator<Double> it = medicoes.iterator();
		double next;
		while (it.hasNext()) {
			next = it.next();
			soma += next;
		}
		return soma / medicoes.size();
	}

	private double takeData() throws IOException {
		return getWattage.getWattage();
	}

	private void getData(int num,List<String> in2,double[][] matrix) throws IOException{
		for(int i=0;i<matrix[num].length;i++){
			matrix[num][i]=Double.parseDouble(overallAverage.get(i));
		}


	}

	private void writeSH(String comando) throws IOException {
		FileWriter writer = new FileWriter("teste.sh");
		writer.write(comando);
		writer.close();

	}

	/**
	 * Generates an array of size n where each value represents the average CPU power when using all CPUs 
	 * in the system with each of the n frequencies supported. It provides power information when there is 
	 * 100% of cpu utilization in the system.
	 * @return an array of the average CPU power with all CPUs in use at different frequencies.
	 * 
	 */
	public double[] getPowerMaximumCPUInUse(){
		monitor= new CPUMonitor();
		String userName = System.getProperty("user.name");
		workGenerator = new CPUWorkloadGenerator();
		workGenerator.stressMaximumCPU();
		List<Double> med=new ArrayList<Double>();
		double[] result;
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			frequencies = reader.readLine().split(" ");
			Runtime.getRuntime().exec("xset +dpms");
			Runtime.getRuntime().exec("xset dpms 0 0 1");
			Thread.sleep(3000);
		} catch (IOException | InterruptedException e1) {
			System.err.println(e1.getMessage());
		}


		for(int k=frequencies.length-1;k>=0;k--){
			final int INTERVALO_DE_TEMPO = 3000;
			try {
				List<Double> medicoes = new ArrayList<Double>();
				long timeSleep;
				try {
					changeFreq(frequencies[k]);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
				Thread.sleep(2000);	
				System.currentTimeMillis();
				for (int i = 0; i < 10; i++) {
					timeSleep = System.currentTimeMillis();
					if((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0){
						Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
					}	
					medicoes.add(takeData());		
				} 
				double media = getAverage(medicoes);
				med.add(media);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}
		result=new double[med.size()];
		for(int i=0;i<med.size();i++){
			result[i]=med.get(i);
		}
		try {
			Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		workGenerator.resetCPUStress();
		return result;
	}

	/**
	 * Generate a value that represents the current system power. This value represents the power with the least possible use
	 * of CPU. To assure a better result, it is important close all of the running applications.  
	 * @return a value that represents the current system power with least possible use of CPU.
	 * 
	 */
	public double getPowerCPUIdle(){
		double result =0;
		try {
			result= getWattage.getWattage();
		} catch (NumberFormatException | IOException e) {
			System.err.println(e.getMessage());
		}

		return result;
	}


}
