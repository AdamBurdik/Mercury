package me.adamix.mercury.core.defaults;

import lombok.Getter;
import me.adamix.mercury.configuration.api.MercuryConfiguration;
import me.adamix.mercury.configuration.api.exception.MissingPropertyException;
import me.adamix.mercury.core.MercuryCore;
import org.jetbrains.annotations.NotNull;

public class PlayerDefaults {
	@Getter
	private static @NotNull String defaultTranslationCode = "en-US";

	public static void load() throws MissingPropertyException {
		MercuryConfiguration config = MercuryCore.config();

		defaultTranslationCode = config.getSafe("default_translation");
	}
}
