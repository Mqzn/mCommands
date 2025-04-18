<div align="center">
  <img src="https://github.com/Mqzn/mCommands/blob/master/img.png" alt="mCommands logo" width="200" height="150">
</div>

# mCommands (NO LONGER MAINTAINED, DONT RECOMMEND TO USE !)
**I Recommend Going for [Imperat](https://github.com/VelixDevelopments/Imperat)**

An advanced general purpose command dispatching framework
designed using OOP concepts.The library is user-friendly and provides 
high performance along with a high quality of production code.

This library utilizes [Kyori Adventure](https://github.com/KyoriPowered/adventure) for
messages and text styles

so in the installation you must include the dependencies of Kyori in your project's dependencies control tool file
here's an example using build.gradle:

```gradle

dependencies {
    compileOnly "net.kyori:adventure-api:4.13.1"
    compileOnly "net.kyori:adventure-platform-bukkit:4.3.0"
}

```

## Installation

mCommands has its own repo in maven central
so all you have to do is like this:

```gradle 
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mqzn:mCommands-<platform>:<LATEST_VERSION>'
}
```

## Platforms

### Common

The main platform that contain the core of the library

```gradle
implementation 'io.github.mqzn:mCommands-common:<LATEST_VERSION>'
```

### Spigot

The spigot platform is for minecraft spigot api development

```gradle
implementation 'io.github.mqzn:mCommands-spigot:<LATEST_VERSION>'
```

### Bungee

This bungeecord platform is for minecraft bungeecord proxy api development, allows you
to declare and register bungeecord commands.

```gradle
implementation 'io.github.mqzn:mCommands-bungee:<LATEST_VERSION>'
```

## Wiki
If you want to learn how to fully utilize the amazing potential of this library.
you must read the wiki pages starting from [here](https://github.com/Mqzn/mCommands/wiki)
You will also need the wiki in order to make something cool like the example below:

### Code Example
Here's a quick example on how to create a command using mCommands.
```java
public final class SpigotPluginTest extends JavaPlugin {
	
	private SpigotCommandManager commandManager;
	
	@Override
	public void onEnable() {
		var cmd = Command.builder(commandManager, "test")
			.info(new CommandInfo("test.perm", "Test cmd", "testis"))
			.requirement(SpigotCommandRequirement.ONLY_PLAYER_EXECUTABLE)
			.cooldown(new CommandCooldown(5, TimeUnit.MINUTES))
			.executionMeta(
				SpigotCommandSyntaxBuilder.builder(commandManager, "test")
					.argument(Argument.literal("testsub"))
					.execute((sender, context) -> sender.sendMessage("Test sub works !"))
					.build()
			)
			.defaultExecutor((s, context) -> s.sendMessage("OMG NO ARGS !"))
			.build();
		
		commandManager.register(cmd);
	}

}
```


