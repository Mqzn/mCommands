package io.github.commands;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.commands.arguments.ArgumentOnlinePlayer;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import org.jetbrains.annotations.NotNull;

public final class VelocityCommandManager extends AbstractCommandManager<ProxyServer, CommandSource> {
	
	@NotNull
	private final Object bootstrapObj;
	
	public VelocityCommandManager(@NotNull Object bootstrapObj, @NotNull ProxyServer plugin) {
		super(plugin, new VelocitySenderWrapper());
		this.bootstrapObj = bootstrapObj;
		this.registerCaptions();
		this.registerTypes();
	}
	
	@Override
	public char commandPrefix() {
		return '/';
	}
	
	private void registerCaptions() {
		captionRegistry.registerCaption(VelocityCaption.INVALID_ARGUMENT);
		captionRegistry.registerCaption(VelocityCaption.UNKNOWN_COMMAND);
		captionRegistry.registerCaption(VelocityCaption.NO_PERMISSION);
		captionRegistry.registerCaption(VelocityCaption.ONLY_PLAYER_EXECUTABLE);
		captionRegistry.registerCaption(VelocityCaption.NO_HELP_TOPIC_AVAILABLE);
	}
	
	private void registerTypes() {
		typeRegistry().registerArgumentConverter(Player.class, (data) -> new ArgumentOnlinePlayer(this.bootstrap, data));
	}
	
	@Override
	public <C extends Command<CommandSource>> void registerCommand(C command) {
		super.registerCommand(command);
		
		CommandMeta commandMeta = bootstrap.getCommandManager().metaBuilder(command.name())
			.aliases(command.info().aliases())
			.plugin(bootstrapObj)
			.build();
		
		this.bootstrap.getCommandManager().register(commandMeta, new InternalVelocityCommand(this, command));
	}
	
	@Override
	public void unregisterCommand(String name) {
		super.unregisterCommand(name);
		this.bootstrap.getCommandManager().unregister(name);
	}
	
	
}
