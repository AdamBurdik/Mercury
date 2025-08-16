package me.adamix.mercury.core.placeholder;

import me.adamix.mercury.core.player.MercuryPlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;

public interface Placeholder {
	@NotNull String identifier();
	@NotNull String onRequest(@NotNull MercuryPlayer player, @NotNull ArgumentQueue args, @NotNull Map<String, Object> data);
}
