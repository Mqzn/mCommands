package io.github.mqzn.commands.annotations.base;

import io.github.mqzn.commands.base.CommandRequirement;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
	String name();
	
	CommandExecutionCoordinator.Type executionType() default CommandExecutionCoordinator.Type.SYNC;
	
	String permission() default "";
	
	String description() default "";
	
	String[] aliases() default {};
	
	Class<? extends CommandRequirement<?>>[] requirements() default {};
	
}
