package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.rarity.ItemRarity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public record ItemRarityComponent(ItemRarity rarity) implements MercuryItemComponent {
	@Override
	public String name() {
		return "itemRarityComponent";
	}

	@Override
	public Map<String, Object> serialize() {
		return Map.of("rarity", this.rarity.name());
	}

	public static @NotNull ItemRarityComponent deserialize(Map<String, Object> map) {
		return ItemRarity.valueOf((String) map.get("rarity")).toComponent();
	}
}
