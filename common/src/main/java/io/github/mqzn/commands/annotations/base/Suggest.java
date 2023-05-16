package io.github.mqzn.commands.annotations.base;

import io.github.mqzn.commands.base.SuggestionProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Suggest {
	
	String[] value() default {};
	
	Class<? extends SuggestionProvider> provider() default SuggestionProvider.class;
	
}
