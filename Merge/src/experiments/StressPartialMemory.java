package experiments;

import java.io.FileWriter;
import java.util.Date;

import memory.MemoryMonitor;

public class StressPartialMemory {

	public void stressMemory() throws Exception{
		MemoryMonitor monitor = new MemoryMonitor();
		FileWriter writer = new FileWriter("/home/tiaraju/stressmemory.txt");
		long totalMemory = Math.round(monitor.getTotalMemory());

		for(int i=25;i<=100;i+=25){
				Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+Math.round(totalMemory*(i/100.0))+"K --vm-keep");
				writer.append(new Date().toString().subSequence(11, 19)+ "-"+ Math.round(totalMemory*(i/100.0))+"-");
				Thread.sleep(30000);
				Runtime.getRuntime().exec("killall stress").waitFor();
				writer.append(new Date().toString().subSequence(11, 19)+"\n");
				Thread.sleep(5000);
				
		}
		writer.close();

	}


	public static void main(String[] args)throws Exception {
		StressPartialMemory test = new StressPartialMemory();
		test.stressMemory();
	}
}
