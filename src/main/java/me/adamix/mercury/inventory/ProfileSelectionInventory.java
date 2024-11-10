package me.adamix.mercury.inventory;

import me.adamix.mercury.Server;
import me.adamix.mercury.inventory.core.GameInventory;
import me.adamix.mercury.inventory.core.context.CloseContext;
import me.adamix.mercury.inventory.core.context.InventoryConfig;
import me.adamix.mercury.inventory.core.context.OpenContext;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.tag.Tag;

import java.util.List;
import java.util.UUID;

public class ProfileSelectionInventory extends GameInventory {

	@Override
	public void onInit(InventoryConfig config) {
		config.rows(3);
		config.title("Profile Selection");
	}

	@Override
	public void onOpen(OpenContext ctx) {
		GamePlayer player = ctx.getPlayer();

		List<PlayerData> playerDataList = Server.getPlayerDataManager().getPlayerDataList(player.getUuid());
		int i = 0;
		for (PlayerData playerData : playerDataList) {
			ItemStack itemStack = ItemStack.of(Material.GRASS_BLOCK)
					.withCustomName(
							Component.text(i)
					).withTag(
							Tag.UUID("profile_uuid"),
							playerData.getProfileUniqueId()
					);

			ctx.slot(i + 11, itemStack)
					.onClick((click) -> {
						click.setCancelled(true);

						UUID selectedProfileUniqueId = click.getItemStack().getTag(Tag.UUID("profile_uuid"));

						GamePlayer clickPlayer = click.getPlayer();

						clickPlayer.sendMessage(
								Component.text("You selected profile with id: " + selectedProfileUniqueId)
										.color(TextColor.color(97, 101, 165))
						);

						clickPlayer.loadPlayerData(selectedProfileUniqueId);
						clickPlayer.removeEffect(PotionEffect.BLINDNESS);
						clickPlayer.sendToSpawn();
						clickPlayer.setNoGravity(false);
						click.close();
					});

			i++;
		}
	}

	@Override
	public void onClose(CloseContext ctx) {
		ctx.setCancelled(true);
	}
}
