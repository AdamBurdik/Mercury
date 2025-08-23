package me.adamix.mercury.core.menu.state;

import me.adamix.mercury.core.menu.context.MenuContext;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MenuState<T> {
	private final @NotNull Map<MercuryPlayer, MenuStateInstance<T>> map = new HashMap<>();
	private final @NotNull T defaultValue;

	protected MenuState(@NotNull T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public @NotNull MenuStateInstance<T> get(@NotNull MercuryPlayer player) {
		return map.computeIfAbsent(player, p -> new MenuStateInstance<>(defaultValue));
	}

	public @NotNull MenuStateInstance<T> get(@NotNull MenuContext ctx) {
		return get(ctx.getPlayer());
	}

	public void clear(@NotNull MercuryPlayer player) {
		map.remove(player);
	}

	public void clear(@NotNull MenuContext ctx) {
		clear(ctx.getPlayer());
	}

	public void reset(@NotNull MercuryPlayer player) {
		get(player).set(defaultValue);
	}

	public void reset(@NotNull MenuContext ctx) {
		reset(ctx.getPlayer());
	}

	public static <T> @NotNull MenuState<T> create(@NotNull T defaultValue) {
		return new MenuState<>(defaultValue);
	}
}
