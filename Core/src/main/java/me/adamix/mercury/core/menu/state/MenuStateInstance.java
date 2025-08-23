package me.adamix.mercury.core.menu.state;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class MenuStateInstance<T> {
	private @NotNull T value;

	public MenuStateInstance(@NotNull T value) {
		this.value = value;
	}

	public void set(@NotNull T value) {
		this.value = value;
	}

	public @NotNull T value() {
		return this.value;
	}

	public void update(@NotNull Function<T, T> function) {
		this.value = function.apply(this.value);
	}
}
