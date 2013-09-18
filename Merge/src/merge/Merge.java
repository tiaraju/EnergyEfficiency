package merge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
/**
 * responsable for put together the power and the details of experiment(freq and cores, for example)
 * @author tiaraju
 *
 */
public class Merge {

	double[][] matrix;


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
		FileReader f = new FileReader("/home/tiaraju/setembro2013/freqstimes.txt");
		BufferedReader freq = new BufferedReader(f);
		FileReader p = new FileReader("/home/tiaraju/setembro2013/PowerDataChangeFreq.txt");
		BufferedReader power = new BufferedReader(p);
		FileWriter merge = new FileWriter("/home/tiaraju/setembro2013/merge.txt");

		List<String> freqTime = new ArrayList<String>();
		List<String> powerTime = new ArrayList<String>();
		List<Double> medias = new ArrayList<Double>();
		matrix = new double[24][13];

		String freqLine = freq.readLine();
		String powerLine = power.readLine();

		while(powerLine != null && !powerLine.equals(" ")){
			powerTime.add(powerLine);
			powerLine=power.readLine();
		}

		while(freqLine != null && !freqLine.equals(" ")){
			freqTime.add(freqLine);
			freqLine= freq.readLine();
		}
		int k=0;
		for(int i=0;i<freqTime.size();i++){
			List<Double>partialValues = new ArrayList<Double>();
			int hour =Integer.parseInt(freqTime.get(i).split("-")[2].split(":")[0]);
			int minute=Integer.parseInt(freqTime.get(i).split("-")[2].split(":")[1]);
			int second = Integer.parseInt(freqTime.get(i).split("-")[2].split(":")[2]);

			Calendar time= Calendar.getInstance();
			time.set(0, 0, 0, hour, minute, second);

			Calendar time2 = Calendar.getInstance();
			time2.set(0, 0, 0, Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[0]),
					Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[1]), 
					Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[2])-5);

			while(time.compareTo(time2)>0){
				partialValues.add(Double.parseDouble(powerTime.get(k).split(" - ")[1]));
				k++;
				time2.set(0, 0, 0, Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[0]),
						Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[1]), 
						Integer.parseInt(powerTime.get(k).split(" - ")[0].split(":")[2]));
			}

			double finalValue = 0;

			if(partialValues.size()>0){
				finalValue=median(partialValues);
			}

			medias.add(finalValue);
			merge.append(freqTime.get(i)+" - "+finalValue+"\n");
		}

		merge.close();
		power.close();
		freq.close();
		p.close();
		f.close();



		//generate matrix
		generateMatrix(medias);

	}





	private void generateMatrix(List<Double> medias) throws IOException {
		int k;
		FileWriter w= new FileWriter("/home/tiaraju/setembro2013/cpu.txt");
		k=0;
		for(int i=0;i<24;i++){
			for(int j=0;j<13;j++){
				this.matrix[i][j]=medias.get(k);
				//System.out.println(medias.get(k));
				k++;
			}
		}
		for(int i=0;i<this.matrix.length;i++){
			for(int l=0;l<this.matrix[i].length;l++){
				w.append(String.format("%.2f",this.matrix[i][l])+" ");
			}
			w.append("\n");

		}
		w.close();
	}


	/**
	 * Consider the most repetead values with an error of +-5.
	 * @param list
	 * @return
	 */
	private double meanOfMostRepeatedValue(List<Double> list){
		return mean(MostRepeatedValues(list));
	}
	
	private double medianOfMostRepeatedValue(List<Double> list){
		return median(MostRepeatedValues(list));
	}
	
	private List<Double> MostRepeatedValues(List<Double> list){
		int maiorQuantidadeDeVezes=0;
		List<Double> temp = new ArrayList<Double>();
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			int num = 0;

			for(int k=0;k<list.size();k++){
				Double value = list.get(k);
				Double maxError = list.get(i)+5;
				Double minError = list.get(i)-5;
				if(value.equals(list.get(i)) || (value.compareTo(maxError)<0 && value.compareTo(minError)>0)){
					num++;
					temp.add(value);
				}
			}

			if(num>maiorQuantidadeDeVezes){
				result = new ArrayList<Double>();
				result.addAll(temp);
				maiorQuantidadeDeVezes=num;
			}
			temp.clear();			
		}
		
		return result;
	}

	private double mean(List<Double> list){
		double value =0.0;
		for (Double d : list) {
			value+=d;
		}
		return value/list.size();
	}
	
	private double median(List<Double> list){
		int middleIndex =0;
		Collections.sort(list);
		middleIndex=list.size()/2;
		return list.get(middleIndex);
	}

	public static void main(String[] args) throws Exception {

		Merge m = new Merge();
		m.cpuMerge();

	}

}
