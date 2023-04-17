# mCommands
An advanced general purpose command dispatching framework
designed using OOP concepts.

This library utilizes [Kyori Adventure](https://github.com/KyoriPowered/adventure) for
messages and text styles

## Installation

We have our own repo in maven central
so all you have to do is like this:

```gradle 
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.mqzn:mCommands-<platform>:1.0.0'
}
```

## Platforms

### Spigot
The spigot platform is for minecraft spigot api development

```gradle
implementation 'io.github.mqzn:mCommands-spigot:1.0.0'
```

### Annotations
This platform allows you to use annotations to declare and register your commands
```gradle
implementation 'io.github.mqzn:mCommands-annotations:1.0.0'
```

### Bungee
This bungeecord platform is for minecraft bungeecord proxy api development, allows you
to declare and register bungeecord commands.
```gradle
implementation 'io.github.mqzn:mCommands-bungee:1.0.0'
```

## Wiki
Wiki for mCommands is available [here](https://github.com/Mqzn/mCommands/wiki)
