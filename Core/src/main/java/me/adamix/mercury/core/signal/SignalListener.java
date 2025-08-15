package me.adamix.mercury.core.signal;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SignalListener {

	/**
	 * This function will be called when signal is sent to the same channel.
	 *
	 * @param message the message that has been sent.
	 * @param args the argument array that has been sent along.
	 */
	void onSignal(@NotNull String message, @NotNull Object... args);
}
