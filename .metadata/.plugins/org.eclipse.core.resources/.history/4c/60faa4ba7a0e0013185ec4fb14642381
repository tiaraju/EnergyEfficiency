package experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import cpu.CPUMonitor;
import cpu.CPUWorkloadGenerator;

public class ChangeFreqs {

	CPUMonitor cpuMonitor;
	CPUWorkloadGenerator work;
	FileWriter writer;
	double freq;


	public void modifyFreq()throws Exception{
		cpuMonitor= new CPUMonitor();
		work= new CPUWorkloadGenerator();
		writer = new FileWriter("/home/tiaraju/resultados/freqstimes.txt");
		double[] freqs = cpuMonitor.getCPUFrequencyLevels();
		int numOfCpus = cpuMonitor.getNumberOfCPUs();
		for(int k=0;k<numOfCpus;k++){
			for (int i = freqs.length - 1; i >= 0; i--) {
				freq =(freqs[i])* 0.000001;
				String comando = "sudo cpufreq-set c " + k + " -g userspace && sudo cpufreq-set c " + k + " -r -f " + freq + "Ghz";
				writeSH(comando);
				Runtime.getRuntime().exec("sh teste.sh");
				work.stressMaximumCPU(k);
				saveTime(writer,k,freqs.length-i);
				Thread.sleep(10000);
				work.resetCPUStress();
			}
			
		}
		writer.close();
	}

	private void writeSH(String comando) throws IOException {
		FileWriter writer = new FileWriter("teste.sh");
		writer.write(comando);
		writer.close();
	}

	private void saveTime(FileWriter writer,int cpu,int freq)throws Exception{
		writer.append("cpus: "+cpu+"-freq: "+freq+"-"+new Date().toString().substring(11,19)+"\n");
	}
	
	public static void main(String[] args) throws Exception{
		ChangeFreqs c = new ChangeFreqs();
		c.modifyFreq();
	}
}
