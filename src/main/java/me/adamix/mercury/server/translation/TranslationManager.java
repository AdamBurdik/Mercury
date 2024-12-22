package me.adamix.mercury.server.translation;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.flag.ServerFlag;
import me.adamix.mercury.server.player.MercuryPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TranslationManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranslationManager.class);
	private final Map<String, Translation> translationMap = new HashMap<>();

	public void clearTranslations() {
		translationMap.clear();
	}

	/**
	 * Load and save translation from file
	 * @param fileName Translation file name including .toml extension
	 */
	public void loadTranslation(String fileName) {
		Path source = Paths.get(ServerFlag.TRANSLATION_PATH + fileName);
		TomlParseResult result = null;
		try {
			result = Toml.parse(source);
		} catch (IOException e) {
			LOGGER.error("Error while parsing translation: {}\n{}\n", fileName, e.toString());
		}
		if (result == null) {
			return;
		}

		result.errors().forEach(error -> LOGGER.error("Error in translation: {}\n{}\n", fileName, error.toString()));

		String translationId = result.getString("translation.id");

		translationMap.put(
				translationId,
				new Translation(translationId, result.dottedEntrySet())
		);
		LOGGER.info("Loaded {} translation.", fileName);
	}

	/**
	 * Retrieves translation by translation id
	 * @param translationId Translation id. This is configured in each translation file.
	 * @return - Translation instance, or default translation if id translationId is null
	 */
	public @NotNull Translation getTranslation(@Nullable String translationId) {
		if (translationId == null) {
			translationId = PlayerDefaults.getTranslationId();
		}
		if (!translationMap.containsKey(translationId)) {
			Translation translation = translationMap.get("en");
			if (translation == null) {
				throw new RuntimeException("Cannot find default translation!");
			}
			return translation;
		}
		return translationMap.get(translationId);
	}

	public static @NotNull Translation getTranslation(MercuryPlayer player) {
		return Server.getTranslationManager().getTranslation(player.getTranslationId());
	}
}
