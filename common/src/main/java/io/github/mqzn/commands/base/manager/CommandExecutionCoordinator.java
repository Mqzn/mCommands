package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.context.CommandContext;
import io.github.mqzn.commands.base.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public sealed abstract class CommandExecutionCoordinator<S> {
	
	@NotNull
	protected final Command<S> command;
	
	private CommandExecutionCoordinator(@NotNull Command<S> manager) {
		this.command = manager;
	}
	
	static <S> CommandExecutionCoordinator<S> async(@NotNull Command<S> manager) {
		return new AsyncCommandCoordinator<>(manager);
	}
	
	static <S> CommandExecutionCoordinator<S> sync(@NotNull Command<S> manager) {
		return new SyncCommandCoordinator<>(manager);
	}
	
	public static <S> CommandExecutionCoordinator<S> fromType(@NotNull Command<S> manager, Type type) {
		return type == Type.SYNC ? sync(manager) : async(manager);
	}
	
	public Command<S> command() {
		return command;
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
		
		private AsyncCommandCoordinator(@NotNull Command<S> manager) {
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
	
	
	final static class SyncCommandCoordinator<S> extends CommandExecutionCoordinator<S> {
		public SyncCommandCoordinator(Command<S> manager) {
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
