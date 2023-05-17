package io.github.mqzn.commands.base.syntax;


public final class Aliases {
	
	private final String[] array;
	
	Aliases(String[] array) {
		this.array = array;
	}
	
	public static Aliases of(String... arr) {
		return new Aliases(arr);
	}
	
	public String[] getArray() {
		return array;
	}
}
