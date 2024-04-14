package io.github.mqzn.commands.test;


public record ClientSender(String name) {
	public void sendMessage(String msg) {
		System.out.println("To " + name + " : " + msg);
	}
}
