package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.rarity.ItemRarity;

public record ItemRarityComponent(ItemRarity rarity) implements MercuryItemComponent {
}
