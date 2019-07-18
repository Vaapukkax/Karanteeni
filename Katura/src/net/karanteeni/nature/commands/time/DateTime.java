package net.karanteeni.nature.commands.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateTime {
	private final static DateTime MORNING = new DateTime("morning", 23000);
	private final static DateTime DAY = new DateTime("day", 1000);
	private final static DateTime MIDDAY = new DateTime("midday", 6000);
	private final static DateTime EVENING = new DateTime("evening", 11000);
	private final static DateTime NIGHT = new DateTime("night", 13000);
	private final static DateTime MIDNIGHT = new DateTime("midnight", 18000);
	private final static List<DateTime> times = Arrays.asList(MORNING, DAY, MIDDAY, EVENING, NIGHT, MIDNIGHT);
	
	private String name;
	private long tickCount;
	
	
	private DateTime(String name, long tickCount) {
		this.name = name;
		this.tickCount = tickCount % 24000;
	}
	
	
	/**
	 * Returns a copy of all static values
	 * @return
	 */
	public List<DateTime> getValues() {
		return new ArrayList<DateTime>(DateTime.times);
	}
	
	
	/**
	 * Returns the time keys
	 * @return
	 */
	public static List<String> getKeys() {
		List<String> items = new ArrayList<String>();
		for(DateTime t : times)
			items.add(t.name);
		return items;
	}
	
	
	/**
	 * Returns the tickcount of this date time
	 * @return tick count
	 */
	public long getTicks() {
		return this.tickCount;
	}
	
	
	/**
	 * Returns the given string as datetime
	 * @param time time to convert to date time
	 * @return DateTime object or null if cannot parse
	 */
	public static DateTime parseTime(String time) {
		// iterate all static values and check if we're trying to access an existing value
		for(DateTime dtime : times)
			if(dtime.name.equalsIgnoreCase(time))
				return dtime;
		
		try {
			long res = Long.parseLong(time);
			if(res < 0)
				return null;
			return new DateTime("custom", res);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}