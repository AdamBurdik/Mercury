package me.adamix.mercury.server;

import me.adamix.mercury.server.attribute.AttributeContainer;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.ItemRarityComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import net.minestom.server.entity.attribute.AttributeOperation;
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

		AttributeContainer attributeContainer = new AttributeContainer();
		attributeContainer.set(MercuryAttribute.DAMAGE, 69d, AttributeOperation.ADD_VALUE);
		attributeContainer.set(MercuryAttribute.ATTACK_SPEED, 420d, AttributeOperation.ADD_VALUE);
		attributeContainer.set(MercuryAttribute.MOVEMENT_SPEED, -5d, AttributeOperation.ADD_VALUE);

		return new MercuryItem(
				UUID.randomUUID(),
				NamespaceID.from(blueprintIDs[random.nextInt(0, 2)]),
				names[random.nextInt(0, 2)],
				materials[random.nextInt(0, 2)],
				new MercuryItemComponent[]{
						new ItemRarityComponent(ItemRarity.LEGENDARY),
						new ItemDescriptionComponent(new String[]{"Line1", "Line2", "Line3"}),
						new ItemAttributeComponent(attributeContainer.getAttributeMap())
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
