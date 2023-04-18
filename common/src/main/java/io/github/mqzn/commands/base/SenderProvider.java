package io.github.mqzn.commands.base;

import org.jetbrains.annotations.NotNull;

public interface SenderProvider<S, C> {


	static <S> SenderProvider<S, S> self() {

		return (s) -> s;
	}

	C mapSender(@NotNull S sender);


}
