package io.github.mqzn.commands.annotations.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cooldown {
	
	long value();
	
	TimeUnit unit() default TimeUnit.SECONDS;
	
}
