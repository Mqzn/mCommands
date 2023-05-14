package io.github.mqzn.commands.annotations;

import io.github.mqzn.commands.base.CommandRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
	String name();
	
	String permission() default "";
	
	String description() default "";
	
	String[] aliases() default {};
	
	Class<? extends CommandRequirement<?>>[] requirements() default {};
	
}
