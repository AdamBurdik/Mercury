package me.adamix.mercury.core.signal;

import me.adamix.mercury.core.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for managing signals.
 * <p>
 * Signal is a message sent between plugins that uses MercuryCore.
 * <p>
 * The Signal contains three parts:
 * <ul>
 *   <li><b>Channel:</b> a broad category, e.g., "spells"</li>
 *   <li><b>Message:</b> the specific action, e.g., "cast"</li>
 *   <li><b>Arguments:</b> an array of objects sent with the message, e.g., ["fireball"]</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * Channel: "spells"
 * Message: "cast"
 * Arguments: ["fireball"]
 * </pre>
 */
public class SignalManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final @NotNull Map<String, Set<SignalListener>> listenerMap = new HashMap<>();

	/**
	 * Sends a message to the specified channel.
	 * All registered listeners for this channel will receive the message.
	 *
	 * @param channel channel id to send the message to
	 * @param message message to send
	 * @param args    arguments to send with the message
	 * @return boolean, if signal has been successfully sent, false otherwise.
	 */
	public boolean sendSignal(@NotNull String channel, @NotNull String message, @NotNull Object... args) {
		Set<SignalListener> channelListeners = listenerMap.get(channel);
		if (channelListeners == null) {
			return false;
		}

		boolean success = false;
		for (SignalListener channelListener : channelListeners) {
			try {
				channelListener.onSignal(message, args);
				// If a listener has been invoked without throwing exception, we can set success to true,
				// because at least one listener has succeeded
				success = true;
			} catch (Exception e) {
				LOGGER.error("Unexpected exception while invoking signal listener for '{}' channel", channel, e);
			}
		}

		return success;
	}

	/**
	 * Registers listener for the specified channel.
	 * @param channel channel to register listener for.
	 * @param listener listener to register.
	 */
	public void registerListener(@NotNull String channel, @NotNull SignalListener listener) {
		Set<SignalListener> chanelListeners = listenerMap.computeIfAbsent(channel, key -> new HashSet<>());
		chanelListeners.add(listener);
	}
}
