package io.github.mqzn.commands.annotations.subcommands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(SubCommands.class)
public @interface SubCommand {
	
	Class<?> value();
}