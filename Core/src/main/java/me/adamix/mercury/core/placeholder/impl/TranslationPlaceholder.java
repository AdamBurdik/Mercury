package me.adamix.mercury.core.placeholder.impl;

import me.adamix.mercury.core.placeholder.Placeholder;
import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.translation.Translation;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TranslationPlaceholder implements Placeholder {
	@Override
	public @NotNull String identifier() {
		return "translation";
	}

	@Override
	public @NotNull String onRequest(@NotNull MercuryPlayer player, @NotNull ArgumentQueue args, @NotNull Map<String, Object> data) {
		if (!args.hasNext()) {
			return "<red>No translation key provided!";
		}

		String translationKey = args.pop().toString();
		try {
			Translation translation = Translation.of(player);

			String message = translation.getTranslated(translationKey);
			if (message == null) {
				return "<red>No message found for '%s'".formatted(translationKey);
			}
			return message;
		} catch (IllegalStateException e) {
			return "<red>No translation found for '%s'".formatted(player.getTranslationCode());
		}
	}
}
