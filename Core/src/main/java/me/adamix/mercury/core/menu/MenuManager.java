package me.adamix.mercury.core.menu;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuManager {
	private final Map<Class<? extends MercuryMenu>, MercuryMenu> menuMap = new HashMap<>();

	public <T extends MercuryMenu> void register(@NotNull T menu) {
		menuMap.put(menu.getClass(), menu);
	}

	public <T extends MercuryMenu> @NotNull Optional<T> get(@NotNull Class<T> clazz) {
		//noinspection unchecked
		return Optional.ofNullable((T) menuMap.get(clazz));
	}
}
