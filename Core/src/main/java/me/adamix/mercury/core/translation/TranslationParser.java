package me.adamix.mercury.core.translation;

import me.adamix.mercury.configuration.TomlConfiguration;
import me.adamix.mercury.configuration.api.MercuryConfiguration;
import me.adamix.mercury.configuration.api.exception.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TranslationParser {
	public static @Nullable Translation parse(@NotNull Path path) throws ParsingException, IOException {
		MercuryConfiguration config = TomlConfiguration.of(path);

		String translationCode = config.get("code");
		String translationName = config.get("name");
		if (translationCode == null || translationName == null) {
			return null;
		}

		Map<String, String> translationMap = new HashMap<>();
		config.dottedEntrySet(false).forEach(entry -> {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (key.equals("code") || key.equals("name")) {
				return;
			}

			translationMap.put(key, String.valueOf(value));
		});

		return new Translation(translationCode, translationName, translationMap);
	}

	public static @NotNull List<Translation> parseAll(@NotNull Path path) throws IOException {
		List<Translation> translations = new ArrayList<>();

		Files.list(path)
				.forEach(child -> {
					try {
						translations.add(parse(child));
					} catch (ParsingException | IOException e) {
						throw new RuntimeException(e);
					}
				});
		return translations;
	}
}
