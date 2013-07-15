package disk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MonitoraHD {

	static FileWriter out =null;
	static FileReader in = null;
        static List<String> medicoes;
        static List<Double> mediasProvisorias;
        
	public static void main(String[] args) throws Exception{
		Runtime.getRuntime().exec("xset +dpms");
		Runtime.getRuntime().exec("xset dpms 0 0 1");
                mediasProvisorias = new ArrayList<Double>();
                analisaHD(8000);
                coletaDados(medicoes, new FileWriter(new File("/home/iago/testes/HD/HD_Ligado_Ocioso.txt")));
                //Runtime.getRuntime().exec("sudo hdparm -S 1 /dev/sda");
                mediasProvisorias = new ArrayList<Double>();
                Runtime.getRuntime().exec("sudo hdparm -y /dve/sda"); 
                analisaHDDesligado(8000);
                coletaDados(medicoes, new FileWriter(new File("/home/iago/testes/HD/HD_Desligado.txt")));
                mediasProvisorias = new ArrayList<Double>();
                Runtime.getRuntime().exec("stress --hdd 1 --hdd-bytes 1");
                analisaHD(8000);
                Runtime.getRuntime().exec("sudo killall stress").waitFor();
                coletaDados(medicoes, new FileWriter(new File("/home/iago/testes/HD/HD_Ligado_Stress.txt")));
                Runtime.getRuntime().exec("sudo hdparm -B255 /dve/sda");
                Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
	}

	private static void coletaDados(List<String> in2, FileWriter out2) throws Exception{
            BufferedWriter writer = new BufferedWriter(out2);
            for (String str : in2) {
                writer.write(str);
                writer.write("\n");
            }
            writer.close();
	}

    private static void analisaHD(int INTERVALO_DE_TEMPO) throws Exception{
        List<Double> potencias = new ArrayList<Double>(); 
        medicoes = new ArrayList<String>();
        long timeSleep;
        double potencia, media;
        final int TOTAL_DE_MEDICOES = 20;
        Thread.sleep(INTERVALO_DE_TEMPO);
        long firstTime = System.currentTimeMillis();
        for(int i=0; i<20; i++){
            potencia = pegaPotencia();
            medicoes.add(String.format("%.2f", potencia) + " W - " + String.format("%d", (System.currentTimeMillis() - firstTime)) + " milisegundos");
            potencias.add(potencia);
            timeSleep = System.currentTimeMillis();
	    if(((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0) && (i!=TOTAL_DE_MEDICOES-1)){
		Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
	    }
        }
        media = calculaMedia(potencias);
        mediasProvisorias.add(media);
        if(mediasProvisorias.size()==1){
            analisaHD(INTERVALO_DE_TEMPO);
        }
        else{
            if(possibilidadeDeRecursao()){
                analisaHD(INTERVALO_DE_TEMPO);
            }
            else{
                medicoes.add(String.format("\nMedia - " + "%.2f", media) + " W");
            }
        }
    }

    private static void analisaHDDesligado(int INTERVALO_DE_TEMPO) throws Exception{
        List<Double> potencias = new ArrayList<Double>(); 
        medicoes = new ArrayList<String>();
        long timeSleep;
        double potencia, media;
        final int TOTAL_DE_MEDICOES = 20;
        //Runtime.getRuntime().exec("sudo hdparm -y /dve/sda");
        Thread.sleep(INTERVALO_DE_TEMPO);
        long firstTime = System.currentTimeMillis();
        for(int i=0; i<20; i++){
            potencia = pegaPotencia();
            medicoes.add(String.format("%.2f", potencia) + " W - " + String.format("%d", (System.currentTimeMillis() - firstTime)) + " milisegundos");
            potencias.add(potencia);
            timeSleep = System.currentTimeMillis();
            //Runtime.getRuntime().exec("sudo hdparm -y /dve/sda");
	    if(((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0) && (i!=TOTAL_DE_MEDICOES-1)){
		Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
	    }
        }
        media = calculaMedia(potencias);
        mediasProvisorias.add(media);
        if(mediasProvisorias.size()==1){
            analisaHD(INTERVALO_DE_TEMPO);
        }
        else{
            if(possibilidadeDeRecursao()){
                analisaHD(INTERVALO_DE_TEMPO);
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

    private static double pegaPotencia() throws Exception{
        Process p = Runtime.getRuntime().exec("cat /proc/acpi/battery/BAT1/state");
        InputStream input = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        List<String> state = new ArrayList<String>();
        String linha = reader.readLine();
        while (linha != null) {
            state.add(linha);
            linha = reader.readLine();
        }
        return Double.parseDouble(state.get(3).split(" ")[13]) * Double.parseDouble(state.get(5).split(" ")[10])*0.000001;
    }

    /*private static void HDLigadoComStress() throws Exception{
        List<Double> potencias = new ArrayList<Double>();
        medicoes = new ArrayList<String>();
        long timeSleep;
        double potencia, media;
        final int INTERVALO_DE_TEMPO = 6000;
        final int TOTAL_DE_MEDICOES = 20;
        //Runtime.getRuntime().exec("sudo hdparm -S 2 /dev/sda");
        Runtime.getRuntime().exec("stress --io 1 --hdd 1 --hdd-bytes 1");
        Thread.sleep(INTERVALO_DE_TEMPO);
        long firstTime = System.currentTimeMillis();
        for(int i=0; i<20; i++){
            potencia = pegaPotencia();
            medicoes.add(String.format("%.2f", potencia) + " W - " + String.format("%d", (System.currentTimeMillis() - firstTime)) + " milisegundos");
            potencias.add(potencia);
            timeSleep = System.currentTimeMillis();
	    if(((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0) && (i!=TOTAL_DE_MEDICOES-1)){
		Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
	    }
        }
        Runtime.getRuntime().exec("sudo killall stress").waitFor();
        media = calculaMedia(potencias);
        mediasProvisorias.add(media);
        if(mediasProvisorias.size()==1){
            HDLigadoOcioso();
        }
        else{
            if(possibilidadeDeRecursao()){
                HDLigadoOcioso();
            }
            else{
                medicoes.add(String.format("\nMedia - " + "%.2f", media) + " W");
            }
        }
    }

    private static void HDDesligado() throws Exception{
        List<Double> potencias = new ArrayList<Double>();
        medicoes = new ArrayList<String>();
        long timeSleep;
        double potencia, media;
        final int INTERVALO_DE_TEMPO = 8000;
        final int TOTAL_DE_MEDICOES = 20;
        Runtime.getRuntime().exec("sudo hdparm -S 1 /dev/sda");
        Thread.sleep(INTERVALO_DE_TEMPO);
        long firstTime = System.currentTimeMillis();
        for(int i=0; i<20; i++){
            potencia = pegaPotencia();
            medicoes.add(String.format("%.2f", potencia) + " W - " + String.format("%d", (System.currentTimeMillis() - firstTime)) + " milisegundos");
            potencias.add(potencia);
            timeSleep = System.currentTimeMillis();
            //Runtime.getRuntime().exec("sudo hdparm -Y /dev/sda");
	    if(((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis()>0) && (i!=TOTAL_DE_MEDICOES-1)){
		Thread.sleep((timeSleep+INTERVALO_DE_TEMPO )-System.currentTimeMillis());
	    }
        }
        media = calculaMedia(potencias);
        mediasProvisorias.add(media);
        if(mediasProvisorias.size()==1){
            HDLigadoOcioso();
        }
        else{
            if(possibilidadeDeRecursao()){
                HDLigadoOcioso();
            }
            else{
                medicoes.add(String.format("\nMedia - " + "%.2f", media) + " W");
            }
        }
    }*/

    private static int getUnixPID(Process process) throws Exception {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            Class cl = process.getClass();
            Field field = cl.getDeclaredField("pid");
            field.setAccessible(true);
            Object pidObject = field.get(process);
            return (Integer) pidObject;
        } else {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }
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
