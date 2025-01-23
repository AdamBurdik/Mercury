package me.adamix.mercury.server.defaults;

import lombok.Getter;
import me.adamix.mercury.server.toml.MercuryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Represents default values for player
 * <br>
 * Values are loaded from config
 */
public class PlayerDefaults {
	@Getter private static @NotNull String translationId = "en";
	@Getter private static int damage = 1;
	@Getter private static long health = 100;
	@Getter private static int maxHealth = 100;
	@Getter private static double movementSpeed = 0.1f;
	@Getter private static double attackSpeed = 0.1f;

	public static void load(File file) {
		MercuryConfiguration configuration = new MercuryConfiguration(file);

		// Get translation id. If not present it won't throw error but use default value from above
		@Nullable String rawTranslationId = configuration.getString("translation_id");
		if (rawTranslationId != null) {
			translationId = rawTranslationId;
		}
		damage = configuration.getIntegerSafe("damage");
		health = configuration.getLongSafe("health");
		maxHealth = configuration.getIntegerSafe("max_health");
		movementSpeed = configuration.getDoubleSafe("movement_speed");
		attackSpeed = configuration.getDoubleSafe("attack_speed");
	}
}
