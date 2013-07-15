package merge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Merge {

	static double[][] matrix;


	public void diskMerge() throws Exception{
		FileReader f = new FileReader("/home/tiaraju/dataTime.txt");
		BufferedReader data = new BufferedReader(f);
		FileReader p = new FileReader("/home/tiaraju/PowerDataServerTotal.txt");
		BufferedReader power = new BufferedReader(p);
		FileWriter merge = new FileWriter("/home/tiaraju/disk.txt");

		List<String> dataTime = new ArrayList<String>();
		List<String> powerTime = new ArrayList<String>();
		List<Double> medias = new ArrayList<Double>();

		String dataLine = data.readLine();
		String powerLine = power.readLine();

		while(powerLine != null){
			powerTime.add(powerLine);
			powerLine=power.readLine();
		}

		while(dataLine != null){ 
			dataTime.add(dataLine);
			dataLine= data.readLine();
		}

		int k=0;
		while(powerTime.get(k).split(" - ")[0].compareTo(dataTime.get(5).split(" ")[1])<0){
			System.out.println(powerTime.get(k).split(" - ")[0]);
			System.out.println(dataTime.get(5).split(" ")[0]);
			k++;
		}

		for(int i=0;i<dataTime.size();i++){
			double media =0;
			int count=0;
			if(dataTime.get(i).contains("harddiskstarts")){
				String time= dataTime.get(i).split(" ")[1];
				while(time.compareTo(powerTime.get(k).split(" - ")[0])>0){
					media+=Double.parseDouble(powerTime.get(k).split(" - ")[1]);
					count++;
					k++;
				}
				medias.add(media/count);
				merge.append("Mindisk: "+media/count+"\n");
			}else if(dataTime.get(i).contains("diskends")){
				String time= dataTime.get(i).split(" ")[1];
				System.out.println(time);
				while(time.compareTo(powerTime.get(k).split(" - ")[0])>0){
					media+=Double.parseDouble(powerTime.get(k).split(" - ")[1]);
					count++;
					k++;
				}
				medias.add(media/count);
				merge.append("Maxdisk: "+media/count+"\n"+ media +" "+ count);
				break;
			}
		}

		merge.close();
		power.close();
		data.close();
		p.close();
		f.close();
	}





public void memoryMerge() throws Exception{
	FileReader f = new FileReader("/home/tiaraju/dataTime.txt");
	BufferedReader data = new BufferedReader(f);
	FileReader p = new FileReader("/home/tiaraju/PowerDataServerTotal.txt");
	BufferedReader power = new BufferedReader(p);
	FileWriter merge = new FileWriter("/home/tiaraju/memory.txt");

	List<String> dataTime = new ArrayList<String>();
	List<String> powerTime = new ArrayList<String>();
	List<Double> medias = new ArrayList<Double>();

	String dataLine = data.readLine();
	String powerLine = power.readLine();

	while(powerLine != null){
		powerTime.add(powerLine);
		powerLine=power.readLine();
	}

	while(dataLine != null){ 
		dataTime.add(dataLine);
		dataLine= data.readLine();
	}

	int k=0;
	while(powerTime.get(k).split(" - ")[0].compareTo(dataTime.get(0).split(" ")[1])<0){
		k++;
	}

	for(int i=0;i<dataTime.size();i++){
		double media =0;
		int count=0;
		if(dataTime.get(i).contains("MAXmemory")){
			String time= dataTime.get(i).split(" ")[1];
			while(time.compareTo(powerTime.get(k).split(" - ")[0])>0){
				media+=Double.parseDouble(powerTime.get(k).split(" - ")[1]);
				count++;
				k++;
			}
			medias.add(media/count);
			merge.append("MinMemory: "+media/count+"\n");
		}else if(dataTime.get(i).contains("memoryends")){
			String time= dataTime.get(i).split(" ")[1];
			System.out.println(time);
			while(time.compareTo(powerTime.get(k).split(" - ")[0])>0){
				media+=Double.parseDouble(powerTime.get(k).split(" - ")[1]);
				count++;
				k++;
			}
			medias.add(media/count);
			merge.append("Maxmemory: "+media/count+"\n"+ media +" "+ count);
			break;
		}
	}

	merge.close();
	power.close();
	data.close();
	p.close();
	f.close();

}

public void cpuMerge() throws Exception{
	FileReader f = new FileReader("/home/tiaraju/freqTime.txt");
	BufferedReader freq = new BufferedReader(f);
	FileReader p = new FileReader("/home/tiaraju/PowerDataServerTotal.txt");
	BufferedReader power = new BufferedReader(p);
	FileWriter merge = new FileWriter("/home/tiaraju/merge.txt");

	List<String> freqTime = new ArrayList<String>();
	List<String> powerTime = new ArrayList<String>();
	List<Double> medias = new ArrayList<Double>();
	matrix = new double[24][13];

	String freqLine = freq.readLine();
	String powerLine = power.readLine();

	while(powerLine != null){
		powerTime.add(powerLine);
		powerLine=power.readLine();
	}

	while(freqLine != null){
		if(!freqLine.contains("CPU")){
			freqTime.add(freqLine);
		}
		freqLine= freq.readLine();
	}

	int k=0;
	for(int i=0;i<freqTime.size();i++){
		double media =0;
		int count=0;
		String time= freqTime.get(i).split("-")[1];
		while(time.compareTo(powerTime.get(k).split(" - ")[0])>0){
			media+=Double.parseDouble(powerTime.get(k).split(" - ")[1]);
			count++;
			k++;
		}
		medias.add(media/count);
		merge.append(freqTime.get(i)+" - "+media/count+"\n");
	}

	merge.close();
	power.close();
	freq.close();
	p.close();
	f.close();


	FileWriter w= new FileWriter("/home/tiaraju/cpu.txt");
	//generate matrix
	k=0;
	for(int i=0;i<24;i++){
		for(int j=0;j<13;j++){
			matrix[i][j]=medias.get(k);
			System.out.println(medias.get(k));
			k++;
		}
	}
	for(int i=0;i<matrix.length;i++){
		for(int l=0;l<matrix[i].length;l++){
			w.append(String.format("%.2f",matrix[i][l])+" ");
		}
		w.append("\n");

	}
	w.close();

}

public static void main(String[] args) throws Exception {

/*	Merge m = new Merge();
	m.diskMerge();
	m.cpuMerge();
	m.memoryMerge();
*/

}

}
