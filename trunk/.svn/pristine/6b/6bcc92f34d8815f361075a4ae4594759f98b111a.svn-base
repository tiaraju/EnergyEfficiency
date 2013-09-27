package video;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestVideo {

	public static void main(String[] args) throws IOException, InterruptedException {
		double media=0;
		FileWriter out = new FileWriter("/home/tiaraju/videoOn.txt");
		BufferedWriter writer = new BufferedWriter(out);
		for(int k=0;k<10;k++){
			Runtime.getRuntime().exec("sudo xbacklight -set "+10*(k+1));
			Thread.sleep(5000);
			for(int i=0;i<10;i++){
				media+=pegaDados();
				writer.append(pegaDados()+"\n");
				Thread.sleep(3000);
			}
			writer.append("\n\nmedia: "+media/10+"\n\n");
		}
		
		writer.close();
		out.close();
		Thread.sleep(2000);
		Runtime.getRuntime().exec("xset +dpms");
		Runtime.getRuntime().exec("xset dpms 0 0 1");
		media=0;
		FileWriter out2 = new FileWriter("/home/tiaraju/videoOff.txt");
		BufferedWriter writer2 = new BufferedWriter(out2);
		Thread.sleep(10000);	
		for(int i=0;i<40;i++){
			media+=pegaDados();
			writer2.append(pegaDados()+"\n");
			Thread.sleep(3000);
		}
		writer2.append("media: "+media/40);
		writer2.close();
		out2.close();
		Runtime.getRuntime().exec("xset -display :0.0 dpms force on");

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
		return dados[0]*dados[1]*0.000001;

	}

}
