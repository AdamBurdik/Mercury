package me.adamix.mercury.core;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.*;

@Slf4j
public class MercuryCorePlugin extends JavaPlugin {
	private @NotNull ComponentLogger componentLogger;
	private @NotNull String version;

	@Override
	public void onEnable() {
		componentLogger = getComponentLogger();

		try {
			version = loadVersion();
		} catch (Exception e) {
			componentLogger.error("Unexpected exception while loading MercuryCore Plugin version from plugin.yml", e);
			disable();
			return;
		}

		// Enabling logic
		MercuryCore.load(this);

		componentLogger.info("MercuryCore Plugin {} has been enabled", version);
	}

	@Override
	public void onDisable() {
		// Disabling Logic

		componentLogger.info("Disabled MercuryCore Plugin {}", version);
	}

	@ApiStatus.Internal
	public void disable() {
		getComponentLogger().info("Disabling MercuryCore Plugin");
		Bukkit.getPluginManager().disablePlugin(this);
	}

	private static @NotNull String loadVersion() throws IOException {
		try (InputStream inputStream = MercuryCorePlugin.class.getClassLoader().getResourceAsStream("plugin.yml")) {
			if (inputStream == null) {
				throw new FileNotFoundException("Resource plugin.yml was not found");
			}
			Reader reader = new InputStreamReader(inputStream);

			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(reader);
			String version = yaml.getString("version");
			if (version == null) {
				throw new IllegalStateException("Missing version property in plugin.yml");
			}

			return version;
		}
	}
}
