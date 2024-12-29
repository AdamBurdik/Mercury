package me.adamix.mercury.server.player.attribute;

import me.adamix.mercury.server.defaults.PlayerDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerAttributes {
	private final EnumMap<PlayerAttribute, Double> attributeMap = new EnumMap<>(PlayerAttribute.class);

	public @NotNull PlayerAttributes setDefaults() {
		set(PlayerAttribute.DAMAGE, (double) PlayerDefaults.getDamage());
		set(PlayerAttribute.HEALTH, (double) PlayerDefaults.getHealth());
		set(PlayerAttribute.MAX_HEALTH, (double) PlayerDefaults.getMaxHealth());
		set(PlayerAttribute.ATTACK_SPEED, PlayerDefaults.getAttackSpeed());
		set(PlayerAttribute.MOVEMENT_SPEED, PlayerDefaults.getMovementSpeed());

		return this;
	}

	public @NotNull PlayerAttributes set(@NotNull PlayerAttribute attribute, @Nullable Double value) {
		if (value != null) {
			this.attributeMap.put(attribute, value);
		}
		return this;
	}

	public boolean has(@NotNull PlayerAttribute attribute) {
		return attributeMap.containsKey(attribute);
	}

	public double get(@NotNull PlayerAttribute attribute) {
		Double value = attributeMap.get(attribute);
		if (value != null) {
			return value;
		}
		throw new RuntimeException("Player attribute " + attribute.name() + " is not available!");
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		attributeMap.forEach((attribute, value) -> {
			map.put(attribute.name(), value);
		});

		return map;
	}

	public static PlayerAttributes deserialize(Map<String, Object> map) {
		PlayerAttributes attributes = new PlayerAttributes();

		map.forEach((attributeString, valueObject) -> {
			PlayerAttribute attribute = PlayerAttribute.valueOf(attributeString);
			if (valueObject instanceof Double doubleValue) {
				attributes.set(attribute, doubleValue);
			}
		});

		return attributes;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		PlayerAttributes that = (PlayerAttributes) object;
		return Objects.equals(attributeMap, that.attributeMap);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(attributeMap);
	}
}
