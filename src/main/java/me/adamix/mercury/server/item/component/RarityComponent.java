package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.rarity.ItemRarity;

public record RarityComponent(ItemRarity rarity) implements MercuryItemComponent {
}
