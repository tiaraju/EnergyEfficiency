package merge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MemoryMerge {
	//merges the data of memory,according to each percentage of load of memory (0,25,50,100)


	public void mergeMemory()throws Exception{

		FileReader t = new FileReader("/home/tiaraju/memory.txt");
		BufferedReader tm = new BufferedReader(t);
		FileReader p = new FileReader("/home/tiaraju/memoryEnergyPower.txt");
		BufferedReader pw = new BufferedReader(p);

		FileWriter write = new FileWriter("/home/tiaraju/resultados/memoryResult.txt");

		List<String> time = new ArrayList<String>();
		List<String> power = new ArrayList<String>();
		List<String> result = new ArrayList<String>();


		String line = tm.readLine();
		while(line!=null){
			time.add(line);
			line = tm.readLine();
		}
		line=pw.readLine();
		while(line!=null){
			power.add(line);
			line=pw.readLine();
		}
		int k=0;

		for(int i=0;i<time.size();i++){
			String memoryTime = time.get(i).split("-")[2];
			String realTime= power.get(k).split(" ")[0];
			while(realTime.compareTo(time.get(i).split("-")[0].split(" ")[1])<0){
				k++;
				realTime= power.get(k).split(" ")[0];
			}
			List<Double> respectiveValues = new ArrayList<Double>();
			//for(int j=0;j<5;j++){//analisa o consumo de acordo com o tempo em que a memoria estava em x% de utilização
			while(realTime.compareTo(memoryTime)<=0){
				if(k<power.size()){
					respectiveValues.add(Double.parseDouble(power.get(k).split(" ")[2]));
					k++;
					realTime= power.get(k).split(" ")[0];
					System.out.println(power.get(k).split(" ")[2]);
				}
			}
			System.out.println("parou");
			double matchingValue=higherValue(respectiveValues);
			//System.out.println(temp+" - "+res);
			result.add(time.get(i)+" power: "+matchingValue);

			if(k>=power.size()) break;


		}

		for(String s:result){
			write.append(s+"\n");
		}
		write.close();
		pw.close();
		tm.close();
		p.close();
		t.close();
	}
//alternativas de obtenção do valor(moda,maior,media)
	private double mostRepeatedValue(List<Double> array){
		int nVezes = 0;  
		double moda = 0;  
		int comparaV = 0;  
		for(int p = 0; p < array.size(); p++){  
			nVezes = 0;  
			for(int k = p+1; k < array.size(); k++){  
				if(array.get(k).equals(array.get(p))){  
					++nVezes;                 
				}  
			}  
			if (nVezes > comparaV ){  
				moda = array.get(p);  
				comparaV = nVezes;  
			}      
		}         
		return moda;      

	}


	private double higherValue(List<Double> array){
		double bigger = 0;

		for(Double number:array){
			if(number>bigger){
				bigger=number;
			}
		}
		
		return bigger;
	}
	

	
	private double avarageValue(List<Double> array){
		double media =0;
		
		for(Double d:array){
			media+=d;
		}
		
		return media/array.size();
	}

	public static void main(String[] args)throws Exception {
		MemoryMerge m = new MemoryMerge();
		m.mergeMemory();

	}
}
