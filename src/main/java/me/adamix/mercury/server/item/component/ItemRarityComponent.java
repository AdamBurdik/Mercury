package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.rarity.ItemRarity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;


public record ItemRarityComponent(ItemRarity rarity) implements MercuryItemComponent {
	@Override
	public String name() {
		return "itemRarityComponent";
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		ItemRarityComponent that = (ItemRarityComponent) object;
		return rarity == that.rarity;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(rarity);
	}

	@Override
	public Map<String, Object> serialize() {
		return Map.of("rarity", this.rarity.name());
	}

	public static @NotNull ItemRarityComponent deserialize(Map<String, Object> map) {
		return ItemRarity.valueOf((String) map.get("rarity")).toComponent();
	}
}
