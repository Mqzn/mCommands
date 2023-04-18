package io.github.mqzn.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CustomSender {


	@NotNull
	private final CommandSender sender;

	public CustomSender(@NotNull CommandSender sender) {
		this.sender = sender;
	}

	public void sendWoo() {
		sender.sendMessage("WOOOOOOO");
	}

}
