package me.adamix.mercury.core.translation;

import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;


public class Translation {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final @NotNull String translationCode; // Unique code for each translation, e.g. cs-CZ
	private final @NotNull String translationName; // Name of translation, e.g. "Čeština"
	private final @NotNull Map<String, String> translationMap;

	public Translation(
			@NotNull String translationCode,
			@NotNull String translationName,
			@NotNull Map<String, String> translationMap
	) {
		this.translationCode = translationCode;
		this.translationName = translationName;
		this.translationMap = translationMap;
	}

	public @Nullable String getTranslated(@NotNull String dottedKey) {
		return translationMap.get(dottedKey);
	}

	public @NotNull String getCode() {
		return translationCode;
	}

	public @NotNull String getName() {
		return translationName;
	}


	public static @NotNull Translation of(@NotNull MercuryPlayer player) {
		Translation translation = MercuryCore.translationManager().getTranslation(player.getTranslationCode());
		if (translation == null) {
			LOGGER.error("Unable to get translation for code '{}'", player.getTranslationCode());
			throw new IllegalStateException("Unable to get translation for code '%s'".formatted(player.getTranslationCode()));
		}
		return translation;
	}
}
