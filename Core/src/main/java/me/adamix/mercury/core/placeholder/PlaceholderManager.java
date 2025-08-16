package me.adamix.mercury.core.placeholder;


import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.utils.LogUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, Placeholder> placeholderMap = new HashMap<>();

	public void registerPlaceholder(Placeholder placeholder) {
		LOGGER.info("Registered '{}' placeholder", placeholder.identifier());
		placeholderMap.put(placeholder.identifier(), placeholder);
	}

	private TagResolver getTagResolver(@NotNull MercuryPlayer player, @NotNull Map<String, Object> data) {
		TagResolver.Builder builder = TagResolver.builder();

		placeholderMap.forEach((identifier, placeholder) -> {
			builder.tag(identifier, (args, ctx) -> {
				try {
					return Tag.preProcessParsed(placeholder.onRequest(player, args, data));
				} catch (RuntimeException e) {
					LOGGER.error("Error while parsing placeholder {}: {}", identifier, e.getMessage());
				}
				return Tag.preProcessParsed(identifier + ":" + args.toString());
			});
		});

		return builder.build();
	}

	public Component parse(@NotNull String text, @NotNull MercuryPlayer player, @NotNull Map<String, Object> data) {
		return MiniMessage.miniMessage().deserialize(text, getTagResolver(player, data));
	}

	public Component parse(@NotNull String text, @NotNull MercuryPlayer player) {
		return MiniMessage.miniMessage().deserialize(text, getTagResolver(player, Map.of()));
	}
}

