import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class YokogawaClient implements MeterGetter {
	
	static String command = "fd0,010,010";
	
	private String servIP;
	private int servPort;

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintStream ps = null;
	private boolean connected = false;
	
	DateFormat timedf = null;
	DateFormat datedf = null;
	
	YokogawaData lastData = null;
	
	public YokogawaClient(String ipAndPort) throws UnknownHostException, IOException {
		String[] address = ipAndPort.split(":");
		servIP = address[0];
		servPort = Integer.parseInt(address[1]);
		
		lastData = new YokogawaData();
		timedf = lastData.getTimeFormat();
		datedf = lastData.getDateFormat();
		connect();
	}
	
	public void connect() throws UnknownHostException, IOException{
		socket = new Socket(servIP, servPort);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ps = new PrintStream(socket.getOutputStream());
		connected = true;
	}
	
	public void terminate() throws IOException {
		socket.close();
		connected = false;
	}
	
	private void sendCommand() {
		if (connected) ps.println(command);
	}
	
	private void refresh() throws IOException {
		if (connected) {
			sendCommand();
			
			Date time, date;
			double wattage;
			StringBuffer buffer = new StringBuffer("");
			char c;
			
			while (( c = (char) in.read()) != '-') {
				//Testing for the '-' at the end of the value sent is a workaround
				//Testing for read() != -1 doesn't work here.
				
				buffer = buffer.append(c);
			}
			
			String words[] = buffer.toString().split("\\s+");
			
			List<String> wordList = Arrays.asList(words);
			
			try {
				date = datedf.parse(words[wordList.indexOf("DATE") + 1]);
				time = timedf.parse(words[wordList.indexOf("TIME") + 1]);
				wattage = Integer.parseInt(words[wordList.indexOf("V") + 1].substring(1,6));
				
				YokogawaData newData = new YokogawaData(time, date, wattage);
				lastData = newData;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out.println("No negative wattage values, please!");
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("No negative wattage values, please!");
			}
		}
	}
	
	@Override
	public double getWattage() throws NumberFormatException, IOException {
		refresh();
		return lastData.getWattage();
	}

	@Override
	public double getMinInterval() {
		// TODO Find this value.
		return 0;
	}

	@Override
	public double getAccuracy() {
		// TODO Find this value.
		return 0;
	}
	
	
}
