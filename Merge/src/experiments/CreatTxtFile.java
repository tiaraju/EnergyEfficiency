package experiments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CreatTxtFile {

	FileReader in;
	BufferedReader reader;
	FileWriter out;
	BufferedWriter writer;

	public CreatTxtFile()throws Exception{
		in = new FileReader("/home/tiaraju/cpu2207.txt");
		reader= new BufferedReader(in);
		out= new FileWriter("/home/tiaraju/1GBtxtFile.txt");
		writer=new BufferedWriter(out);
	}
	public void createFile()throws Exception{
		File f = new File("/home/tiaraju/1GBtxtFile.txt");
		String line = reader.readLine();
		for(int i=0;i<50;i++){
			while(f.length()<1000000000){
				writer.append(line+"\n\n\n");
				line=reader.readLine();
				System.out.println(f.length()+" - "+line);
				if(line ==null){
					in = new FileReader("/home/tiaraju/cpu2207.txt");
					reader = new BufferedReader(in);
					line=reader.readLine();
				}
			}

		}
		reader.close();
		writer.close();
		out.close();
		in.close();

	}
	
	public static void main(String[] args)throws Exception {
		CreatTxtFile creator = new CreatTxtFile();
		creator.createFile();
	}

}
