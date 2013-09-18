package disk;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tiaraju
 */
public class hdDesligado {

	static FileWriter out = null;
	static FileReader in = null;
	static List<String> medicoes;
	static List<Process> processos;
	static List<Double> mediasProvisorias;

	public static void main(String[] args) throws Exception{
		//Desligar monitor
		Runtime.getRuntime().exec("xset +dpms");
		Runtime.getRuntime().exec("xset dpms 0 0 1");
		mediasProvisorias = new ArrayList<Double>();
		analisaHDDesligado(5000);
		coletaDados(medicoes, new FileWriter(new File("/home/tiaraju/testes/HD/HD_Desligado.txt")));
		//Ligar monitor
		Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
	}

	private static void analisaHDDesligado(int INTERVALO_DE_TEMPO) throws Exception {
		processos = new ArrayList<Process>();
		List<Double> potencias = new ArrayList<Double>();
		medicoes = new ArrayList<String>();
		long timeSleep;
		double potencia, media;
		final int TOTAL_DE_MEDICOES = 15;
		//Utilizei o hdparm para deixar o hd em sleep mode. Nesse site tem a lista de comandos: http://linux.die.net/man/8/hdparm
		Runtime.getRuntime().exec("sudo hdparm -y /dev/sda");
		Thread.sleep(INTERVALO_DE_TEMPO);
		long firstTime = System.currentTimeMillis();
		for (int i = 0; i < 15; i++) {
			potencia = pegaPotencia();
			medicoes.add(String.format("%.2f", potencia) + " W - " + String.format("%d", (System.currentTimeMillis() - firstTime)) + " milisegundos");
			potencias.add(potencia);
			timeSleep = System.currentTimeMillis();
			if (((timeSleep + INTERVALO_DE_TEMPO) - System.currentTimeMillis() > 0) && (i != TOTAL_DE_MEDICOES - 1)) {
				Thread.sleep((timeSleep + INTERVALO_DE_TEMPO) - System.currentTimeMillis());
			}
			processos.add(Runtime.getRuntime().exec("cat /proc/acpi/battery/BAT0/state"));
		}
		media = calculaMedia(potencias);
		mediasProvisorias.add(media);
		if(mediasProvisorias.size()==1){
			analisaHDDesligado(INTERVALO_DE_TEMPO);
		}
		else{
			if(possibilidadeDeRecursao()){
				analisaHDDesligado(INTERVALO_DE_TEMPO);
			}
			else{
				medicoes.add(String.format("\nMedia - " + "%.2f", media) + " W");
			}
		}
	}

	private static double calculaMedia(List<Double> medicoes) {
		double soma = 0;
		Iterator<Double> it = medicoes.iterator();
		double next;
		while (it.hasNext()) {
			next = it.next();
			soma += next;
		}
		return soma / medicoes.size();
	}

	private static double pegaPotencia() throws Exception {
		Process p = Runtime.getRuntime().exec("cat /proc/acpi/battery/BAT0/state");
		InputStream input = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		List<String> state = new ArrayList<String>();
		String linha = reader.readLine();
		while (linha != null) {
			state.add(linha);
			linha = reader.readLine();
		}
		return Double.parseDouble(state.get(3).split(" ")[13]) * Double.parseDouble(state.get(5).split(" ")[10]) * 0.000001;
	}

	private static void coletaDados(List<String> in2, FileWriter out2) throws Exception {
		BufferedWriter writer = new BufferedWriter(out2);
		for (String str : in2) {
			writer.write(str);
			writer.write("\n");
		}
		writer.close();
	}

	private static boolean possibilidadeDeRecursao() {
		int indice = mediasProvisorias.size() - 1;
		double last = mediasProvisorias.get(indice);
		for (int i = 0; i < indice; i++) {
			if (Math.abs(last - mediasProvisorias.get(i)) < 0.2) {
				return false;
			}
		}
		return true;
	}
}

