package io.github.mqzn.commands.base.caption;


import java.util.Objects;

public final class CaptionKey {
	
	
	public final static CaptionKey UNKNOWN_COMMAND = CaptionKey.of("execution.unknown-command");
	public final static CaptionKey NO_PERMISSION = CaptionKey.of("execution.no-permission");
	public final static CaptionKey ONLY_PLAYER_EXECUTABLE = CaptionKey.of("execution.only-player-allowed");
	public final static CaptionKey INVALID_ARGUMENT = CaptionKey.of("argument.parsing-invalid");
	public static final CaptionKey NO_HELP_TOPIC_AVAILABLE = CaptionKey.of("execution.unknown-help-topic");
	public static final CaptionKey COMMAND_IN_COOLDOWN = CaptionKey.of("execution.command-cooldown");
	
	private final String key;
	
	private CaptionKey(String key) {
		this.key = key;
	}
	
	public static CaptionKey of(String key) {
		return new CaptionKey(key);
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CaptionKey key1)) return false;
		return Objects.equals(key, key1.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(key);
	}
}
