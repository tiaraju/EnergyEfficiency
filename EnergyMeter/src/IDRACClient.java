import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;


public class IDRACClient implements MeterGetter {
	
	private boolean isAuthenticated;
	private Connection idracConnection;
	private final String command = "racadm getconfig -g cfgServerPower -o cfgServerActualPowerConsumption";
	
	public IDRACClient(String hostname, String username, String password) throws IOException {
		isAuthenticated = false;
		connect(hostname, username, password);
	}
	
	public double getWattage() throws IOException {
		Session session = idracConnection.openSession();
		session.execCommand(command);
		InputStream stdout = new StreamGobbler(session.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		
		String line[] = br.readLine().split("\\s+");
		
		int AC_W = Integer.parseInt(line[0]);
		int Btu_Hr = Integer.parseInt(line[4]);
		//Output example: "160 AC W | 546 Btu/hr"
		
		br.close();
		stdout.close();
		session.close();
		
		return AC_W;
		// return Btu_Hr;
	}
	
	public double getMinInterval() {
		return -1; //TODO: Find this value (or a way to calculate it)
	}
	
	public double getAccuracy() {
		return -1; //TODO: Find this value
	}
	
	private void connect(String hostname, String username, String password) throws IOException {
		idracConnection = new Connection(hostname);
		idracConnection.connect();
		
		isAuthenticated = idracConnection.authenticateWithPassword(username, password);
		if (!isAuthenticated) throw new IOException("Authentication failed.");
	}
	
}
