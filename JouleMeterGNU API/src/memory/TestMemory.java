package memory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cpu.CPUWorkloadGenerator;


public class TestMemory {

	private static ArrayList<Double> mediasMemoria= new ArrayList<Double>();
	private static ArrayList<String> infobateria= new ArrayList<String>();
	


	public static void main(String[] args)throws Exception {

		fazMedicao("/home/tiaraju/lowMemoryUse.txt");


		FileWriter out = new FileWriter("/home/tiaraju/cpuInUse.txt");
		stressCPU();
		
		for(int i=0;i<10;i++){
			out.write("valor com a cpu em 100%: "+pegaDados()+"\n");
			Thread.sleep(2000);
		}
		out.close();
		
		Process p = Runtime.getRuntime().exec("cat /proc/meminfo");
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha = reader.readLine();
		for(int i=0;i<=1;i++){
			memoryInfo.add(linha);
			linha = reader.readLine();
		}

		String freeMemory=(memoryInfo.get(1).split(" ")[10]);
		Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+freeMemory+"K --vm-keep"); 
		Thread.sleep(10000);

		fazMedicao();

		Runtime.getRuntime().exec("sudo killall stress").waitFor();
		
		FileWriter DataBat = new FileWriter("/home/tiaraju/informacoesBateria.txt");
		BufferedWriter DataBatery =new BufferedWriter(DataBat);
		for(int i=0;i<infobateria.size();i++){
			DataBatery.append(infobateria.get(i)+"\n");
		}
		DataBatery.close();
		DataBat.close();

	}

	private static void fazMedicao()throws Exception{
		FileWriter out = new FileWriter("/home/tiaraju/medias.txt");
		FileWriter out2 = new FileWriter("/home/tiaraju/ValoresMemIndividuais.txt");
		BufferedWriter wrt = new BufferedWriter(out);BufferedWriter wrt2 = new BufferedWriter(out2);
		for(int k=0;k<10;k++){
			double media = 0;
			double potencia = pegaDados();
			for(int i=0;i<20;i++){
				wrt2.append(potencia+"\n");
				media+=potencia;
				Thread.sleep(4000);
				potencia = pegaDados();
			}
			wrt2.append("\n\n");
			mediasMemoria.add(media/20);
			wrt.append(media/20+"\n");

		}
		wrt.close();
		wrt2.close();
		out.close();
		out2.close();

	}

	public static void stressCPU() throws InterruptedException, IOException{
		CPUWorkloadGenerator work = new CPUWorkloadGenerator();
		work.stressMaximumCPU();
		Thread.sleep(10000);
		double p1 = pegaDados();
		Thread.sleep(5000);
		double p2 = pegaDados();
		while(Math.abs(p2-p1)>0.2){
			p1=p2;
			Thread.sleep(5000);
			p2=pegaDados();
		}
	}

	private static void fazMedicao(String arquivo)throws Exception{

		double potencia = pegaDados();
		FileWriter out = new FileWriter(arquivo);
		BufferedWriter writer = new BufferedWriter(out);
		double media = 0;
		for(int i=0;i<20;i++){
			media+=potencia;
			writer.write(potencia+"\n");
			Thread.sleep(4000);
			potencia = pegaDados();
		}
		mediasMemoria.add(media/20);

		writer.write("\n\n MEDIA: "+(media/20));
		writer.close();
		out.close();
		if(mediasMemoria.size()>1){
			if(isRecursive()){
				fazMedicao();		
			}
		}

	}


	private static double pegaDados() throws IOException {
	
		double[] dados = new double[2];
		Process p = Runtime.getRuntime().exec("cat /proc/acpi/battery/BAT0/state");
		InputStream input = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		List<String> state = new ArrayList<String>();
		String linha = reader.readLine();
		while (linha != null) {
			state.add(linha);
			linha = reader.readLine();
		}
		dados[0] = Double.parseDouble(state.get(3).split(" ")[13]);
		dados[1] = Double.parseDouble(state.get(5).split(" ")[10]);
		infobateria.add(dados[0]+" -- "+dados[1]);
		return dados[0]*dados[1]*0.000001;

	}

	private static boolean isRecursive() {
		int indice = mediasMemoria.size() - 1;
		System.out.println(indice);
		double last = mediasMemoria.get(indice);
		for (int i = 0; i < indice; i++) {
			if (Math.abs(last - mediasMemoria.get(i)) < 0.0000000000000000001){
				return false;
			}
		}
		return true;
	}



}
