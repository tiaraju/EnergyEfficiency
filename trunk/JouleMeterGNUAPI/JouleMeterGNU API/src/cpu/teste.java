package cpu;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;




public class teste {
	
	
	public static void main(String[] args) throws IOException {
		FileWriter w = new FileWriter("/home/tiaraju/nada.txt");
		BufferedWriter writer = new BufferedWriter(w);
		while(true){
			writer.append("aaaaaa");
			
		}
		

	}








}
