package me.adamix.mercury.core;

import com.comphenix.protocol.ProtocolManager;
import me.adamix.mercury.configuration.TomlConfiguration;
import me.adamix.mercury.configuration.api.MercuryConfiguration;
import me.adamix.mercury.configuration.api.exception.MissingPropertyException;
import me.adamix.mercury.configuration.api.exception.ParsingException;
import me.adamix.mercury.core.defaults.PlayerDefaults;
import me.adamix.mercury.core.entity.EntityManager;
import me.adamix.mercury.core.menu.MenuManager;
import me.adamix.mercury.core.placeholder.PlaceholderManager;
import me.adamix.mercury.core.placeholder.impl.PlayerPlaceholder;
import me.adamix.mercury.core.placeholder.impl.TranslationPlaceholder;
import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.player.PlayerManager;
import me.adamix.mercury.core.signal.SignalManager;
import me.adamix.mercury.core.translation.TranslationManager;
import me.adamix.mercury.core.utils.FileUtils;
import me.adamix.mercury.core.utils.LogUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

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
	public static boolean load(@NotNull MercuryCorePlugin plugin) {
		LOGGER.info("Loading MercuryCore");

		MercuryCore.plugin = plugin;

		try {
			Path configPath = Paths.get("").resolve("config/mercury.toml");
			implementation = new MercuryCoreImpl(configPath);

		} catch (Exception e) {
			LOGGER.error("Unexpected exception while loading main configuration file", e);
			return false;
		}

		try {
			reload();
		} catch (Exception e) {
			LOGGER.error("Unexpected exception while reloading MercuryCore", e);
			return false;
		}

		LOGGER.info("Successfully loaded MercuryCore");
		return true;
	}

	public static void reload() throws ParsingException, MissingPropertyException, IOException {
		getImplementation().reload();

		if (CoreFlags.REGISTER_DEFAULT_PLACEHOLDERS) {
			LOGGER.info("Registering default placeholders");
			PlaceholderManager placeholderManager = getImplementation().getPlaceholderManager();
			placeholderManager.registerPlaceholder(new PlayerPlaceholder());
			placeholderManager.registerPlaceholder(new TranslationPlaceholder());
		}

		PlayerDefaults.load();
	}

	public static @NotNull Collection<MercuryPlayer> onlinePlayers() {
		return Bukkit.getOnlinePlayers()
				.stream()
				.map(MercuryPlayer::of)
				.toList();
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

	public static @NotNull ProtocolManager protocolManager() {
		return getImplementation().getProtocolManager();
	}

	public static @NotNull EntityManager entityManager() {
		return getImplementation().getEntityManager();
	}

	public static @NotNull MenuManager menuManager() {
		return getImplementation().getMenuManager();
	}

	public static @NotNull MercuryCorePlugin plugin() {
		return plugin;
	}

	public static @NotNull MercuryConfiguration config() {
		return getImplementation().getMainConfig();
	}
}