package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.SenderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SenderProviderRegistry<S> {

	@NotNull
	private final Map<Class<?>, SenderProvider<?, ?>> senderProviders = new HashMap<>();


	@SuppressWarnings("unchecked")
	private <C> @Nullable SenderProvider<S, C> getSenderProvider(Class<C> clazz) {
		return (SenderProvider<S, C>) senderProviders.get(clazz);
	}

	public <C> void registerSenderProvider(Class<C> clazz, SenderProvider<S, C> provider) {
		senderProviders.put(clazz, provider);
	}

	public <C> boolean hasProviderFor(Class<C> clazz) {
		return senderProviders.get(clazz) != null;
	}

	public <C> @Nullable C provideSender(@NotNull S sender, Class<C> clazz) {

		SenderProvider<S, C> provider = getSenderProvider(clazz);
		if (provider != null)
			return provider.mapSender(sender);

		return null;
	}


}
