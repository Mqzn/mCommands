package io.github.mqzn;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.mqzn.arguments.ArgumentOnlinePlayer;
import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import org.jetbrains.annotations.NotNull;

public final class VelocityCommandManager extends AbstractCommandManager<ProxyServer, CommandSource> {

	@NotNull
	private final Object bootstrap;

	public VelocityCommandManager(@NotNull Object bootstrap, @NotNull ProxyServer plugin,
	                              CommandExecutionCoordinator.@NotNull Type coordinator) {
		super(plugin, new VelocitySenderWrapper(), coordinator);
		this.bootstrap = bootstrap;
		this.registerCaptions();
		this.registerTypes();
	}

	public VelocityCommandManager(@NotNull Object bootstrap, @NotNull ProxyServer plugin) {
		super(plugin, new VelocitySenderWrapper());
		this.bootstrap = bootstrap;
		this.registerCaptions();
		this.registerTypes();
	}

	@Override
	public char commandStarter() {
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
		typeRegistry().registerArgumentConverter(Player.class, (data)-> new ArgumentOnlinePlayer(plugin, data));
	}

	@Override
	public <C extends Command<CommandSource>> void registerCommand(C command) {
		super.registerCommand(command);

		CommandMeta commandMeta = plugin.getCommandManager().metaBuilder(command.name())
						.aliases(command.info().aliases())
						.plugin(bootstrap)
						.build();

		plugin.getCommandManager().register(commandMeta, new InternalVelocityCommand(this, command));
	}

	@Override
	public void unregisterCommand(String name) {
		super.unregisterCommand(name);
		plugin.getCommandManager().unregister(name);
	}


}
