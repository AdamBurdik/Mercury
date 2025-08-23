package me.adamix.mercury.core.command;

import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.menu.impl.TestMenu;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuCommand implements CommandExecutor {
	public MenuCommand() {
		MercuryCore.menuManager().register(new TestMenu());
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
		Player bukkitPlayer = (Player) sender;

		MercuryPlayer player = MercuryPlayer.of(bukkitPlayer);

		TestMenu menu = MercuryCore.menuManager().get(TestMenu.class).orElseThrow();
		menu.open(player);

		return false;
	}
}
