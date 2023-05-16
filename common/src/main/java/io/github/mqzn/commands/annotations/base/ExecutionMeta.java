package io.github.mqzn.commands.annotations.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExecutionMeta {
	
	String syntax() default "";
	
	Class<?> senderType() default Object.class;
	
	String description() default "";
	
	String permission() default "";
	
	
}
