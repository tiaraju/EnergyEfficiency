package disk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tiaraju
 *
 */
public class DiskMonitor {
	/**
	 * 
	 * @return
	 */
	public double diskTotalCapacity(){
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("sudo fdisk -l");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha = null;
		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		for(int i=0;i<=1;i++){
			memoryInfo.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		double totalCapacity=Double.parseDouble((memoryInfo.get(1).split(" ")[2]));
		return totalCapacity;
	}
	/**
	 * 
	 * @return  The number of kb written in the hard disk per second
	 * @throws FileNotFoundException 
	 */
	public int getActualWrittenPerSecond() {
		String userName = System.getProperty("user.name");
		BufferedReader reader= null;
		FileReader diskReader=null;
		try {
			FileWriter writeSh= new FileWriter("/home/"+userName+"/disk.sh");
			writeSh.append("iostat -d -x | grep 'sda'| awk '{print $7;}' > /home/"+userName+"/diskWritten.txt");
			writeSh.close();
			Runtime.getRuntime().exec("sh /home/"+userName+"/disk.sh");
			diskReader = new FileReader("/home/"+userName+"/diskWritten.txt");
			reader = new BufferedReader(diskReader);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		int result=0;
		try {
			int indexOfSeparator;
			String line = reader.readLine();
			if(line.contains(",")){
				indexOfSeparator =line.indexOf(",");
			}else{
				indexOfSeparator= line.indexOf(".");
			}
			result = Integer.parseInt(line.substring(0,indexOfSeparator));
			reader.close();
			diskReader.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		
		return result;
	}


	/**
	 * 
	 * @return
	 */
	public double diskInUse(){
		return 0;
	}

}
