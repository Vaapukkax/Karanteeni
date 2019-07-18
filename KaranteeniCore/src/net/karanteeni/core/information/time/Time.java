package net.karanteeni.core.information.time;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {
	//private static final Regex
	private static final Pattern DIGITS = Pattern.compile("^\\d+");
	
	// times as milliseconds
	private static final long MS_SECOND 	= 	1000;
	private static final long MS_MINUTE 	= 	MS_SECOND 	* 60;
	private static final long MS_HOUR 		= 	MS_MINUTE 	* 60;
	private static final long MS_DAY 		= 	MS_HOUR 	* 24;
	private static final long MS_WEEK 		= 	MS_DAY		* 7;
	private static final long MS_MONTH 		= 	MS_DAY		* 30;
	private static final long MS_YEAR 		= 	MS_DAY		* 365;
	// times as ticks (milliseconds in one tick)
	private static final long TICK_SECOND	= 	20;
	private static final long TICK_MINUTE 	= 	TICK_SECOND * 60;
	private static final long TICK_HOUR 	= 	TICK_MINUTE * 60;
	private static final long TICK_DAY 		= 	TICK_HOUR 	* 24;
	@SuppressWarnings("unused")
	private static final long TICK_WEEK 	= 	TICK_DAY	* 7;
	@SuppressWarnings("unused")
	private static final long TICK_MONTH 	= 	TICK_DAY	* 30;
	@SuppressWarnings("unused")
	private static final long TICK_YEAR 	= 	TICK_DAY	* 365;
	// map strings to times
	private static final HashMap<String, Long> timeMap = new HashMap<String, Long>() {
		private static final long serialVersionUID = -6200868715335094311L;
	{
		put("ms", 			1l);
		put("millisecond", 	1l);
		put("milliseconds", 1l);
		put("s", 			MS_SECOND);
		put("sec", 			MS_SECOND);
		put("second", 		MS_SECOND);
		put("seconds", 		MS_SECOND);
		put("min", 			MS_MINUTE);
		put("minute", 		MS_MINUTE);
		put("minutes", 		MS_MINUTE);
		put("h", 			MS_HOUR);
		put("hour", 		MS_HOUR);
		put("hours", 		MS_HOUR);
		put("d", 			MS_DAY);
		put("day", 			MS_DAY);
		put("days", 		MS_DAY);
		put("w", 			MS_WEEK);
		put("week", 		MS_WEEK);
		put("weeks", 		MS_WEEK);
		put("m", 			MS_MONTH);
		put("month", 		MS_MONTH);
		put("months", 		MS_MONTH);
		put("y", 			MS_YEAR);
		put("year", 		MS_YEAR);
		put("years", 		MS_YEAR);
		put("", 			50l);
		put("t", 			50l);
		put("tick", 		50l);
		put("ticks", 		50l);
	}};
	
	
	/**
	 * Returns the list of suffixes which can be used to determine the the format
	 * @return suffix for the time format
	 */
	public static Set<String> getTimeNames() {
		return timeMap.keySet();
	}
	
	
	/**
	 * Parses a given string to a new TimeData object
	 * @param text string to format to timedata
	 * @return a new timeData object
	 */
	public static TimeData parseTime(String text) {
		long time = -1;
		String[] parts = text.split(",");
		
		for(String part : parts) {
			// check if there are numbers in the string beginning
			Matcher m = DIGITS.matcher(part);
			if(!m.find())
				continue;
			
			// split the string into number and identifier part
			String numbers = part.substring(0, m.end());
			String letters = part.replaceFirst(numbers, "");
			
			Long scale = timeMap.get(letters.toLowerCase());
			
			// if no length found for this time format, continue
			if(scale == null)
				continue;
			
			try {
				// parse the number part to numbers
				long inputTime = Long.parseLong(numbers);
				
				// remove incorrect time identifier
				if(time == -1)
					time = 0;
				
				// add the found time to time
				time = inputTime * scale;
			} catch (NumberFormatException e) {
				continue;
			}
		}
		
		// if incorrect time return null
		if(time == -1)
			return null;
		else
			return new TimeData(time); // create new millisecond holder for parsed time
	}
}