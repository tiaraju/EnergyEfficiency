package memory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MemoryMonitor {

	/**
	 * 
	 * @return
	 */
	public long getTotalMemory(){
		String fullMemory=null;
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha=null;
		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		for(int i=0;i<=1;i++){
			memoryInfo.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		for(int i=2;i<memoryInfo.get(0).split(" ").length-1;i++){
			if(!(memoryInfo.get(0).split(" ")[i].equals(" "))){
				fullMemory=(memoryInfo.get(0).split(" ")[i]);
			}
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.valueOf(fullMemory);
	}



	/**
	 * 
	 * @return
	 */
	public long getFreeMemory(){
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha=null;
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
		String[] temp =memoryInfo.get(1).split(":")[1].split(" ");
		String freeMemory=(temp[temp.length-2]);
		
		return Integer.valueOf(freeMemory);
	}

	/**
	 * 
	 * @return
	 */
	public long getUsedMemory(){
		return getTotalMemory()-getFreeMemory();
	}
	/**
	 * 
	 * @return
	 */
	public double getTotalSwappedMemory(){
		String swappedMemory=null;
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha=null;
		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		for(int i=0;i<=18;i++){
			memoryInfo.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		for(int i=2;i<memoryInfo.get(17).split(" ").length-1;i++){
			if(!(memoryInfo.get(17).split(" ")[i].equals(" "))){
				swappedMemory=(memoryInfo.get(17).split(" ")[i]);
			}
		}

		return Double.valueOf(swappedMemory);
	}
	/**
	 * 
	 * @return
	 */
	public double getBufferedMemory(){
		String bufferedMemory=null;
		Process p=null;
		try {
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		InputStream in = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<String> memoryInfo = new ArrayList<String>();
		String linha=null;
		try {
			linha = reader.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		for(int i=0;i<=2;i++){
			memoryInfo.add(linha);
			try {
				linha = reader.readLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		for(int i=2;i<memoryInfo.get(2).split(" ").length-1;i++){
			if(!(memoryInfo.get(2).split(" ")[i].equals(" "))){
				bufferedMemory=(memoryInfo.get(2).split(" ")[i]);
			}
		}

		return Double.valueOf(bufferedMemory);
	}

	
	public static void main(String[] args) throws Exception{
		MemoryMonitor m = new MemoryMonitor();
		double free = m .getUsedMemory();
		
		while(true){
			System.out.println(free);
			Thread.sleep(2000);
			free = m .getUsedMemory();
		}
	}
}
