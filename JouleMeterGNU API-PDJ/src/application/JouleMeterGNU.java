package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import memory.MemoryEnergyMeter;
import memory.MemoryMonitor;
import video.VideoEnergyMeter;
import ch.ethz.ssh2.Connection;
import cpu.CPUEnergyMeter;
import disk.DiskEnergyMeter;
import disk.DiskMonitor;


/**
 * The JouleMeterGnu class offer a way to execute a series of experiments supported by the  JouleMeterGNU API and their respective classes,
 * and obtains some important informations about the power of the system components.
 * @author Efficiency Energetic Project
 * @version 1.0
 * 
 *
 */

public class JouleMeterGNU{
	private VideoEnergyMeter videoMeter;
	private CPUEnergyMeter cpuMeter;
	private DiskEnergyMeter diskMeter;
	private MemoryEnergyMeter memoryMeter;
	private DiskMonitor diskMonitor;
	private double[][] cpuMatrix;
	private double videoOn;
	private double videoOff;
	private double maxPowerMemory;
	private double minPowerMemory;
	private double diskInUse;
	private double lazyDisk;
	private int maxWrittenPerSecond;
	private int minWrittenPerSecond;
	private FileWriter cpuInformation;
	private FileWriter videoInformation;
	private FileWriter memoryInformation;
	private FileWriter diskInformation;
	private FileReader memoryReader;
	private FileReader diskReader;
	private FileReader cpuReader;
	private FileReader videoReader;
	private static long actualMemory;
	private int actualFreq;
	private int actualCpus;
	private int actualDisk;
	private double[] memory;
	double maxDisk ,minDisk ,maxWritten,minWritten ,maxVideo,minVideo;

	private FileWriter dataTime;

	private boolean isAuthenticated;
	private Connection serverConnection;


	static Scanner input;

	private static final int BATTERY = 0;
	private static final int IDRAC = 1;
	private static final int YOKOGAWA = 2;




