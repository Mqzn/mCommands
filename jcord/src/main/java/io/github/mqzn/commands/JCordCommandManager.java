package io.github.mqzn.commands;

import io.github.mqzn.commands.base.Command;
import io.github.mqzn.commands.base.manager.AbstractCommandManager;
import io.github.mqzn.commands.base.manager.CommandExecutionCoordinator;
import io.github.mqzn.commands.sender.SenderWrapper;
import net.kyori.adventure.text.TextComponent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public final class JCordCommandManager extends AbstractCommandManager<DiscordApi, User> {


	private final char commandStarter;

	public JCordCommandManager(@NotNull DiscordApi bootstrap,
	                           char commandPrefix,
	                           @NotNull Server server,
	                           @NotNull ServerTextChannel commandsChannel,
	                           CommandExecutionCoordinator.@NotNull Type coordinator) {
		super(bootstrap, new MemberWrapper(server, commandsChannel), coordinator);
		this.commandStarter = commandPrefix;
	}

	public JCordCommandManager(char commandPrefix,
	                           @NotNull DiscordApi bootstrap,
	                           @NotNull Server server,
	                           @NotNull ServerTextChannel commandsChannel) {
		super(bootstrap, new MemberWrapper(server, commandsChannel));
		this.commandStarter = commandPrefix;

		bootstrap.addMessageCreateListener((e) -> {
			String rawString = e.getMessageContent();
			String[] split = rawString.split(Pattern.quote(" "));

			String cmdUsed = split[0];
			char prefix = cmdUsed.charAt(0);
			if (prefix != commandStarter() || e.getChannel().getId() != commandsChannel.getId()) return;

			String cmd = cmdUsed.substring(1);

			String[] actualArgs = new String[split.length - 1];
			System.arraycopy(split, 1, actualArgs, 0, split.length - 1);

			var officialCmd = getCommand(cmd);
			if (officialCmd != null) {
				e.getMessageAuthor().asUser()
								.ifPresent((user) -> executeCommand(officialCmd, user, actualArgs));

			}

		});
	}

	@Override
	public char commandStarter() {
		return commandStarter;
	}

	@Override
	public <C extends Command<User>> void registerCommand(C command) {
		super.registerCommand(command);
	}

	private static class MemberWrapper implements SenderWrapper<User> {

		private final Server server;
		private final ServerTextChannel channel;

		public MemberWrapper(Server server, ServerTextChannel textChannel) {
			this.server = server;
			this.channel = textChannel;
		}


		@Override
		public Class<User> senderType() {
			return User.class;
		}

		@Override
		public boolean isConsole(User sender) {
			return false;
		}

		@Override
		public void sendMessage(User sender, String msg) {
			channel.sendMessage(msg).join();
		}

		@Override
		public void sendMessage(User sender, TextComponent component) {
			sendMessage(sender, component.content());
		}

		@Override
		public boolean canBeSender(Class<?> type) {
			return User.class.isAssignableFrom(type);
		}

		@Override
		public boolean hasPermission(User user, @Nullable String name) {

			try {
				PermissionType permissionType = PermissionType.valueOf(name);
				return server.hasPermission(user, permissionType);
			} catch (EnumConstantNotPresentException ex) {
				return false;
			}

		}

		@Override
		public String senderName(User sender) {
			return sender.getName();
		}

	}

}
