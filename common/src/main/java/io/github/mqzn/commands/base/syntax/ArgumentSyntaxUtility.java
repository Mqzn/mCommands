package io.github.mqzn.commands.base.syntax;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentLiteral;
import io.github.mqzn.commands.base.manager.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ArgumentSyntaxUtility {
	
	@NotNull
	public final static String[] ARGUMENT_FORMAT_PREFIX_SUFFIX = {
		"<", ">", "[", "]"
	};
	
	private ArgumentSyntaxUtility() {
	}
	
	
	public static boolean isArgRequired(String argSyntax) {
		return argSyntax.startsWith(ARGUMENT_FORMAT_PREFIX_SUFFIX[0]) && argSyntax.endsWith(ARGUMENT_FORMAT_PREFIX_SUFFIX[1]);
	}
	
	public static boolean isArgOptional(String argSyntax) {
		return argSyntax.startsWith(ARGUMENT_FORMAT_PREFIX_SUFFIX[2]) && argSyntax.endsWith(ARGUMENT_FORMAT_PREFIX_SUFFIX[3]);
	}
	
	public static boolean isArgLiteral(String argSyntax) {
		return !isArgRequired(argSyntax) && !isArgOptional(argSyntax);
	}
	
	public static String fetchArgId(String argSyntax) {
		StringBuilder builder = new StringBuilder(argSyntax);
		builder.deleteCharAt(argSyntax.length() - 1);
		builder.deleteCharAt(0);
		
		return builder.toString();
	}
	
	public static boolean aliasesIncludes(CommandAliases commandAliases, String name) {
		return aliasesIncludes(commandAliases.getArray(), name);
	}
	
	public static boolean aliasesIncludes(String[] aliases, String name) {
		for (String aliase : aliases) {
			if (aliase.equalsIgnoreCase(name))
				return true;
		}
		
		return false;
	}
	
	public static <S> String format(
		@NotNull CommandManager<?, S> commandManager,
		@NotNull String commandLabel,
		@NotNull List<@NotNull Argument<?>> arguments) {
		
		String start = commandManager.commandPrefix() == ' ' ? "" : String.valueOf(commandManager.commandPrefix());
		StringBuilder builder = new StringBuilder(start).append(commandLabel).append(" ");
		
		for (int i = 0; i < arguments.size(); i++) {
			
			var arg = arguments.get(i);
			
			if (arg instanceof ArgumentLiteral) {
				builder.append(arg.id()).append(" ");
				continue;
			}
			
			String format = formatArg(arg);
			builder.append(format);
			
			if (i != arguments.size() - 1) builder.append(" ");
			
		}
		
		return builder.toString();
	}
	
	
	public static String formatArg(@NotNull Argument<?> argument) {
		if (argument instanceof ArgumentLiteral) return "";
		String prefix, suffix;
		if (argument.isOptional()) {
			prefix = ARGUMENT_FORMAT_PREFIX_SUFFIX[2];
			suffix = ARGUMENT_FORMAT_PREFIX_SUFFIX[3];
		} else {
			prefix = ARGUMENT_FORMAT_PREFIX_SUFFIX[0];
			suffix = ARGUMENT_FORMAT_PREFIX_SUFFIX[1];
		}
		
		return prefix + argument.id() + suffix;
	}
	
}
