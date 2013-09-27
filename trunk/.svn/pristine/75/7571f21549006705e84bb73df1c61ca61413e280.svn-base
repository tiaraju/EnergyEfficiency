package video;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wattage.EnergyMeter;

/**
 * 
 * @author tiaraju
 *
 */

public class VideoEnergyMeter {
	private EnergyMeter getWattage;


	public VideoEnergyMeter(int type,String hostName,String userName,String password){
		try {
			this.getWattage = new EnergyMeter(type, hostName, userName, password);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getPowerVideoInUse(){
		double media=0;
		List<Double> videoOn = new ArrayList<Double>();

		for(int i=0;i<30;i++){
			try {
				media=pegaDados();
				videoOn.add(media);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}


		}
		double result =0;
		for(int i=0;i<videoOn.size();i++){
			result+=videoOn.get(i);
		}
		result = result/videoOn.size();
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public double getPowerVideoOutOfUse(){

		try {
			Runtime.getRuntime().exec("xset +dpms");
			Runtime.getRuntime().exec("xset dpms 0 0 1");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		double media=0;
		List<Double> videoOn = new ArrayList<Double>();
		for(int i=0;i<50;i++){
			try {
				media=pegaDados();
				videoOn.add(media);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}


		double result =0;
		for(int i=0;i<videoOn.size();i++){
			result+=videoOn.get(i);
		}
		result = result/videoOn.size();
		try {
			Runtime.getRuntime().exec("xset -display :0.0 dpms force on");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	private double pegaDados() throws IOException {
		return getWattage.getWattage();

	}

}
