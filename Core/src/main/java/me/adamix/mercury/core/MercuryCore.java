package me.adamix.mercury.core;

import me.adamix.mercury.core.placeholder.PlaceholderManager;
import me.adamix.mercury.core.placeholder.impl.PlayerPlaceholder;
import me.adamix.mercury.core.placeholder.impl.TranslationPlaceholder;
import me.adamix.mercury.core.player.PlayerManager;
import me.adamix.mercury.core.signal.SignalManager;
import me.adamix.mercury.core.translation.TranslationManager;
import me.adamix.mercury.core.utils.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Basically helper class with lots of static methods.
 * <p>
 * Most of the methods are just calling the methods from managers or {@link MercuryCoreImpl}
 */
public class MercuryCore {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static MercuryCoreImpl implementation = null;
	private static MercuryCorePlugin plugin;

	/**
	 * Sends a message to the specified channel.
	 * <p>
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




	private static @NotNull MercuryCoreImpl getImplementation() {
		if (implementation == null) {
			throw new IllegalStateException("MercuryCore is not properly loaded!");
		}
		return implementation;
	}

	@ApiStatus.Internal
	public static void load(@NotNull MercuryCorePlugin plugin) {
		LOGGER.info("Loading MercuryCore");

		MercuryCore.plugin = plugin;

		implementation = new MercuryCoreImpl();

		if (CoreFlags.REGISTER_DEFAULT_PLACEHOLDERS) {
			LOGGER.info("Registering default placeholders");
			PlaceholderManager placeholderManager = getImplementation().getPlaceholderManager();
			placeholderManager.registerPlaceholder(new PlayerPlaceholder());
			placeholderManager.registerPlaceholder(new TranslationPlaceholder());
		}

		LOGGER.info("Successfully loaded MercuryCore");
	}

	public static @NotNull PlayerManager playerManager() {
		return getImplementation().getPlayerManager();
	}

	public static @NotNull SignalManager signalManager() {
		return getImplementation().getSignalManager();
	}

	public static @NotNull TranslationManager translationManager() {
		return getImplementation().getTranslationManager();
	}

	public static @NotNull PlaceholderManager placeholderManager() {
		return getImplementation().getPlaceholderManager();
	}
}