package io.github.mqzn.commands.base;

import io.github.mqzn.commands.arguments.Argument;
import io.github.mqzn.commands.arguments.ArgumentInteger;
import io.github.mqzn.commands.base.context.Context;
import io.github.mqzn.commands.base.cooldown.CommandCooldown;
import io.github.mqzn.commands.base.manager.CommandManager;
import io.github.mqzn.commands.base.manager.CommandSuggestionEngine;
import io.github.mqzn.commands.base.syntax.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public sealed interface Command<S> permits Command.Builder.ImmutableCommandImpl {
	
	/**
	 * Creating a builder instance for
	 * proper building of the command instance
	 *
	 * @param manager the command manager for instantiating the command
	 *                since every command is linked to it's manager
	 * @param name    the command name to start off by using it
	 * @param <P>     the bootstrap
	 * @param <S>     the sender
	 * @return the builder instance created
	 * @see Command
	 */
	static <P, S> Builder<S> builder(CommandManager<P, S> manager, String name) {
		return new Builder<>(manager, name);
	}
	
	/**
	 * Command Manager instance
	 * that will be used in creation of this command
	 *
	 * @return the manager shared instance
	 */
	CommandManager<?, S> manager();
	
	/**
	 * The name of the command
	 *
	 * @return the command name
	 */
	String name();
	
	/**
	 * The info of the command,
	 * like description, permission, and it's aliases
	 *
	 * @return the information of the command
	 */
	@NotNull CommandInfo info();
	
	/**
	 * Represents a command cooldown
	 *
	 * @return the cooldown of the command
	 */
	@NotNull CommandCooldown cooldown();
	
	/**
	 * The requirements for the command to be executed
	 *
	 * @return The requirements for the command to be executed
	 */
	@NotNull Set<CommandRequirement<S>> requirements();
	
	/**
	 * The default command execution when no args are provided !
	 *
	 * @param sender the command executor !
	 */
	void defaultExecution(S sender, Context<S> commandContext);
	
	/**
	 * The syntaxes registered for this command
	 *
	 * @return The syntaxes registered for this command
	 * @see CommandSyntax
	 */
	@NotNull List<CommandSyntax<S>> syntaxes();
	
	/**
	 * The tree of subcommands of this
	 * command specifically
	 *
	 * @return the tree of all subcommands hierarchy
	 */
	@NotNull CommandTree<S> tree();
	
	
	/**
	 * Fetches the engine controlling and caching all
	 * suggestions for this command in a complex mapping
	 *
	 * @return the command suggestions holder
	 */
	@NotNull CommandSuggestionEngine<S> suggestions();
	
	
	default boolean hasCooldown() {
		return !cooldown().isEmpty();
	}
	
	/**
	 * An internal builder class for the command
	 * class {@link Command<S>}
	 *
	 * @param <S> the sender type
	 * @see Command
	 * @see CommandManager
	 * @see CommandSyntax
	 * @see CommandRequirement
	 */
	final class Builder<S> {
		
		@NotNull
		private final CommandManager<?, S> manager;
		
		@NotNull
		private final String name;
		@NotNull
		private final Set<CommandRequirement<S>> requirements = new HashSet<>();
		@NotNull
		private final List<CommandSyntax<S>> syntaxes = new ArrayList<>();
		@NotNull
		private CommandCooldown cooldown = CommandCooldown.EMPTY;
		@NotNull
		private CommandInfo info = CommandInfo.EMPTY_INFO;
		
		private CommandExecution<S, S> defaultExecutor;
		
		Builder(@NotNull CommandManager<?, S> manager, @NotNull String name) {
			this.manager = manager;
			this.name = name;
		}
		
		public Builder<S> info(@NotNull CommandInfo info) {
			this.info = info;
			return this;
		}
		
		@SuppressWarnings("UnusedReturnValue")
		public Builder<S> requirement(@NotNull CommandRequirement<S> requirement) {
			this.requirements.add(requirement);
			return this;
		}
		
		@SafeVarargs
		public final Builder<S> syntax(@NotNull CommandSyntax<S>... syntaxes) {
			this.syntaxes.addAll(Arrays.asList(syntaxes));
			return this;
		}
		
		@SuppressWarnings("UnusedReturnValue")
		public Builder<S> defaultExecutor(@NotNull CommandExecution<S, S> execution) {
			this.defaultExecutor = execution;
			return this;
		}
		
		@SuppressWarnings("UnusedReturnValue")
		public Builder<S> cooldown(@NotNull CommandCooldown cooldown) {
			this.cooldown = cooldown;
			return this;
		}
		
		
		public synchronized Command<S> build() {
			if (manager.helpProvider() != null) {
				
				ArgumentInteger pageArg = (ArgumentInteger) Argument.integer("page").min(1);
				pageArg.setOptional(true);
				pageArg.setDefaultValue(1);
				
				CommandSyntax<S> helpSyntax =
					CommandSyntaxBuilder.<S, S>genericBuilder(manager.getSenderWrapper().senderType(), name).execute((sender, context) -> {
							
							@Nullable Integer page = context.getArgument("page");
							if (page == null) return;
							manager.handleHelpProvider(sender, context, name, page, syntaxes);
						}
					).argument(Argument.literal("help")).argument(pageArg).build();
				
				syntaxes.add(helpSyntax);
			}
			
			return new ImmutableCommandImpl<>(manager, name, info, cooldown, requirements, syntaxes, defaultExecutor);
		}
		
		
		static final class ImmutableCommandImpl<S> implements Command<S> {
			
			@NotNull
			private final CommandManager<?, S> manager;
			
			@NotNull
			private final String name;
			
			@NotNull
			private final CommandInfo info;
			
			@NotNull
			private final CommandCooldown cooldown;
			
			@NotNull
			private final Set<CommandRequirement<S>> requirements;
			
			private List<CommandSyntax<S>> syntaxes;
			
			@Nullable
			private final CommandExecution<S, S> execution;
			
			@NotNull
			private final CommandTree<S> tree;
			
			@NotNull
			private final CommandSuggestionEngine<S> suggestionsEngine;
			
			ImmutableCommandImpl(@NotNull CommandManager<?, S> manager,
			                     @NotNull String name,
			                     @NotNull CommandInfo info,
			                     @NotNull CommandCooldown cooldown,
			                     @NotNull Set<CommandRequirement<S>> requirements,
			                     @NotNull List<CommandSyntax<S>> syntaxes,
			                     @Nullable CommandExecution<S, S> execution) {
				this.manager = manager;
				this.name = name;
				this.info = info;
				this.cooldown = cooldown;
				this.requirements = requirements;
				this.syntaxes = syntaxes;
				this.execution = execution;
				this.tree = CommandTree.create(this);
				this.suggestionsEngine = CommandSuggestionEngine.create(this);
				
				this.syntaxes = syntaxes.stream().map((syntax)-> {
					if(!(syntax instanceof SubCommandSyntax<S>)) return syntax;
					((SubCommandSyntax<S>) syntax).setParentArguments(tree.getParentalArguments(((SubCommandSyntax<S>) syntax).getName()));
					return syntax;
				}).collect(Collectors.toList());
			}
			
			/**
			 * The name of the command
			 *
			 * @return the command name
			 */
			@Override
			public String name() {
				return name;
			}
			
			/**
			 * The info of the command,
			 * like description, permission, and it's aliases
			 *
			 * @return the information of the command
			 */
			@Override
			public @NotNull CommandInfo info() {
				return info;
			}
			
			/**
			 * The requirements for the command to be executed
			 *
			 * @return The requirements for the command to be executed
			 */
			@Override
			public @NotNull Set<CommandRequirement<S>> requirements() {
				return requirements;
			}
			
			/**
			 * The default command execution when no args are provided !
			 *
			 * @param sender the command executor !
			 */
			@Override
			public void defaultExecution(@NotNull S sender, @NotNull Context<S> commandContext) {
				if (execution != null)
					execution.execute(sender, commandContext);
			}
			
			/**
			 * Fetches the engine controlling and caching all
			 * suggestions for this command in a complex mapping
			 *
			 * @return the command suggestions holder
			 */
			@Override
			public @NotNull CommandSuggestionEngine<S> suggestions() {
				return suggestionsEngine;
			}
			
			@Override
			public @NotNull CommandManager<?, S> manager() {
				return manager;
			}
			
			@Override
			public @NotNull CommandCooldown cooldown() {
				return cooldown;
			}
			
			@Override
			public @NotNull List<CommandSyntax<S>> syntaxes() {
				return syntaxes;
			}
			
			/**
			 * The tree of subcommands of this
			 * command specifically
			 *
			 * @return the tree of all subcommands hierarchy
			 */
			@Override
			public @NotNull CommandTree<S> tree() {
				return tree;
			}
			
			
			@Override
			public boolean equals(Object obj) {
				if (obj == this) return true;
				if (obj == null || obj.getClass() != this.getClass()) return false;
				var that = (ImmutableCommandImpl<?>) obj;
				return Objects.equals(this.name, that.name) &&
					Objects.equals(this.info, that.info);
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(name, info);
			}
			
		}
		
		
	}
	
}
