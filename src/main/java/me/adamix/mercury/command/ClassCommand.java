package me.adamix.mercury.command;

import me.adamix.mercury.managers.Managers;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

public class ClassCommand extends Command {
	public ClassCommand() {
		super("class", "limbo");

		setDefaultExecutor((sender, ctx) -> {

			if (sender instanceof GamePlayer player) {
				player.clearPlayerData();

				player.setNoGravity(true);
				player.setGameMode(GameMode.CREATIVE);
				player.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, Potion.INFINITE_DURATION));
				player.sendToLimbo();

				Managers.getInventoryManager().open("profile_selection", player);
			}
		});
	}
}
