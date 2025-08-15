package me.adamix.mercury.core;

import me.adamix.mercury.core.player.PlayerManager;
import me.adamix.mercury.core.utils.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Basically helper class with lots of static methods.
 * <P>
 * Most of the methods are just calling the methods from managers or {@link MercuryCoreImpl}
 */
public class MercuryCore {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static MercuryCoreImpl implementation = null;

	private static @NotNull MercuryCoreImpl getImplementation() {
		if (implementation == null) {
			throw new IllegalStateException("MercuryCore is not properly loaded!");
		}
		return implementation;
	}

	@ApiStatus.Internal
	public static void load() {
		LOGGER.info("Loading MercuryCore");

		implementation = new MercuryCoreImpl();

		LOGGER.info("Successfully loaded MercuryCore");
	}

	/**
	 * Sends a message to the specified channel.
	 * All registered listeners for this channel will receive the message.
	 *
	 * @param channel channel id to send the message to
	 * @param message message to send
	 * @param args    arguments to send with the message
	 * @return boolean, if signal has been successfully sent, false otherwise.
	 */
	public static boolean sendSignal(@NotNull String channel, @NotNull String message, @NotNull Object... args) {
		return getImplementation().getSignalManager().sendSignal(channel, message, args);
	}




	public static @NotNull PlayerManager playerManager() {
		return getImplementation().getPlayerManager();
	}
}