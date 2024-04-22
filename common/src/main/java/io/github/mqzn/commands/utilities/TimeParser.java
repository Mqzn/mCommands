package io.github.mqzn.commands.utilities;

import java.util.concurrent.TimeUnit;

/**
 * The main class that is used to parse time strings inputs
 * The algorithm is fully written by me (aka Mqzen)
 * Make sure to know just when to use it properly :D !
 *
 * @author Mqzen
 */
public class TimeParser {
	private long days;
	private long hours;
	private long minutes;
	private long seconds;
	
	private TimeParser(long millis) {
		this.days = millis / 86400000L;
		this.hours = (millis / 3600000L) % 24L;
		this.minutes = (millis / 60000L) % 60L;
		this.seconds = (millis / 1000L) % 60L;
	}
	
	private TimeParser(String timePeriod) {
		char[] chars = timePeriod.toCharArray();
		int i = 0;
		while (i < timePeriod.length()) {
			if (Character.isDigit(chars[i])) {
				StringBuilder digitToCollect = new StringBuilder();
				int start = i;
				while (Character.isDigit(chars[start])) {
					digitToCollect.append(chars[start]);
					start++;
				}
				
				char unit = chars[start];
				int digit = Integer.parseInt(digitToCollect.toString());
				switch (unit) {
					case 'd', 'D' -> this.days += digit;
					case 'h', 'H' -> this.hours += digit;
					case 'm', 'M' -> this.minutes += digit;
					case 's', 'S' -> this.seconds += digit;
				}
				i = start;
			}
			i++;
		}
	}
	
	public static TimeParser parse(String timePeriod) {
		return new TimeParser(timePeriod);
	}
	
	public static TimeParser parse(long millis) {
		return new TimeParser(millis);
	}
	
	public Pair<Long, TimeUnit> highestLogicalUnitValue() {
		if (this.days != 0L) {
			return Pair.of(this.days, TimeUnit.DAYS);
		}
		if (this.hours != 0L) {
			return Pair.of(this.hours, TimeUnit.HOURS);
		}
		if (this.minutes != 0L) {
			return Pair.of(this.minutes, TimeUnit.MINUTES);
		}
		return Pair.of(this.seconds, TimeUnit.SECONDS);
	}
}