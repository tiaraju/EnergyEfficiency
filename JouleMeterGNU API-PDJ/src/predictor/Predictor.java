package predictor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import memory.MemoryMonitor;
import wattage.EnergyMeter;
import cpu.CPUEnergyMeter;
import cpu.CPUMonitor;
import disk.DiskMonitor;

public class Predictor {

	private int actualFreq;
	private int actualCpus;
	private int actualDisk;
	private long actualMemory;
	private double[] memory;
	private FileReader memoryReader;
	private FileReader diskReader;
	private FileReader videoReader;
	private double maxDisk;
	private double minDisk;
	private double maxWritten;
	private double minWritten;
	private double maxVideo;
	private double minVideo;
	
	
	/**
	 * this method returns an array that represents the current estimative of cpu,memory,disk,video, respectively.
	 * The index 0 represents the cpu's power, 1 memory, 2 disk, 3 video 
	 * @return
	 */
	public double[] predict(){
		obtainActualSituation();
		loadData();
		double[] result= associateValues();
		return result;
	}
	
	/**
	 * this method returns an array that represents the current status of system
	 * The index 0 represents the actual freq, 1 actual cpus, 2 actual percentage of used memory, 3 used disk
	 * @return
	 */
	public double[] currentUse(){
		double[] result = new double[4];
		obtainActualSituation();
		result[0]= actualFreq;
		result[1]= actualCpus;
		result[2]=actualMemory;
		result[3]=actualDisk;
		return result;
	}

	//obtem os valores de como cada componente esta se comportando no momento ( cpu,disco,memoria)
	private void obtainActualSituation(){
		//condição atual da CPU
		this.actualFreq=0;
		CPUMonitor cpuMonitor= new CPUMonitor();
		this.actualCpus =(int)Math.round((cpuMonitor.getNumberOfCPUs()*((double)cpuMonitor.getCPULoad())/100));
		System.out.println(actualCpus);
		int numberOfCpus;
		if(actualCpus == 0){numberOfCpus=actualCpus;}else{  numberOfCpus=actualCpus-1;}
		double freq=cpuMonitor.getActualFreq(numberOfCpus);
		List<Double> freqs= new ArrayList<Double>();
		for(int i=cpuMonitor.getCPUFrequencyLevels().length-1;i>=0;i--){
			freqs.add(cpuMonitor.getCPUFrequencyLevels()[i]);
		}
		actualFreq = freqs.indexOf(freq);

		//condição atual do disco
		DiskMonitor diskMonitor = new DiskMonitor();
		this.actualDisk=diskMonitor.getActualWrittenPerSecond();

		//condição atual da memoria
		MemoryMonitor memoryMonitor = new MemoryMonitor();
		actualMemory =memoryMonitor.getUsedMemory();

		// TODO condição atual do video
	}
	
	
	
	//carrega os dados obtidos na calibragem
	private void loadData(){
		this.memory=new double[5];
		BufferedReader reader;
		String userName = System.getProperty("user.name");
		try {
			memoryReader= new FileReader("/home/"+userName+"/memoryResult.txt");
			diskReader= new FileReader("/home/"+userName+"/JMGProject/disk.txt");
			videoReader= new FileReader("/home/"+userName+"/JMGProject/video.txt");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

		//valores da memoria
		reader = new BufferedReader(memoryReader);
		String line=null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		while(line!=null){
			if(line.substring(0,5).contains("50%")) {
				memory[2]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("25%")){
				memory[1]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("75%")){
				memory[3]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("100%")) {
				memory[4]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("0%")){
				memory[0]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}else{
				break;
			}
			try {
				line=reader.readLine();
			} catch (IOException e) {
			}
		}
		try{
			reader.close();
			memoryReader.close();
			for(Double d:memory){
				System.out.println(d);
			}
		}catch(Exception e){
		}
		//valores do disco
		reader=new BufferedReader(diskReader);
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		while(line!=null){
			if(line.contains("ON")) maxDisk=Double.parseDouble(line.split(" ")[1]);
			if(line.contains("OFF")) minDisk=Double.parseDouble(line.split(" ")[1]);
			if(line.contains("MAXWS")) maxWritten=Double.parseDouble(line.split(" ")[1]);
			if(line.contains("MINWS")) minWritten=Double.parseDouble(line.split(" ")[1]);
			try {
				line=reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		//valores do video
		reader= new BufferedReader(videoReader);
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		while(line!=null){
			if(line.contains("ON")) this.maxVideo=Double.parseDouble(line.split(" ")[1]);
			if(line.contains("OFF")) this.minVideo=Double.parseDouble(line.split(" ")[1]);
			try {
				line=reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}	 

	}
	
	
	//associa a condiçao atual do componente com o obtido na calibragem

	private double[] associateValues(){
		double[] result=new double[4];
		//cpu
		CPUEnergyMeter m = new CPUEnergyMeter(0, null, null, null);
		double[][] cpuMatrix=m.getMatrixOfConsumption();
		result[0]= cpuMatrix[actualCpus-1][actualFreq-1];

		//memoria
		MemoryMonitor memoryMonitor = new MemoryMonitor();

		System.out.println("");
		int percentageOfUse=roundMemory(actualMemory, memoryMonitor.getTotalMemory());

		result[1]=memory[percentageOfUse/25];
		//memory[(roundMemory(actualMemory, memoryMonitor.getTotalMemory()))/25];

		//disco
		if((maxWritten-actualDisk)<(actualDisk-minWritten)){
			result[2]=maxDisk*(actualDisk/maxWritten);
		}else{
			result[2]=minDisk*(actualDisk/minWritten);
		}
		return result;
	}

	
	/**
	 * this method is used to validate an experiment.
	 * @param type
	 * @param hostname
	 * @param username
	 * @param password
	 */
	private void getValidationData(int type, String hostname, String username, String password){
		String userName = System.getProperty("user.name");
		double[] components=null; 
		try {
			EnergyMeter meter = new EnergyMeter(type, hostname, username, password);
			FileWriter validationFile = new FileWriter("/home/"+userName+"/JMGProject/validation.txt");
			obtainActualSituation();
			components=associateValues();
			validationFile.append("CPU: Estimate - "+components[0]+" f "+actualFreq+" c "+actualCpus+"\n");
			obtainActualSituation();
			components=associateValues();
			validationFile.append("MEMORY: Estimate - "+components[1]);
			obtainActualSituation();
			components=associateValues();
			validationFile.append("DISK: Estimate - "+components[2]);
			validationFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int roundMemory(double actualMemory,double totalMemory){ 
		double percentageOfUse =actualMemory*100.0/totalMemory;
		int result=0;
		if(percentageOfUse < 50){
			if(percentageOfUse<=12){
				result= 0;
			}else if(percentageOfUse<=34){
				result= 25;
			}else{
				result= 50;
			}
		}else if(percentageOfUse > 50){
			if(percentageOfUse<=62){
				result= 50;
			}else if(percentageOfUse<=84){
				result= 75;
			}else{
				result= 100;
			}
		}else{
			result= 50;
		}
		return result;
	}
	
	



}
