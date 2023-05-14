package io.github.mqzn.commands.base.syntax;

import lombok.Data;
import lombok.Getter;

@Data(staticConstructor = "of")

public class Aliases {
	
	@Getter
	private final String[] array;
	
}
