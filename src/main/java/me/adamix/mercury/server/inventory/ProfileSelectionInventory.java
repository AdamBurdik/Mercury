package me.adamix.mercury.server.inventory;

import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.inventory.core.context.CloseContext;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.profile.ProfileData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.tag.Tag;

import java.util.List;
import java.util.UUID;

public class ProfileSelectionInventory extends MercuryInventory {
	private final List<ProfileData> profileDataList;

	public ProfileSelectionInventory(List<ProfileData> profileDataList) {
		this.profileDataList = profileDataList;
	}

	@Override
	public void onInit(InventoryConfig config) {
		config.rows(3);
		config.title("Profile Selection");
	}

	@Override
	public void onOpen(OpenContext ctx) {
		int i = 0;
		for (ProfileData profileData : profileDataList) {
			ItemStack itemStack = ItemStack.of(Material.GRASS_BLOCK)
					.withCustomName(
							Component.text(i)
					).withTag(
							Tag.UUID("profile_uuid"),
							profileData.getProfileUniqueId()
					);

			ctx.slot(i + 11, itemStack)
					.onClick((click) -> {
						click.setCancelled(true);

						UUID selectedProfileUniqueId = click.getItemStack().getTag(Tag.UUID("profile_uuid"));

						MercuryPlayer clickPlayer = click.getPlayer();

						clickPlayer.sendMessage(
								Component.text("You selected profile with id: " + selectedProfileUniqueId)
										.color(TextColor.color(97, 101, 165))
						);

						clickPlayer.loadProfileData(selectedProfileUniqueId);
						clickPlayer.removeEffect(PotionEffect.BLINDNESS);
						clickPlayer.sendToSpawn();
						clickPlayer.setNoGravity(false);
						click.close();
					});
			i++;
		}

		// ToDo Inventory is not opening because of this
//		ctx.slot(16, NamespaceID.from("mercury", "create_new_profile"))
//				.onClick((click) -> {
//					click.setCancelled(true);
//					click.close();
//					ProfileCreationInventory inventory = new ProfileCreationInventory();
//					click.getPlayer().openGameInventory(inventory);
//				});
	}

	@Override
	public void onClose(CloseContext ctx) {
		ctx.setCancelled(true);
	}
}
