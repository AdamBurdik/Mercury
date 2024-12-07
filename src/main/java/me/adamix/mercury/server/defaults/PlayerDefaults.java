package me.adamix.mercury.server.defaults;

import lombok.Getter;
import me.adamix.mercury.server.toml.TomlConfiguration;
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
	@Getter private static int health = 100;
	@Getter private static int maxHealth = 100;
	@Getter private static float movementSpeed = 0.1f;
	@Getter private static float attackSpeed = 0.1f;


	public static void load(File file) {
		TomlConfiguration configuration = new TomlConfiguration(file);

		// Get translation id. If not present it won't throw error but use default value from above
		@Nullable String rawTranslationId = configuration.getString("translation_id");
		if (rawTranslationId != null) {
			translationId = rawTranslationId;
		}
		health = configuration.getInteger("health");
		maxHealth = configuration.getInteger("max_health");
		movementSpeed = configuration.getFloat("movement_speed");
		attackSpeed = configuration.getFloat("attack_speed");
	}
}
