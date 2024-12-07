package me.adamix.mercury.server.placeholder;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PlaceholderManager {

	private static PlaceholderManager instance;
	private final MiniMessage miniMessage;
	private final Map<String, BiFunction<ArgumentQueue, GamePlayer, String>> playerPlaceholderMap = new HashMap<>();

	public PlaceholderManager() {
		this.miniMessage = MiniMessage.miniMessage();

		registerPlayer("player_health", (args, player) -> {
			return String.valueOf(player.getHealth());
		});
		registerPlayer("player_name", (args, player) -> {
			return player.getUsername();
		});
		registerPlayer("player_max_health", (args, player) -> {
			return String.valueOf(player.getMaxHealth());
		});
		registerPlayer("translation", (args, player) -> {
			if (!args.hasNext()) {
				return "Invalid Key";
			}

		 	Tag.Argument argument = args.pop();
			String key = argument.lowerValue();

			Translation translation = Server.getTranslationManager().getTranslation(player.getTranslationId());
			return translation.get(key);
		});
	}

	public void registerPlayer(@NotNull String name, BiFunction<ArgumentQueue, GamePlayer, String> function) {
		playerPlaceholderMap.put(name, function);
	}

	private TagResolver getTagResolver(GamePlayer player) {
		TagResolver.Builder builder = TagResolver.builder();

		playerPlaceholderMap.forEach((name, function) -> {
			builder.tag(name, (args, context) -> Tag.preProcessParsed(function.apply(args, player)));
		});

		return builder.build();
	}

	public Component parse(String text, GamePlayer player) {
		return miniMessage.deserialize(text, getTagResolver(player));
	}


	public static PlaceholderManager get() {
		return instance != null ? instance : new PlaceholderManager();
	}

}
