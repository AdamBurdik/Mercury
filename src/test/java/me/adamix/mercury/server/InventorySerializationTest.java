package me.adamix.mercury.server;

import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.attribute.ItemAttribute;
import me.adamix.mercury.server.item.attribute.ItemAttributes;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.ItemRarityComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventorySerializationTest {
	private static final String[] blueprintIDs = {"test_blueprint", "best_blueprint", "example_blueprint"};
	private static final String[] names = {"Example Item", "Test Item", "Really Good Item"};
	private static final Material[] materials = {Material.STONE, Material.DIAMOND, Material.DIAMOND_SWORD};


	private MercuryItem getRandomItem() {
		Random random = new Random();

		ItemAttributes itemAttributes = new ItemAttributes();
		itemAttributes.set(ItemAttribute.DAMAGE, 69f);
		itemAttributes.set(ItemAttribute.ATTACK_SPEED, 420f);
		itemAttributes.set(ItemAttribute.MOVEMENT_SPEED, -5f);

		return new MercuryItem(
				UUID.randomUUID(),
				NamespaceID.from(blueprintIDs[random.nextInt(0, 2)]),
				names[random.nextInt(0, 2)],
				materials[random.nextInt(0, 2)],
				new MercuryItemComponent[]{
						new ItemRarityComponent(ItemRarity.LEGENDARY),
						new ItemDescriptionComponent(new String[]{"Line1", "Line2", "Line3"}),
						itemAttributes.toComponent()
				}
		);
	}

	@Test
	public void testInventorySerialization() {
		MercuryPlayerInventory playerInventory = new MercuryPlayerInventory();

		for (int i = 0; i < 10; i++) {
			playerInventory.addItem(getRandomItem());
		}

		Map<String, Object> serializedInventory = playerInventory.serialize();
		MercuryPlayerInventory deserializedInventory = MercuryPlayerInventory.deserialize(serializedInventory);

		assertEquals(playerInventory, deserializedInventory);

	}
}
