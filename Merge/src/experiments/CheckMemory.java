package experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import memory.MemoryMonitor;

public class CheckMemory {

	//classe que faz validação dos experimentos de memoria baseada na base de dados coletados na calibragem



	public long associateValue()throws Exception{
		long actualMem=0;
		long maxMem=0;
		long minMem=0;
		long maxPower=0;
		long minPower=0;
		long result =0;
		MemoryMonitor memMonitor = new MemoryMonitor();
		FileReader r = new FileReader("/home/tiaraju/JMGProject/memory.txt");
		BufferedReader reader = new BufferedReader(r);
		String line = reader.readLine();

		while(line != null){
			if(line.contains("MaxUsed"))maxMem=Math.round(Double.parseDouble(line.split(" ")[1]));
			else if(line.contains("MinUsed"))minMem=Math.round(Double.parseDouble(line.split(" ")[1]));
			else if(line.contains("MinMemory")) minPower=Math.round(Double.parseDouble(line.split(" ")[1]));
			else if(line.contains("Maxmemory"))maxPower=Math.round(Double.parseDouble(line.split(" ")[1]));
			line=reader.readLine();
		}
		reader.close();
		r.close();
		actualMem = Math.round(memMonitor.getUsedMemory());
		System.out.println("actual: "+actualMem+", max: "+maxMem+", min: "+ minMem);
		
		if((maxMem-actualMem) < (actualMem-minMem)){
			System.out.println("pegou a max");
			System.out.println(actualMem+"*"+maxPower+"/"+maxMem);
			result= (actualMem*maxPower)/maxMem;
		}
		else{
			System.out.println("pegou a min");
			System.out.println(actualMem+"*"+minPower+"/"+minMem);
			result =(actualMem*minPower)/minMem;
			if(result<160) result=160;
		}

		return result;
	}

	public static void main(String[] args) throws Exception{
		FileWriter writer = new FileWriter("/home/tiaraju/memoryValidation.txt");
		CheckMemory m = new CheckMemory();
		MemoryMonitor monitor = new MemoryMonitor();
		long totalMemory =Math.round( monitor.getTotalMemory());
		long memory=0;
		for(int i=0;i<=100;i+=10){
			memory=Math.round(totalMemory*(i/100.0));
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memory+"K --vm-keep");
			//Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+memory+"K --vm-keep");
			Thread.sleep(30000);
			writer.append(i+": "+new Date().toString().substring(11,19)+"-"+m.associateValue()+"\n");
			Runtime.getRuntime().exec("killall stress").waitFor();
			//work.resetStress();
			Thread.sleep(5000);
		}
		writer.close();
	}
}
