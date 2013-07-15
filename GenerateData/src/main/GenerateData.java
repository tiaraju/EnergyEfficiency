package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenerateData {
	
	public void separaDados() throws Exception{
		FileReader r =  new FileReader("/home/tiaraju/cpuServer.txt");
		BufferedReader reader = new BufferedReader(r);
		String line = reader.readLine();
		List<String> time = new ArrayList<String>();
		List<String> power = new ArrayList<String>();
		while(line != null){
			time.add(line.split(" - ")[0]);
			power.add(line.split(" - ")[1]);
			line=reader.readLine();
		}
		FileWriter outPut= new FileWriter("/home/tiaraju/output.txt");
		for(int i=0; i< time.size();i++){
			outPut.append(time.get(i)+"\n");
		}
		
		for (int i = 0; i < power.size(); i++) {
			outPut.append(power.get(i)+"\n");
		}
		reader.close();
		r.close();
		outPut.close();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		FileReader r =  new FileReader("/home/tiaraju/top.txt");
		BufferedReader reader = new BufferedReader(r);
		String line = reader.readLine();
		System.out.println(line);
		String percentage = line.split(",")[0].split(":")[1];
		System.out.println(percentage.split(" ")[percentage.split(" ").length-2]);
	}

}
