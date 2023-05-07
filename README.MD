# mCommands

An advanced general purpose command dispatching framework
designed using OOP concepts.

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

We have our own repo in maven central
so all you have to do is like this:

```gradle 
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mqzn:mCommands-<platform>:1.0.2'
}
```

## Platforms

### Spigot

The spigot platform is for minecraft spigot api development

```gradle
implementation 'io.github.mqzn:mCommands-spigot:1.0.2'
```

### Annotations

This platform allows you to use annotations to declare and register your commands

```gradle
implementation 'io.github.mqzn:mCommands-annotations:1.0.2'
```

### Bungee

This bungeecord platform is for minecraft bungeecord proxy api development, allows you
to declare and register bungeecord commands.

```gradle
implementation 'io.github.mqzn:mCommands-bungee:1.0.2'
```

## Quick Usage Overview
```java
public final class SpigotPluginTest extends JavaPlugin {
	private SpigotCommandManager commandManager;
	
    @Override
	public void onEnable() {
	    var cmd = Command.builder(commandManager, "test")
				.info(new CommandInfo("test.perm", "Test cmd", "testis"))
				.requirement(SpigotCommandRequirement.ONLY_PLAYER_EXECUTABLE)
				.cooldown(new CommandCooldown(5, TimeUnit.MINUTES))
				.syntax(
					SpigotCommandSyntaxBuilder.builder("test")
					  .argument(Argument.literal("testsub"))
                      .execute((sender, context) -> {
                      
						sender.sendMessage("Test sub works !");
											
					  }).build())
				.defaultExecutor((s, context) -> s.sendMessage("OMG NO ARGS !"))
				.build();
      
      commandManager.register(cmd);
    }
	
}


```

## Wiki

Wiki for mCommands is available [here](https://github.com/Mqzn/mCommands/wiki)

