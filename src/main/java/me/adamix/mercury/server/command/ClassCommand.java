package me.adamix.mercury.server.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.ProfileSelectionInventory;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

public class ClassCommand extends Command {
	public ClassCommand() {
		super("class", "limbo");

		setDefaultExecutor((sender, ctx) -> {

			if (sender instanceof MercuryPlayer player) {
				player.clearProfileData();

				player.setNoGravity(true);
				player.setGameMode(GameMode.CREATIVE);
				player.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, Potion.INFINITE_DURATION));
				player.sendToLimbo();

				Server.getProfileDataManager().getProfileDataListSync(player.getUuid(), (playerDataList -> {
					ProfileSelectionInventory inventory = new ProfileSelectionInventory(playerDataList);
					player.openGameInventory(inventory);
				}));
			}
		});
	}
}
