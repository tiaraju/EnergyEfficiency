package disk;

import java.io.IOException;
/**
 * 
 * @author tiaraju
 *
 */
public class DiskWorkLoadGenerator {
	/**
	 * 
	 */
	public void stressHD(){
		try {
			Runtime.getRuntime().exec("sudo stress --hdd 1 --hdd-bytes 1");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	/**
	 * 
	 * @param time
	 */
	public void stressHD(int time){
		try {
			Runtime.getRuntime().exec("sudo stress --hdd 1 --hdd-bytes 1 --timeout"+time);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	/**
	 * 
	 */
	public void resetStress(){
		try {
			Runtime.getRuntime().exec("sudo killall stress");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
