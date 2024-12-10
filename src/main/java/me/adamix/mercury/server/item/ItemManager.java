package me.adamix.mercury.server.item;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.item.blueprint.MercuryItemBlueprint;
import net.minestom.server.utils.NamespaceID;

import java.util.Optional;
import java.util.UUID;


public class ItemManager {

	public Optional<MercuryItem> buildItem(NamespaceID blueprintID) {
		Optional<MercuryItemBlueprint> optionalBlueprint = Server.getItemBlueprintManager().get(blueprintID);
		if (optionalBlueprint.isEmpty()) {
			return Optional.empty();
		}

		MercuryItemBlueprint blueprint = optionalBlueprint.get();
		return Optional.of(blueprint.build(UUID.randomUUID()));
	}
}
