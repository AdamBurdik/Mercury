package me.adamix.mercury.server.translation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Translation {
	private final Logger LOGGER = LoggerFactory.getLogger(Translation.class);
	private final String translationCode;
	private final Map<String, String> translationMap = new HashMap<>();

	public Translation(String translationCode, Set<Map.Entry<String, Object>> dottedEntrySet) {
		this.translationCode = translationCode;
		for (Map.Entry<String, Object> entry : dottedEntrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();

			translationMap.put(key, String.valueOf(value));
		}
	}

	public @NotNull String get(String dottedKey) {
		if (!translationMap.containsKey(dottedKey)) {
			LOGGER.error("Unable to get translation for key: {}:{}!", this.translationCode, dottedKey);
			return translationCode + ":" + dottedKey;
		}
		return translationMap.get(dottedKey);
	}

	public @NotNull Component getComponent(String dottedKey) {
		return Component.text(this.get(dottedKey));
	}
}
