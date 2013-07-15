import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class BatteryStatsClient implements MeterGetter {
	
	private static final String events_file_path = "/sys/class/power_supply/BAT0/uevent";
	private File events_file = null;
	
	
	public BatteryStatsClient() {
		events_file = new File(events_file_path);
	}

	@Override
	public double getWattage() throws NumberFormatException, IOException {
		int voltage_now = -1, current_now = -1;
		double wattage;
		String s;
		
		FileReader fr = new FileReader(events_file);
		BufferedReader br = new BufferedReader(fr);
		
		while ((s = br.readLine()) != null) {
			String line[] = s.split("=");
			if (line[0].equals("POWER_SUPPLY_VOLTAGE_NOW"))
				voltage_now = Integer.parseInt(line[1])/1000;
			else if (line[0].equals("POWER_SUPPLY_CURRENT_NOW"))
				current_now = Integer.parseInt(line[1])/1000;
		}
		
		br.close();
		fr.close();
		
		wattage = (double) voltage_now*current_now/1000000;
		return wattage;
	}

	@Override
	public double getMinInterval() {
		// TODO Find this value.
		return -1;
	}

	@Override
	public double getAccuracy() {
		// TODO Find this value. Yokogawa can't help us here, as this won't work
		// when not on battery....
		return -1;
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BatteryStatsClient client = new BatteryStatsClient();
		System.out.println(client.getWattage());
	}


}