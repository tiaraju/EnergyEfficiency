package experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import disk.DiskMonitor;


public class WriteHd {

	
	public static void main(String[] args) throws Exception{
		DiskMonitor diskMonitor;
		diskMonitor= new DiskMonitor();
		FileWriter diskStatus;
		try {
			diskStatus = new FileWriter("/home/tiaraju/diskStatus.txt");
			diskStatus.append("stress begining: "+ new Date().toString().subSequence(11, 19));
			Runtime.getRuntime().exec("stress -d 200");
			System.out.println("começou\n");
			for(int i=0;i<20;i++){
				diskStatus.append(new Date().toString().subSequence(11, 19)+" - wkb/s: "+diskMonitor.getActualWrittenPerSecond()+"\n");
				Thread.sleep(30000);
			}
			System.out.println("parou");
			Runtime.getRuntime().exec("killall stress");
			diskStatus.append("stress finished: "+ new Date().toString().subSequence(11, 19));
			diskStatus.close();
		} catch (IOException e) {
			System.out.println("quebrou =X");
		} catch (InterruptedException e) {
			System.out.println("quebrou =X");
		}
		
		
	
		


	}

}
