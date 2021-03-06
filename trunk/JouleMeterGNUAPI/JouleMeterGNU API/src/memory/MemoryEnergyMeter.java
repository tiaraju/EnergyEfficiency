package memory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import wattage.EnergyMeter;
import cpu.CPUWorkloadGenerator;



/**
 * 
 * @author tiaraju
 *
 */
public class MemoryEnergyMeter {
	
	private EnergyMeter wattage;
	
	public MemoryEnergyMeter (int type,String hostName,String userName,String password){
		/*try {
			this.wattage = new EnergyMeter(type, hostName, userName, password);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}*/
	}
	
	private  ArrayList<Double> mediasMemoria;
	
	
	public double[] getPartialMemory(){
		return new double[0];
	}
		
	/**
	 * 
	 * @return
	 */
	public double getTotalMemoryInUse(){
		
		this.mediasMemoria= new ArrayList<Double>();
		double result=0;
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream in = (InputStream) p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha=null;
		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		for(int i=0;i<=1;i++){
			memoryInfo.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		String[] temp =memoryInfo.get(1).split(":")[1].split(" ");
		String freeMemory=(temp[temp.length-2]);
		try {
			Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+freeMemory+"K --vm-keep");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} 
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		try {
			result=fazMedicao();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
			
		return result;
		
	}
	/**
	 * 
	 * @return
	 */
	public double getMininumMemoryInUse(){
		double result =0;

		this.mediasMemoria= new ArrayList<Double>();
		try {
			//Runtime.getRuntime().exec("xset +dpms");
			//Runtime.getRuntime().exec("xset dpms 0 0 1");
			Thread.sleep(10000);
			result= fazMedicao();
			//Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	
	private double fazMedicao()throws Exception{
		for(int k=0;k<5;k++){
			double media = 0;
			for(int i=0;i<10;i++){
				media+=pegaDados();
				Thread.sleep(1000);
			}		
			mediasMemoria.add(media/10);
		}
		int result =0;
		for(int i=0;i<mediasMemoria.size();i++){
			result+=mediasMemoria.get(i);
		}
		if(mediasMemoria.size()>1){
			if(isRecursive()){
				fazMedicao();		
			}
		}
	
		return result/mediasMemoria.size();
		
	}
	
	public   void stressCPU() throws InterruptedException, IOException{
		CPUWorkloadGenerator work = new CPUWorkloadGenerator();
		work.stressMaximumCPU();
		Thread.sleep(5000);
		double p1 = pegaDados();
		Thread.sleep(5000);
		double p2 = pegaDados();
		while(Math.abs(p2-p1)>0.2){
			p1=p2;
			Thread.sleep(5000);
			p2=pegaDados();
		}
	}
	

	private   double pegaDados() throws IOException {
		return 0;

	}

	private boolean isRecursive() {
		int indice = mediasMemoria.size() - 1;
		double last = mediasMemoria.get(indice);
		for (int i = 0; i < indice; i++) {
			if (Math.abs(last - mediasMemoria.get(i)) < 0.5){
				System.out.println(last-mediasMemoria.get(i));
				return false;
			}
		}
		return true;
	}

}
