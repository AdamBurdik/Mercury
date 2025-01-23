package me.adamix.mercury.server;

import me.adamix.mercury.server.attribute.AttributeContainer;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.ItemRarityComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemSerializationTest {

	@Test
	public void testItemSerialization() {
		AttributeContainer attributeContainer = new AttributeContainer();
		attributeContainer.set(MercuryAttribute.DAMAGE, 69d, AttributeOperation.ADD_VALUE);
		attributeContainer.set(MercuryAttribute.ATTACK_SPEED, 420d, AttributeOperation.ADD_VALUE);
		attributeContainer.set(MercuryAttribute.MOVEMENT_SPEED, -5d, AttributeOperation.ADD_VALUE);

		MercuryItem mercuryItem = new MercuryItem(
				UUID.randomUUID(),
				NamespaceID.from("mercury", "test_blueprint"),
				"TestItem",
				Material.STONE,
				new MercuryItemComponent[]{
						new ItemRarityComponent(ItemRarity.LEGENDARY),
						new ItemDescriptionComponent(new String[]{"Line1", "Line2", "Line3"}),
						new ItemAttributeComponent(attributeContainer.getAttributeMap())
				}
		);

		Map<String, Object> serializedItem = mercuryItem.serialize();
		MercuryItem deserializedItem = MercuryItem.deserialize(serializedItem);

		assertEquals(mercuryItem, deserializedItem);

	}
}
