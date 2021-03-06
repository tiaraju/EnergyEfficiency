package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import cpu.CPUEnergyMeter;
import cpu.CPUMonitor;

public class WatchStatus {

	//this class is responsible for watch the current status of the components and associate a power value to that status. 
	// Its used to compare the real value and the estimative ( CPU at first)

	int actualCpus;
	int actualFreq;
	double[][] matrix;
	CPUMonitor cpuMonitor;
	FileWriter result;
	final String userName;


	public WatchStatus(){
		matrix=new CPUEnergyMeter(0,null,null,null).getMatrixOfConsumption();
		cpuMonitor= new CPUMonitor();
		userName=System.getProperty("user.name");
		try {
			result= new FileWriter("/home/"+userName+"/JMGProject/watchingStatus.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private void getStatus(){
		this.actualFreq=0;
		this.actualCpus =(int)Math.round((cpuMonitor.getNumberOfCPUs()*((double)cpuMonitor.getCPULoad())/100));
		System.out.println(actualCpus);
		if(actualCpus != 0){actualCpus--;}
		double freq=cpuMonitor.getActualFreq(actualCpus);
		List<Double> freqs= new ArrayList<Double>();
		for(int i=cpuMonitor.getCPUFrequencyLevels().length-1;i>=0;i--){
			freqs.add(cpuMonitor.getCPUFrequencyLevels()[i]);
		}
		actualFreq = freqs.indexOf(freq);
	}


	private double associateValues(){
		getStatus();
		return matrix[actualCpus][actualFreq];
	}
	/**
	 *  this method watch your system for how long time ( in seconds) you want to. It will write in a file the result with the hour, number of cpus, 
	 *  index of freq and estimative power
	 * @param time in seconds
	 */
	public void watch(int time){
		BufferedWriter writer = new BufferedWriter(result);
		double power=associateValues();
		String hour= new Date().toString().substring(11,19);
		
		for(int i=0;i<time;i++){
			try {
				writer.append(hour+"-"+"cpus:"+actualCpus+"freq:"+actualFreq+"-"+power+"\n");
				Thread.sleep(1000);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			 power=associateValues();
			 hour= new Date().toString().substring(11,19);
		}
		
		try {
			writer.close();
			result.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		WatchStatus watcher = new WatchStatus();
		System.out.println("Digite o tempo que deseja em segundos: ");
		Scanner sc = new Scanner(System.in);
		int time = sc.nextInt();
		watcher.watch(time);
		sc.close();
	}


}
