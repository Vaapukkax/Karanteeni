package net.karanteeni.core.information.time;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public class Time {
	private static long SECOND = 	1000;
	private static long MINUTE = 	60000;
	private static long HOUR = 		3600000;
	private static long DAY = 		86400000;
	private static long WEEK = 		604800000;
	private static long MONTH = 	2592000000l;
	private static long YEAR = 		31536000000l;
	private static String[] YEAR_NAME = new String[] {"year", "vuotta", "y"};
	private static String[] MONTH_NAME = new String[] {"month", "months", "mon", "kuukautta"};
	private static String[] WEEK_NAME = new String[] {"week","w","viikkoa"};
	private static String[] DAY_NAME = new String[] {"d","day","days","päivää"};
	private static String[] HOUR_NAME = new String[] {"h","hour","tuntia"};
	private static String[] MINUTE_NAME = new String[] {"min","minute","minuuttia"};
	private static String[] SECOND_NAME = new String[] {"s","second","sekuntia"};
	private static Pattern[] YEAR_PATTERN;
	private static Pattern[] MONTH_PATTERN;
	private static Pattern[] WEEK_PATTERN;
	private static Pattern[] DAY_PATTERN;
	private static Pattern[] HOUR_PATTERN;
	private static Pattern[] MINUTE_PATTERN;
	private static Pattern[] SECOND_PATTERN;
	private static Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
	
	private long time;
	
	/**
	 * Initializes this classs patterns
	 */
	public static void initialize()
	{
		if(YEAR_PATTERN == null)
		{
			YEAR_PATTERN = new Pattern[YEAR_NAME.length];
			for(int i = 0; i < YEAR_NAME.length; ++i)
				YEAR_PATTERN[i] = Pattern.compile("\\d+"+YEAR_NAME[i]);
			
			MONTH_PATTERN = new Pattern[MONTH_NAME.length];
			for(int i = 0; i < MONTH_NAME.length; ++i)
				MONTH_PATTERN[i] = Pattern.compile("\\d+"+MONTH_NAME[i]);
			
			WEEK_PATTERN = new Pattern[WEEK_NAME.length];
			for(int i = 0; i < WEEK_NAME.length; ++i)
				WEEK_PATTERN[i] = Pattern.compile("\\d+"+WEEK_NAME[i]);
			
			DAY_PATTERN = new Pattern[DAY_NAME.length];
			for(int i = 0; i < DAY_NAME.length; ++i)
				DAY_PATTERN[i] = Pattern.compile("\\d+"+DAY_NAME[i]);
			
			HOUR_PATTERN = new Pattern[HOUR_NAME.length];
			for(int i = 0; i < HOUR_NAME.length; ++i)
				HOUR_PATTERN[i] = Pattern.compile("\\d+"+HOUR_NAME[i]);
			
			MINUTE_PATTERN = new Pattern[MINUTE_NAME.length];
			for(int i = 0; i < SECOND_NAME.length; ++i)
				MINUTE_PATTERN[i] = Pattern.compile("\\d+"+MINUTE_NAME[i]);
			
			SECOND_PATTERN = new Pattern[SECOND_NAME.length];
			for(int i = 0; i < SECOND_NAME.length; ++i)
				SECOND_PATTERN[i] = Pattern.compile("\\d+"+SECOND_NAME[i]);
		}
	}
	
	/**
	 * Initializes the time to current time
	 */
	public Time()
	{
		time = System.currentTimeMillis();
	}
	
	/**
	 * Initializes the time to given value
	 * @param time
	 */
	public Time(long time)
	{
		this.time = time;
	}
	
	/**
	 * Parses a list to a time removing the time arguments from the list
	 * @param args
	 * @return
	 */
	public static Time parseTime(List<String> args)
	{
		long time = 0;
		
		short counter = 0;
		//Check all times possible, don't loop over
		for(int j = 0; j < args.size() && counter < 8; ++j)
		{
			++counter;
			String arg = args.get(j);
			String primArg = arg;
			boolean found = false;
			
			// Get all the years from the given string
			for(Pattern p : YEAR_PATTERN)
			{
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*YEAR;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the months from the given string
			for(Pattern p : MONTH_PATTERN)
			{
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*MONTH;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the weeks from the given string
			for(Pattern p : WEEK_PATTERN)
			{
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*WEEK;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the years from the given string
			for(Pattern p : DAY_PATTERN)
			{
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*DAY;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the years from the given string
			for(Pattern p : HOUR_PATTERN)
			{				
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*HOUR;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the years from the given string
			for(Pattern p : MINUTE_PATTERN)
			{
				Matcher m = p.matcher(arg);
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*MINUTE;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;
					}
					catch(Exception e) {}
				}
			}
			
			// Get all the years from the given string
			for(Pattern p : SECOND_PATTERN)
			{
				Matcher m = p.matcher(arg);
				
				if(m.find())
				{
					try{
						Matcher ma = NUMBER_PATTERN.matcher(arg);
						ma.find();
						time += Integer.parseInt(ma.group())*SECOND;
						arg = arg.replaceFirst(m.group(), ""); //Remove the found string
						found = true;						
					}
					catch(Exception e) {}
				}
			}
			
			//If a match was found, remove this string from list
			if(found)
			{
				args.remove(primArg);
				--j;
			}
		}
		
		return new Time(time);
	}
	
	/**
	 * 10v 10kk 10d 10min 10s
	 */
	@Override
	public String toString()
	{
		String str = "";
		long time = this.time;
		
		if(time >= YEAR)
		{
			str += (int)(time/YEAR) + "v";
			time = time%YEAR;
		}
		
		if(time >= MONTH)
		{
			if(str.equals(""))
				str += (int)(time/MONTH) + "kk";
			else
				str += " " + (int)(time/MONTH) + "kk";
			time = time%MONTH;
		}
		
		if(time >= DAY)
		{
			if(str.equals(""))
				str += (int)(time/DAY) + "d";
			else
				str += " " + (int)(time/DAY) + "d";
			time = time%DAY;
		}
		
		if(time >= MINUTE)
		{
			if(str.equals(""))
				str += (int)(time/MINUTE) + "min";
			else
				str += " " + (int)(time/MINUTE) + "min";
			time = time%MINUTE;
		}
		
		if(time >= SECOND)
		{
			if(str.equals(""))
				str += (int)(time/SECOND) + "s";
			else
				str += " " + (int)(time/SECOND) + "s";
			time = time%SECOND;
		}
		
		return str;
	}
	
	/**
	 * 
	 */
	public String getDayMinuteSecond()
	{
		String str = "";
		long time = this.time;
		
		if(time >= DAY)
		{
			if(str.equals(""))
				str += (int)(time/DAY) + "d";
			else
				str += " " + (int)(time/DAY) + "d";
			time = time%DAY;
		}
		
		if(time >= MINUTE)
		{
			if(str.equals(""))
				str += (int)(time/MINUTE) + "min";
			else
				str += " " + (int)(time/MINUTE) + "min";
			time = time%MINUTE;
		}
		
		if(time >= SECOND)
		{
			if(str.equals(""))
				str += (int)(time/SECOND) + "s";
			else
				str += " " + (int)(time/SECOND) + "s";
			time = time%SECOND;
		}
		
		return str;
	}
	
	/**
	 * Parses a string to a time
	 * @param arg
	 * @return
	 */
	public static Time parseTime(String arg)
	{
		long time = 0;
		
		// Get all the years from the given string
		for(Pattern p : YEAR_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*YEAR;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		// Get all the months from the given string
		for(Pattern p : MONTH_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*MONTH;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		// Get all the weeks from the given string
		for(Pattern p : WEEK_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*WEEK;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		// Get all the years from the given string
		for(Pattern p : DAY_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*DAY;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		// Get all the years from the given string
		for(Pattern p : HOUR_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*HOUR;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		// Get all the years from the given string
		for(Pattern p : MINUTE_PATTERN)
		{
			Matcher m = p.matcher(arg);
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*MINUTE;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}
		
		Bukkit.broadcastMessage(arg);
		
		// Get all the years from the given string
		for(Pattern p : SECOND_PATTERN)
		{
			Matcher m = p.matcher(arg);
			
			if(m.find())
			{
				try{
					Matcher ma = NUMBER_PATTERN.matcher(arg);
					ma.find();
					time += Integer.parseInt(ma.group())*SECOND;
					arg = arg.replaceFirst(m.group(), ""); //Remove the found string
				}
				catch(Exception e) {}
			}
		}	
		
		return new Time(time);
	}
	
	/**
	 * Sets this time
	 * @param time
	 */
	public void setTime(long time)
	{
		this.time = time;
	}
	
	/**
	 * Adds this many years to this time
	 * @param years
	 * @return
	 */
	public Time addYears(int years)
	{
		this.time += years*YEAR;
		return this;
	}
	
	/**
	 * Adds this many months to this time
	 * @param months
	 * @return
	 */
	public Time addMonths(int months)
	{
		this.time += months*MONTH;
		return this;
	}
	
	/**
	 * Adds this many weeks to the this time
	 * @param weeks
	 * @return
	 */
	public Time addWeeks(int weeks)
	{
		this.time += weeks*WEEK;
		return this;
	}
	
	/**
	 * Adds this many days to this time
	 * @param days
	 * @return
	 */
	public Time addDays(int days)
	{
		this.time += days*DAY;
		return this;
	}
	
	/**
	 * Adds this many hours to this time
	 * @param hours
	 * @return
	 */
	public Time addHours(int hours)
	{
		this.time += hours*HOUR;
		return this;
	}
	
	/**
	 * Adds this many minutes to this time
	 * @param minutes
	 * @return
	 */
	public Time addMinutes(int minutes)
	{
		this.time += minutes*MINUTE;
		return this;
	}
	
	/**
	 * Adds this many seconds to this time
	 * @param seconds
	 * @return
	 */
	public Time addSeconds(int seconds)
	{
		this.time += seconds*SECOND;
		return this;
	}
	
	/**
	 * Returns the time difference between this time and system time
	 * @return
	 */
	public long getTimeDifference()
	{
		return System.currentTimeMillis()-time;
	}
	
	/**
	 * Returns how many years are in this time
	 * @return
	 */
	public int getYears()
	{
		return (int)(time/YEAR);
	}
	
	/**
	 * Returns how many months are in this time
	 * @return
	 */
	public int getMonths()
	{
		return (int)(time/MONTH);
	}
	
	/**
	 * Returns how many weeks are in this time
	 * @return
	 */
	public int getWeeks()
	{
		return (int)(time/WEEK);
	}
	
	/**
	 * Returns how many days are in this time
	 * @return
	 */
	public int getDays()
	{
		return (int)(time/DAY);
	}
	
	/**
	 * Returns how many hours are in this time
	 * @return
	 */
	public long getHours()
	{
		return (int)(time/HOUR);
	}
	
	/**
	 * Returns how many minutes are in this time
	 * @return
	 */
	public long getMinutes()
	{
		return (int)(time/MINUTE);
	}
	
	/**
	 * Returns how many years are in this time
	 * @return
	 */
	public int getSeconds()
	{
		return (int)(time/SECOND);
	}
	
	/**
	 * Returns the actual long time in this class.
	 * Basically getMillis()
	 * @return
	 */
	public long getTime()
	{
		return time;
	}
}
