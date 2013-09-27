package wattage;

import java.io.IOException;

public class EnergyMeter {
	
	private static final int BATTERY = 0;
	private static final int IDRAC = 1;
	private static final int YOKOGAWA = 2;
	
	private MeterGetter meterGetter;
	private BatteryStatsClient battery;
	private IDRACClient idrac;
	private YokogawaClient yokogawa;
	
	public EnergyMeter(int type, String hostname, String username, String password) throws IOException {
		switch (type){
		case BATTERY:
			battery = new BatteryStatsClient();
			this.meterGetter = battery;
			break;
		case IDRAC:
			idrac = new IDRACClient(hostname, username, password);
			this.meterGetter = idrac;
			break;
		case YOKOGAWA:
			yokogawa = new YokogawaClient(hostname);
			this.meterGetter = yokogawa;
			break;
		}
	}
	
	
	public double getWattage() throws NumberFormatException, IOException {
		return meterGetter.getWattage();
	}
	
	public double getMinInterval() {
		return meterGetter.getMinInterval();
	}
	
	public static void main(String[] args) {
		try {
			/*EnergyMeter meter_battery = new EnergyMeter(BATTERY, null, null, null);
			System.out.println(meter_battery.getWattage());*/
			
			/*EnergyMeter meter_idrac = new EnergyMeter(IDRAC, "150.165.15.136", "root", "dr4cLSD");
			while (true) {
				System.out.println(meter_idrac.getWattage());
			}*/
					
			EnergyMeter meter_yokogawa = new EnergyMeter(YOKOGAWA, "150.165.85.230:34318" , null, null);
			System.out.println(meter_yokogawa.getWattage());
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
