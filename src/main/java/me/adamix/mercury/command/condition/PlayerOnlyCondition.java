package me.adamix.mercury.command.condition;

import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerOnlyCondition implements CommandCondition {
	@Override
	public boolean canUse(@NotNull CommandSender sender, @Nullable String commandString) {
		return sender instanceof GamePlayer;
	}
}
