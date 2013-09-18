package disk;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wattage.EnergyMeter;


/**
 * 
 * @author tiaraju
 *
 */
public class DiskEnergyMeter {
	List<Double> mediasProvisoriasOcioso= new ArrayList<Double>();
	List<Double> mediasProvisoriasUse= new ArrayList<Double>();
	private EnergyMeter getWattage;
	
	public DiskEnergyMeter(int type,String hostName,String userName,String password){
	/*	try {
			this.getWattage = new EnergyMeter(type, hostName, userName, password);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}*/
	}

	/**
	 * 
	 * @return
	 */
	public double getPowerHdInUse(){
		double result =0;
		try {
			//Runtime.getRuntime().exec("xset +dpms");
			//Runtime.getRuntime().exec("xset dpms 0 0 1");
			Runtime.getRuntime().exec("stress --hdd 1");
			Thread.sleep(10000);
			result=fazMedicao(mediasProvisoriasUse);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		try {
			//Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
			Runtime.getRuntime().exec("sudo killall stress").waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	

	/**
	 * 
	 */
	public double getPowerHdStandBy(){
		double result=0;
		try {
			//Runtime.getRuntime().exec("xset +dpms");
			//Runtime.getRuntime().exec("xset dpms 0 0 1");
			Thread.sleep(10000);
			result = fazMedicao(mediasProvisoriasOcioso);
			//Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	private double fazMedicao(List<Double> list)throws Exception{
		List<Double> potencias = new ArrayList<Double>();
		long timeSleep;
		double media=0;
		final int TOTAL_DE_MEDICOES = 10;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 10; i++) {
			try {
				potencias.add(pegaPotencia());
			} catch (Exception e) {
				e.printStackTrace();
			}
			timeSleep = System.currentTimeMillis();
			if (((timeSleep + 3000) - System.currentTimeMillis() > 0) && (i != TOTAL_DE_MEDICOES - 1)) {
				try {
					Thread.sleep((timeSleep + 3000) - System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(2000);
		}
		media = calculaMedia(potencias);
		list.add(media);
		if(list.size()==1){
			fazMedicao(list);
		}
		else{
			if(possibilidadeDeRecursao(list)){
				fazMedicao(list);
			}		
		}
		return list.get(list.size()-1);
		
	}


	private double pegaPotencia() throws Exception {
		return 0;
	}

	private boolean possibilidadeDeRecursao(List<Double> list) {
		int indice = list.size() - 1;
		double last = list.get(indice);
		for (int i = 0; i < indice; i++) {
			if (Math.abs(last - list.get(i)) < 0.6) {
				System.out.println(last - list.get(i));
				return false;
			}
		}
		return true;
	}

	private double calculaMedia(List<Double> medicoes) {
		double soma = 0;
		Iterator<Double> it = medicoes.iterator();
		double next;
		while (it.hasNext()) {
			next = it.next();
			soma += next;
		}
		return soma / medicoes.size();
	}

}
