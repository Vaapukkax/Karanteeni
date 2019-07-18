package net.karanteeni.core.information.time;

import java.util.Date;

public class TimeData {
	private long ms;
	private static long SECOND = 1000;
	private static long MINUTE = SECOND * 60;
	private static long HOUR = MINUTE * 60;
	private static long DAY = HOUR * 24;
	private static long WEEK = DAY * 7;
	private static long MONTH = DAY * 30;
	private static long YEAR = DAY * 365;
	
	
	/**
	 * Returns the given object as date to the future
	 * @return date to the future forwarded by this time
	 */
	public Date asDate() {
		return new Date(System.currentTimeMillis() + ms);
	}
	
	
	/**
	 * Initializes a new TimeData object
	 * @param milliseconds milliseconds in this time
	 */
	public TimeData(long milliseconds) {
		if(milliseconds < 0) this.ms = -1;
		else this.ms = milliseconds;
	}
	
	
	/**
	 * Returns true if the time is invalid
	 * @return true if invalid, false if correct
	 */
	public boolean isInvalid() {
		return this.ms == -1;
	}
	
	
	/**
	 * Returns the amount of milliseconds in this time
	 * @return milliseconds
	 */
	public long getMilliseconds() {
		return this.ms;
	}
	
	
	public long getSeconds() {
		return this.ms / SECOND;
	}
	
	
	public long getMinutes() {
		return this.ms / MINUTE;
	}
	
	
	public long getHours() {
		return this.ms / HOUR;
	}
	
	
	public long getDays() {
		return this.ms / DAY;
	}
	
	
	public long getWeeks() {
		return this.ms / WEEK;
	}
	
	
	public long getMonths() {
		return this.ms / MONTH;
	}
	
	
	public long getYears() {
		return this.ms / YEAR;
	}
	
	
	/**
	 * Returns the extra milliseconds after all time has been split
	 * between seconds
	 * @return milliseconds
	 */
	public long getExtraMilliseconds() {
		return (this.ms - this.getSeconds() * SECOND);
	}
	
	
	public long getExtraSeconds() {
		return (this.ms - this.getMinutes() * MINUTE) / SECOND;
	}
	
	
	public long getExtraMinutes() {
		return (this.ms - this.getHours() * HOUR) / MINUTE;
	}
	
	
	public long getExtraHours() {
		return (this.ms - this.getDays() * DAY) / HOUR;
	}
	
	
	public long getExtraDays(boolean discardWeeks) {
		if(discardWeeks)
			return (this.ms - this.getMonths() * MONTH) / DAY;
		else
			return (this.ms - this.getWeeks() * WEEK) / DAY;
	}
	
	
	public long getExtraWeeks() {
		return (this.ms - this.getMonths() * MONTH) / WEEK;
	}
	
	
	public long getExtraMonths() {
		return (this.ms - this.getYears() * YEAR) / MONTH;
	}
	
	
	/**
	 * Converts the time to ticks
	 * @return
	 */
	public long asTicks() {
		return this.ms / 50;
	}
}
