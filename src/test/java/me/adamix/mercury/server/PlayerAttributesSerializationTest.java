package me.adamix.mercury.server;

import me.adamix.mercury.server.player.attribute.PlayerAttribute;
import me.adamix.mercury.server.player.attribute.PlayerAttributes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerAttributesSerializationTest {

	@Test
	public void testPlayerAttributesSerialization() {
		PlayerAttributes attributes = new PlayerAttributes()
				.set(PlayerAttribute.DAMAGE, 69.)
				.set(PlayerAttribute.ATTACK_SPEED, 420.)
				.set(PlayerAttribute.MOVEMENT_SPEED, 999.);

		Map<String, Object> serializedAttributes = attributes.serialize();
		PlayerAttributes deserializedAttributes = PlayerAttributes.deserialize(serializedAttributes);

		assertEquals(attributes, deserializedAttributes);
	}
}
