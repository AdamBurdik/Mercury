package me.adamix.mercury.core;

import org.jetbrains.annotations.NotNull;

public class CoreFlags {
	public static final boolean REGISTER_DEFAULT_PLACEHOLDERS = getBoolean("registerDefaultPlaceholders", true);

	private static boolean getBoolean(@NotNull String key, boolean def) {
		return Boolean.parseBoolean(System.getProperty(key, Boolean.toString(def)));
	}
}
