package me.adamix.mercury.server.inventory;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.party.MercuryParty;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.skin.PlayerSkinProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

import java.util.Set;
import java.util.UUID;

public class PartyInventory extends MercuryInventory {
	private final MercuryParty party;

	public PartyInventory(MercuryParty party) {
		this.party = party;
	}

	@Override
	public void onInit(InventoryConfig config) {
		config.title(party.getName());
		config.rows(6);
	}

	@Override
	public void onOpen(OpenContext ctx) {
		ctx.slot(
				13,
				ItemStack.of(Material.PLAYER_HEAD)
						.withCustomName(
								Component.text(party.getName())
						)
						.withLore(
								Component.text(party.getDescription() != null ? party.getDescription() : "no description provided")
						)
		).cancelOnClick();

		Set<UUID> adminSet = party.getAdminSet();

		int i = 0;
		for (UUID memberUniqueId : party.getAllMembers()) {
			MercuryPlayer player = Server.getOnlinePlayerByUniqueId(memberUniqueId);
			if (player == null) {
				continue;
			}

			Component description;
			if (adminSet.contains(memberUniqueId)) {
				description = Component.text("admin").color(ColorPallet.NEGATIVE_RED.getColor());
			} else if (party.getLeaderUniqueId().equals(memberUniqueId)) {
				description = Component.text("leader").color(ColorPallet.YELLOW.getColor());
			} else {
				description = Component.text("member").color(ColorPallet.LIGHT_GRAY.getColor());
			}

			PlayerSkin playerSkin = PlayerSkinProvider.getByUniqueId(memberUniqueId);
			HeadProfile headProfile = HeadProfile.EMPTY;
			if (playerSkin != null) {
				headProfile = new HeadProfile(playerSkin);
			}

			ctx.slot(
					19 + i,
					ItemStack.of(Material.PLAYER_HEAD)
							.withCustomName(
									Component.text(player.getUsername())
							)
							.withLore(description)
							.with(ItemComponent.PROFILE, headProfile)
			).cancelOnClick();

			i++;
		}
	}
}
