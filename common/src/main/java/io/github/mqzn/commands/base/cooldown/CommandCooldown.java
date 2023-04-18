package io.github.mqzn.commands.base.cooldown;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class CommandCooldown {

	@NotNull
	public static final CommandCooldown EMPTY = new CommandCooldown(0, TimeUnit.SECONDS);
	private final Duration duration;
	private final long value;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (CommandCooldown) obj;
		return this.value == that.value &&
						Objects.equals(this.unit, that.unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, unit);
	}

	@Override
	public String toString() {
		return "CommandCooldown[" +
						"value=" + value + ", " +
						"unit=" + unit + ']';
	}


}
