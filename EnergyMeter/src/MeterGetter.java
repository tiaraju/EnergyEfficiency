import java.io.IOException;


public interface MeterGetter {
	double getWattage() throws NumberFormatException, IOException;

	double getMinInterval();
	
	double getAccuracy();
}