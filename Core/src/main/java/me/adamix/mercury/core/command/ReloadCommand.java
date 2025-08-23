package me.adamix.mercury.core.command;

import me.adamix.mercury.configuration.api.exception.MissingPropertyException;
import me.adamix.mercury.configuration.api.exception.ParsingException;
import me.adamix.mercury.core.MercuryCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
		try {
			MercuryCore.reload();
		} catch (ParsingException | MissingPropertyException | IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
