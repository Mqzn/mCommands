package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.base.SuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SuggestionProviderRegistry {
	
	private final Map<Class<? extends SuggestionProvider>, SuggestionProvider> providers = new HashMap<>();
	
	SuggestionProviderRegistry() {
	
	}
	
	public void register(SuggestionProvider provider) {
		providers.put(provider.getClass(), provider);
	}
	
	public void unregister(Class<? extends SuggestionProvider> clazz) {
		providers.remove(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public @Nullable <SP extends SuggestionProvider> SP getProvider(Class<SP> clazz) {
		return (SP) providers.get(clazz);
	}
	
	
}
