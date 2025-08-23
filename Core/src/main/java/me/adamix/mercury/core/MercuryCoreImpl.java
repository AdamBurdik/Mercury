package me.adamix.mercury.core;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Data;
import me.adamix.mercury.configuration.TomlConfiguration;
import me.adamix.mercury.configuration.api.MercuryConfiguration;
import me.adamix.mercury.configuration.api.exception.MissingPropertyException;
import me.adamix.mercury.configuration.api.exception.ParsingException;
import me.adamix.mercury.core.defaults.PlayerDefaults;
import me.adamix.mercury.core.entity.EntityManager;
import me.adamix.mercury.core.placeholder.PlaceholderManager;
import me.adamix.mercury.core.player.PlayerManager;
import me.adamix.mercury.core.signal.SignalManager;
import me.adamix.mercury.core.translation.TranslationManager;
import me.adamix.mercury.core.translation.TranslationParser;
import me.adamix.mercury.core.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class MercuryCoreImpl {
	private static final @NotNull Path rootPath = Path.of("");

	private final @NotNull Path mainConfigPath;
	private final @NotNull SignalManager signalManager;
	private final @NotNull PlayerManager playerManager;
	private final @NotNull TranslationManager translationManager;
	private final @NotNull PlaceholderManager placeholderManager;
	private final @NotNull EntityManager entityManager;

	private final ProtocolManager protocolManager;

	private @NotNull MercuryConfiguration mainConfig;

	public MercuryCoreImpl(@NotNull Path mainConfigPath) throws MissingPropertyException, IOException, ParsingException {
		this.signalManager = new SignalManager();
		this.playerManager = new PlayerManager();
		this.translationManager = new TranslationManager();
		this.placeholderManager = new PlaceholderManager();
		this.entityManager = new EntityManager();
		this.mainConfigPath = mainConfigPath;

		this.protocolManager = ProtocolLibrary.getProtocolManager();

		reload();
	}

	public void reload() throws IOException, ParsingException, MissingPropertyException {
		Path configPath = rootPath.resolve("config/mercury.toml");
		if (!Files.exists(configPath)) {
			Files.createDirectories(configPath.getParent());
			FileUtils.copyResourceIfAbsent("mercury.toml", "config/mercury.toml");
		}

		this.mainConfig = TomlConfiguration.of(configPath);

		TranslationParser.parseAll(Path.of("").resolve("config/translations")).forEach(translation -> {
			if (translation == null) {
				return;
			}
			translationManager.registerTranslation(translation);
		});
	}
}
