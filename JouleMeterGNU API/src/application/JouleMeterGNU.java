package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import memory.MemoryEnergyMeter;
import memory.MemoryMonitor;
import video.VideoEnergyMeter;
import wattage.EnergyMeter;
import ch.ethz.ssh2.Connection;
import cpu.CPUEnergyMeter;
import cpu.CPUMonitor;
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
	private double actualMemory;
	private int actualFreq;
	private int actualCpus;
	private int actualDisk;

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


		String userName = System.getProperty("user.name");
		File diretorio = new File("/home/"+userName+"/JMGProject");

		if(!(diretorio.exists())){
			diretorio.mkdirs();
		}



		videoMeter = new VideoEnergyMeter(type,hostname,username,password);
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

		MemoryMonitor memoryMonitor = new MemoryMonitor();
		double maxUsedMemory=0,minUsedMemory=0;
		minUsedMemory=memoryMonitor.getUsedMemory();
		memoryMeter = new MemoryEnergyMeter(type,hostname,username,password);
		minPowerMemory = memoryMeter.getMininumMemoryInUse();
		try {
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memoryMonitor.getFreeMemory()+"K --vm-keep");
			Thread.sleep(10000);
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}

		maxPowerMemory = memoryMeter.getTotalMemoryInUse();
		maxUsedMemory=memoryMonitor.getUsedMemory();


		try {
			Runtime.getRuntime().exec("killall stress").waitFor();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			memoryInformation= new FileWriter(new File("/home/"+userName+"/JMGProject/memory.txt"));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			memoryInformation.append("MinUsed: "+minUsedMemory+"\nMaxUsed: "+maxUsedMemory + "\nMAX: "+maxPowerMemory+"\n"+"MIN: "+minPowerMemory);
			memoryInformation.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		cpuMeter= new CPUEnergyMeter(type,hostname,username,password);
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

		diskMeter = new DiskEnergyMeter(type,hostname,username,password);
		diskMonitor= new DiskMonitor();
		lazyDisk=diskMeter.getPowerHdStandBy();
		try {
			minWrittenPerSecond=diskMonitor.getActualWrittenPerSecond();
			Runtime.getRuntime().exec("stress --hdd 1 --hdd-bytes");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}	
		diskInUse = diskMeter.getPowerHdInUse();
		maxWrittenPerSecond=diskMonitor.getActualWrittenPerSecond();
		try {
			Runtime.getRuntime().exec("killall stress").waitFor();
		} catch (IOException | InterruptedException e) {
			System.err.println(e.getMessage());
		}
		try {
			diskInformation= new FileWriter(new File("/home/"+userName+"/JMGProject/disk.txt"));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			diskInformation.append("ON: "+diskInUse+"\nOFF: "+lazyDisk+"\nMAXWS: "+maxWrittenPerSecond+"\nMINWS: "+minWrittenPerSecond);
			diskInformation.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	private void connect(String hostname, String username, String password) throws IOException {
		serverConnection = new Connection(hostname);
		serverConnection.connect();

		isAuthenticated = serverConnection.authenticateWithPassword(username, password);
		if (!isAuthenticated) throw new IOException("Authentication failed.");
	}

	public static void main(String[] args) throws IOException, InterruptedException {	
		JouleMeterGNU meter = new JouleMeterGNU();
		Scanner input= new Scanner(System.in);
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
		}

		//if(calibrate){
		//	meter.getValidationData(choice, hostname, username, password);
		//}

	}
}



