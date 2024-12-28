package me.adamix.mercury.server;

import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.attribute.ItemAttribute;
import me.adamix.mercury.server.item.attribute.ItemAttributes;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.ItemRarityComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemSerializationTest {

	@Test
	public void testItemSerialization() {
		ItemAttributes itemAttributes = new ItemAttributes();
		itemAttributes.set(ItemAttribute.DAMAGE, 69f);
		itemAttributes.set(ItemAttribute.ATTACK_SPEED, 420f);
		itemAttributes.set(ItemAttribute.MOVEMENT_SPEED, -5f);

		MercuryItem mercuryItem = new MercuryItem(
				UUID.randomUUID(),
				NamespaceID.from("mercury", "test_blueprint"),
				"TestItem",
				Material.STONE,
				new MercuryItemComponent[]{
						new ItemRarityComponent(ItemRarity.LEGENDARY),
						new ItemDescriptionComponent(new String[]{"Line1", "Line2", "Line3"}),
						itemAttributes.toComponent()
				}
		);

		Map<String, Object> serializedItem = mercuryItem.serialize();
		MercuryItem deserializedItem = MercuryItem.deserialize(serializedItem);

		assertEquals(mercuryItem, deserializedItem);

	}
}
