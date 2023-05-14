package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class CommandExecutionCoordinator<S> {
	
	@NotNull
	protected final CommandManager<?, S> manager;
	
	private CommandExecutionCoordinator(@NotNull CommandManager<?, S> manager) {
		this.manager = manager;
	}
	
	static <S> CommandExecutionCoordinator<S> async(@NotNull CommandManager<?, S> manager) {
		return new AsyncCommandCoordinator<>(manager);
	}
	
	static <S> CommandExecutionCoordinator<S> sync(@NotNull CommandManager<?, S> manager) {
		return new SyncCommandCoordinator<>(manager);
	}
	
	public CommandManager<?, S> manager() {
		return manager;
	}
	
	public abstract Type type();
	
	public abstract <C> CompletableFuture<ExecutionResult> coordinateExecution(@NotNull C sender,
	                                                                           @NotNull CommandSyntax<S> syntax,
	                                                                           @NotNull CommandContext<S> context);
	
	
	public enum ExecutionResult {
		SUCCESS,
		
		FAILED
		
	}
	
	
	public enum Type {
		
		ASYNC,
		SYNC
	}
	
	final static class AsyncCommandCoordinator<S> extends CommandExecutionCoordinator<S> {
		
		private AsyncCommandCoordinator(@NotNull CommandManager<?, S> manager) {
			super(manager);
		}
		
		@Override
		public Type type() {
			return Type.ASYNC;
		}
		
		@Override
		public <C> CompletableFuture<ExecutionResult> coordinateExecution(@NotNull C sender,
		                                                                  @NotNull CommandSyntax<S> syntax,
		                                                                  @NotNull CommandContext<S> context) {
			return CompletableFuture.supplyAsync(() -> {
				
				try {
					syntax.execute(sender, context);
					return ExecutionResult.SUCCESS;
				} catch (Exception ex) {
					ex.printStackTrace();
					return ExecutionResult.FAILED;
				}
				
			});
			
		}
	}
	
	
	static class SyncCommandCoordinator<S> extends CommandExecutionCoordinator<S> {
		public SyncCommandCoordinator(CommandManager<?, S> manager) {
			super(manager);
		}
		
		@Override
		public Type type() {
			return Type.SYNC;
		}
		
		@Override
		public <C> CompletableFuture<ExecutionResult> coordinateExecution(@NotNull C sender,
		                                                                  @NotNull CommandSyntax<S> syntax,
		                                                                  @NotNull CommandContext<S> context) {
			
			try {
				syntax.execute(sender, context);
				return CompletableFuture.completedFuture(ExecutionResult.SUCCESS);
			} catch (Exception ex) {
				ex.printStackTrace();
				return CompletableFuture.completedFuture(ExecutionResult.FAILED);
			}
			
		}
		
		
	}
	
	
}