	/**
	 * Execute some experiments supported by the JouleMeterGNU API, analyzing the obtained data e estimating the power of each system component.
	 * This estimate is based in studies and experiments performed previously where each component was analyzed separately to obtain more precision. 
	 */
	public void calibrate(int type, String hostname, String username, String password){
		try {
			dataTime = new FileWriter("/home/tiaraju/dataTime.txt");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String userName = System.getProperty("user.name");
		File diretorio = new File("/home/"+userName+"/JMGProject");

		if(!(diretorio.exists())){
			diretorio.mkdirs();
		}



		/*videoMeter = new VideoEnergyMeter(type,hostname,username,password);
		videoOn=videoMeter.getPowerVideoInUse();
		videoOff=videoMeter.getPowerVideoOutOfUse();

		try {
			videoInformation = new FileWriter(new File("/home/"+userName+"/JMGProject/video.txt"));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			videoInformation.append("ON: "+videoOn+"\n"+"OFF: "+videoOff);
			videoInformation.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		 */		

		MemoryMonitor memoryMonitor = new MemoryMonitor();
		double freeMemory= memoryMonitor.getFreeMemory();
		try {
			dataTime.append("memory starts: "+new Date().toString().substring(11,19)+"\n");
			memoryInformation= new FileWriter(new File("/home/"+userName+"/JMGProject/memory.txt"));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		try {
			for(int i=0;i<=100;i+=25){
				Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+Math.round(freeMemory*(i/100.0))+"K --vm-keep");
				memoryInformation.append(i+"%: "+  new Date().toString().substring(11,19)+"-"+Math.round(freeMemory*(i/100.0)));
				Thread.sleep(30000);
				memoryInformation.append("-"+new Date().toString().substring(11,19)+"\n");
				Runtime.getRuntime().exec("killall stress").waitFor();
				Thread.sleep(5000);
			}
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}


		try {
			memoryInformation.close();
			dataTime.append("memory ends: "+new Date().toString().substring(11,19));
			Thread.sleep(10000);
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}



		/*cpuMeter= new CPUEnergyMeter(type,hostname,username,password);
		try {
			dataTime.append("cpustarts "+ new Date().toString().substring(11,19)+"\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		cpuMatrix = cpuMeter.getPowerCPUInUse();

		try {
			cpuInformation= new FileWriter(new File("/home/"+userName+"/JMGProject/cpu.txt"));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		for(int i=0;i<cpuMatrix.length;i++){
			for(int k=0;k<cpuMatrix[i].length;k++){
				try {
					cpuInformation.append(String.format("%.2f",cpuMatrix[i][k])+" ");
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			try {
				cpuInformation.append("\n");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			cpuInformation.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}


		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			dataTime.append("cpuends "+ new Date().toString().substring(11,19)+"\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			Runtime.getRuntime().exec("killall stress").waitFor();
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		diskMeter = new DiskEnergyMeter(type,hostname,username,password);
		diskMonitor= new DiskMonitor();
		try {
			dataTime.append("lazydiskstarts "+ new Date().toString().substring(11,19)+"\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		lazyDisk=diskMeter.getPowerHdStandBy();
		try {
			//minWrittenPerSecond=diskMonitor.getActualWrittenPerSecond();
			try {
				dataTime.append("harddiskstarts "+ new Date().toString().substring(11,19)+"\n");
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			Runtime.getRuntime().exec("stress --hdd 1 ");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}	
		diskInUse = diskMeter.getPowerHdInUse();
		try {
			dataTime.append("diskends "+ new Date().toString().substring(11,19)+"\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		//maxWrittenPerSecond=diskMonitor.getActualWrittenPerSecond();
		try {
			Runtime.getRuntime().exec("killall stress").waitFor();
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}


			try {
			//diskInformation= new FileWriter(new File("/home/"+userName+"/JMGProject/disk.txt"));
		} catch (IOException e) {
			//System.err.println(e.getMessage());
		}
		 */
		try {
			//diskInformation.append("\nMAXWS: "+maxWrittenPerSecond+"\nMINWS: "+minWrittenPerSecond);
			//diskInformation.close();
			dataTime.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}


	}

	//obtem os valores de como cada componente esta se comportando no momento ( cpu,disco,memoria)
	private void obtainActualSituation(){
		//condição atual da CPU
		/*this.actualFreq=0;
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
		 */
		/*//condição atual do disco
		DiskMonitor diskMonitor = new DiskMonitor();
		this.actualDisk=diskMonitor.getActualWrittenPerSecond();
		 */
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
			//diskReader= new FileReader("/home/"+userName+"/JMGProject/disk.txt");
			//cpuReader = new FileReader("/home/"+userName+"/JMGProject/cpu.txt");
			//videoReader= new FileReader("/home/"+userName+"/JMGProject/video.txt");
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
			System.out.println(line);
			if(line.substring(0,5).contains("50%")) {
				System.out.println("foi 50");
				System.out.println("colocou: "+line.split(" ")[line.split(" ").length-1]);
				memory[2]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("25%")){
				System.out.println("foi 25");
				System.out.println("colocou: "+line.split(" ")[line.split(" ").length-1]);
				memory[1]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("75%")){
				System.out.println("foi 75");
				System.out.println("colocou: "+line.split(" ")[line.split(" ").length-1]);
				memory[3]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("100%")) {
				System.out.println("foi 100");
				System.out.println("colocou: "+line.split(" ")[line.split(" ").length-1]);
				memory[4]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}
			else if(line.substring(0,5).contains("0%")){
				System.out.println("foi 0");
				System.out.println("colocou: "+line.split(" ")[line.split(" ").length-1]);
				memory[0]=Double.parseDouble(line.split(" ")[line.split(" ").length-1]);
			}else{
				break;
			}
			try {
				line=reader.readLine();
			} catch (IOException e) {
				System.out.println("naoleualinha");
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
		/*//valores do disco
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
			if(line.contains("ON")) maxVideo=Double.parseDouble(line.split(" ")[1]);
			if(line.contains("OFF")) minVideo=Double.parseDouble(line.split(" ")[1]);
			try {
				line=reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}	 
*/
	}
	//associa a condiçao atual do componente com o obtido na calibragem
	
	private double[] associateValues(){
		double[] result=new double[5];
		/*//cpu
		CPUEnergyMeter m = new CPUEnergyMeter(0, null, null, null);
		double[][] cpuMatrix=m.getMatrixOfConsumption();
		result[0]= cpuMatrix[actualCpus][actualFreq];
		 */
		//memoria
		MemoryMonitor memoryMonitor = new MemoryMonitor();
	
		System.out.println("");
		int percentageOfUse=roundMemory(actualMemory, memoryMonitor.getTotalMemory());
		
		result[1]=memory[percentageOfUse/25];
				//memory[(roundMemory(actualMemory, memoryMonitor.getTotalMemory()))/25];
		
		/*//disco
		if((maxWritten-actualDisk)<(actualDisk-minWritten)){
			result[2]=maxDisk*(actualDisk/maxWritten);
		}else{
			result[2]=minDisk*(actualDisk/minWritten);
		}*/
		return result;
	}

	private void getValidationData(int type, String hostname, String username, String password){
		String userName = System.getProperty("user.name");
		double[] components=null; 
		try {
			//EnergyMeter meter = new EnergyMeter(type, hostname, username, password);
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

	private void connect(String hostname, String username, String password) throws IOException {
		serverConnection = new Connection(hostname);
		serverConnection.connect();

		isAuthenticated = serverConnection.authenticateWithPassword(username, password);
		if (!isAuthenticated) throw new IOException("Authentication failed.");


	}

	public static void main(String[] args) throws IOException, InterruptedException {	
		/*Scanner input= new Scanner(System.in);
		boolean calibrate = false;

		System.out.println("Welcome to JouleMeter GNU Project. Please, select your device that you will use to get power.");
		System.out.println("Please, type 0 - Notebok's Battery");
		System.out.println("1 - IDRAC Client");
		System.out.println("2 - Yokogawa");
		int choice = input.nextInt();
		String hostname=null,username=null,password=null;
		switch(choice){
		case BATTERY:
			input.close();
			System.out.println("Please, wait!");
			meter.calibrate(BATTERY,null,null,null);
			calibrate = true;
			break;
		case IDRAC:
			//TODO remote access
			System.out.println("Please, type your hostname: ");
			hostname=input.next();
			System.out.println("Please, type your username: ");
			username=input.next();
			System.out.println("Please, type your password: ");
			password=input.next();
			input.close();
			meter.calibrate(IDRAC, hostname, username, password);
			calibrate = true;
			break;
		case YOKOGAWA:
			System.out.println("Please, type your hostname: ");
			hostname = input.next();
			input.close();
			meter.calibrate(YOKOGAWA, hostname,null,null);
			calibrate = true;
			break;
		}*/

		//meter.calibrate(0, null,null, null);
		FileWriter writer = new FileWriter("/home/tiaraju/memoryValidation.txt");
		MemoryMonitor monitor = new MemoryMonitor();
		JouleMeterGNU meter = new JouleMeterGNU();

		meter.loadData();
		for(int i=0;i<=100;i+=10){
			long memory=Math.round(monitor.getFreeMemory()*(i/100.0));
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memory+"K --vm-keep");
			Thread.sleep(30000);
			meter.obtainActualSituation();
			writer.append(i+"-"+memory+": "+new Date().toString().substring(11,19)+"- actualMem: "+actualMemory+"- percentage of use: "+roundMemory(actualMemory, monitor.getTotalMemory())+"-power: "+meter.associateValues()[1]+"\n");
			Runtime.getRuntime().exec("killall stress").waitFor();
			//work.resetStress();
			Thread.sleep(5000);
		}
		writer.close();

	}
}


