package io.github.mqzn.commands.base.cooldown;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode

public final class CommandCooldown {
	
	@NotNull
	public static final CommandCooldown EMPTY = new CommandCooldown(0, TimeUnit.SECONDS);
	
	@EqualsAndHashCode.Exclude
	private final Duration duration;
	
	private final long value;
	
	@NotNull
	private final TimeUnit unit;
	
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
	
	public long value() {
		return value;
	}
	
	public TimeUnit unit() {
		return unit;
	}
	
	
}
