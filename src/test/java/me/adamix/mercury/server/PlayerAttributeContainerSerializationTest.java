package me.adamix.mercury.server;

import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.player.attribute.PlayerAttributeContainer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerAttributeContainerSerializationTest {

	@Test
	public void testPlayerAttributesSerialization() {
		PlayerAttributeContainer attributes = new PlayerAttributeContainer()
				.set(MercuryAttribute.DAMAGE, 69.)
				.set(MercuryAttribute.ATTACK_SPEED, 420.)
				.set(MercuryAttribute.MOVEMENT_SPEED, 999.);

		Map<String, Object> serializedAttributes = attributes.serialize();
		PlayerAttributeContainer deserializedAttributes = PlayerAttributeContainer.deserialize(serializedAttributes);

		assertEquals(attributes, deserializedAttributes);
	}
}
