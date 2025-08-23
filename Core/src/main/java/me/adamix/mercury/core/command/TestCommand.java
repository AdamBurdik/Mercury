package me.adamix.mercury.core.command;

import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
		Player bukkitPlayer = (Player) sender;

		MercuryPlayer player = MercuryPlayer.of(bukkitPlayer);

		var entity = MercuryCore.entityManager().builder()
				.name(args[0])
				.ai(false)
				.spawn(bukkitPlayer.getWorld(), bukkitPlayer.getLocation());

		entity.addViewer(player);

		return false;
	}
}
