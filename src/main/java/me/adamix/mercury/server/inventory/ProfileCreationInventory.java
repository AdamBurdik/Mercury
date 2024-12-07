package me.adamix.mercury.server.inventory;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.core.GameInventory;
import me.adamix.mercury.server.inventory.core.context.CloseContext;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import net.minestom.server.utils.NamespaceID;

import java.util.UUID;

public class ProfileCreationInventory extends GameInventory {

	@Override
	public void onInit(InventoryConfig config) {
		config.rows(4);
		config.title("Profile Creation");
	}

	@Override
	public void onOpen(OpenContext ctx) {
		ctx.slot(4 * 9 - 1, NamespaceID.from("mercury:confirm_profile_creation"))
				.onClick((click) -> {
					GamePlayer clickPlayer = click.getPlayer();

					click.setCancelled(true);
					ProfileDataManager profileDataManager = Server.getProfileDataManager();
					// ToDO Create new player data with default values from config
					profileDataManager.saveProfileData(
							new ProfileData(
									clickPlayer.getUuid(),
									UUID.randomUUID(),
									"en",
									100,
									100,
									0.1f,
									0.1f,
									new GamePlayerInventory()
							)
					);
					click.close();

					Server.getProfileDataManager().getProfileDataListSync(clickPlayer.getUuid(), (profileDataList -> {
						ProfileSelectionInventory inventory = new ProfileSelectionInventory(profileDataList);
						clickPlayer.openGameInventory(inventory);
					}));

				});
	}

	@Override
	public void onClose(CloseContext ctx) {
		ctx.setCancelled(true);
	}
}
