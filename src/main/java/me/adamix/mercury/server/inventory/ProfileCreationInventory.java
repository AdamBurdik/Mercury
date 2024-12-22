package me.adamix.mercury.server.inventory;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.inventory.core.context.CloseContext;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import me.adamix.mercury.server.player.profile.quest.ProfileQuests;
import me.adamix.mercury.server.player.stats.Statistics;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;
import java.util.UUID;

public class ProfileCreationInventory extends MercuryInventory {

	@Override
	public void onInit(InventoryConfig config) {
		config.rows(4);
		config.title("Profile Creation");
	}

	@Override
	public void onOpen(OpenContext ctx) {
		ctx.slot(4 * 9 - 1, NamespaceID.from("mercury:confirm_profile_creation"))
				.onClick((click) -> {
					MercuryPlayer clickPlayer = click.getPlayer();

					click.setCancelled(true);
					ProfileDataManager profileDataManager = Server.getProfileDataManager();
					profileDataManager.saveProfileData(
							new ProfileData(
									clickPlayer.getUuid(),
									UUID.randomUUID(),
									PlayerDefaults.getTranslationId(),
									PlayerDefaults.getHealth(),
									PlayerDefaults.getMaxHealth(),
									PlayerDefaults.getMovementSpeed(),
									PlayerDefaults.getAttackSpeed(),
									new MercuryPlayerInventory(),
									new Statistics(),
									new ProfileQuests(null, new ArrayList<>())
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
