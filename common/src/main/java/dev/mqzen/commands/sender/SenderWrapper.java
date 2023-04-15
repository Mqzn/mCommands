package dev.mqzen.commands;

public interface SenderFactory {

	boolean isConsole();
	
	void sendMessage(String msg);

}
