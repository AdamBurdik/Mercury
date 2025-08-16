package me.adamix.mercury.core.placeholder.impl;

import me.adamix.mercury.core.placeholder.Placeholder;
import me.adamix.mercury.core.player.MercuryPlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PlayerPlaceholder implements Placeholder {
	@Override
	public @NotNull String identifier() {
		return "player";
	}

	@Override
	public @NotNull String onRequest(@NotNull MercuryPlayer player, @NotNull ArgumentQueue args, @NotNull Map<String, Object> data) {
		if (!args.hasNext()) {
			return player.getName();
		}

		return switch (args.pop().lowerValue()) {
			case "name" -> player.getName();
			case "uuid" -> player.getUniqueId().toString();
			default -> throw new IllegalStateException("Unexpected value: " + args.pop());
		};
	}
}
