package me.adamix.mercury.core.translation;

import me.adamix.mercury.core.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for managing translations.
 */
public class TranslationManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	// Map with the key as translation code, and value translation instance.
	private final @NotNull Map<String, Translation> translationMap = new HashMap<>();

	public void registerTranslation(@NotNull Translation translation) {
		translationMap.put(translation.getCode(), translation);
		LOGGER.info("Translation for {} has been registered", translation.getCode());
	}

	public @Nullable Translation getTranslation(@NotNull String code) {
		return translationMap.get(code);
	}
}
