package wattage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YokogawaData {
	DateFormat timedf = null;
	DateFormat datedf = null;
	
	Date time = null;
	Date date = null;
	double wattage;
	
	public YokogawaData() {
		timedf = new SimpleDateFormat("hh:mm:ss");
		datedf = new SimpleDateFormat("yy/MM/dd");
	}
	
	public YokogawaData(Date time, Date date, double wattage) {
		this();
		this.time = time;
		this.date = date;
		this.wattage = wattage;
	}
	
	public DateFormat getTimeFormat(){
		return timedf;
	}
	
	public DateFormat getDateFormat(){
		return datedf;
	}
	
	public double getWattage() {
		return wattage/10000;
	}
	
	public Date getTime() {
		return time;
		
	}
	public Date getDate() {
		return date;
	}
}
