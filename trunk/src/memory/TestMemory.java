package memory;

import java.io.BufferedReader;
import java.io.BufferedWriter;


import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;





public class TestMemory {


	public static void main(String[] args)throws Exception {
		
		fazMedicao("/home/tiaraju/LowMemoryData2.txt");
		
		Process p = Runtime.getRuntime().exec("cat /proc/meminfo");
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha = reader.readLine();
		for(int i=0;i<=1;i++){
			memoryInfo.add(linha);
			linha = reader.readLine();
		}
		String freeMemory=(memoryInfo.get(1).split(" ")[9]);
		Runtime.getRuntime().exec("stress --vm 1 --vm-bytes "+freeMemory+"K --vm-keep"); 
		Thread.sleep(10000);
		fazMedicao("/home/tiaraju/HighMemoryData2.txt");
			
		Runtime.getRuntime().exec("sudo killall stress").waitFor();

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
		writer.write("\n\n MEDIA: "+(media/20));
		writer.close();
		out.close();
		
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
		System.out.println(dados[0]+" - "+ dados[1]);
		return dados[0]*dados[1];

	}
	


}
