package io.github.mqzn.commands.base.cooldown;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CommandCooldown {
	
	public static final CommandCooldown EMPTY = new CommandCooldown(0, TimeUnit.SECONDS);
	private final long value;
	private final TimeUnit unit;
	private final Duration duration;
	
	public CommandCooldown(long value, TimeUnit unit) {
		this.value = value;
		this.unit = unit;
		this.duration = Duration.of(value, unit.toChronoUnit());
	}
	
	public boolean isEmpty() {
		return value <= 0;
	}
	
	public long toMillis() {
		return duration.toMillis();
	}
	
	public long getValue() {
		return value;
	}
	
	public TimeUnit getUnit() {
		return unit;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof CommandCooldown that)) return false;
		return value == that.value && unit == that.unit;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value, unit);
	}
}


