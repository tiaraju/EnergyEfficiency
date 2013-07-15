package merge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MergeExperiments {
//carrega valores de acordo com determinada frequencia e numero de cpus para fazer os graficos
	public static void mergeExp()throws Exception{
		FileReader t = new FileReader("/home/tiaraju/resultados/freqstimes.txt");
		BufferedReader tm = new BufferedReader(t);
		FileReader p = new FileReader("/home/tiaraju/CpuPowerDataServerTotal.txt");
		BufferedReader pw = new BufferedReader(p);

		FileWriter write = new FileWriter("/home/tiaraju/resultados/freqResult.txt");

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
		
		for(int i=0;i<time.size()-1;i++){
			int division=0;
			String freqTime = time.get(i+1).split(" ")[4];
			String realTime= power.get(k).split(" ")[0];
			double media = 0;
			//for(int j=0;j<5;j++){//enquanto o tempo nao corresponder a outra freq, vai somando
			while(realTime.compareTo(freqTime)<0){
				if(k<power.size()){
					media +=Double.parseDouble(power.get(k).split(" ")[2]);
					k++;
					division++;
					realTime= power.get(k).split(" ")[0];
					System.out.println(power.get(k).split(" ")[2]);
				}
			}
			System.out.println("parou");
			media=media/division;
			//System.out.println(temp+" - "+res);
			result.add(time.get(i)+" power: "+media);

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



	/*private static String concat(String s){
		int parcel = Integer.parseInt(s.split(":")[2]);
		int parcel2=Integer.parseInt(s.split(":")[1]);


		String result = null;
		parcel+=10;
		if(parcel>59){
			parcel=(parcel-10)-50;
			result="0"+parcel;
			//result=s.split(":")
		}else{
			result=""+parcel;
		}
		return s.split(":")[0]+":"+s.split(":")[1]+":"+result;
	}*/

	public static void main(String[] args)throws Exception {
		mergeExp();
	}

}
