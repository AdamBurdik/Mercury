package me.adamix.mercury.item.core;

import lombok.Getter;
import me.adamix.mercury.Server;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.item.core.attribute.ItemAttribute;
import me.adamix.mercury.item.core.attribute.ItemAttributeValue;
import me.adamix.mercury.item.core.attribute.ItemAttributes;
import me.adamix.mercury.item.core.rarity.ItemRarity;
import me.adamix.mercury.placeholder.PlaceholderManager;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.translation.Translation;
import me.adamix.mercury.translation.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents instance of game item created from a blueprint.
 * Contains item-specific properties and methods for item stack generation.
 */
@Getter
public class GameItem {
	private final @NotNull UUID itemUniqueId;
	private final @NotNull NamespaceID blueprintID;
	private final @NotNull Material baseMaterial;
	private final @NotNull String name;
	private final @Nullable String description;
	private final @NotNull ItemAttributes attributes;
	private final @Nullable ItemRarity rarity;
	private static final Tag<UUID> idTag = Tag.UUID("uniqueId");

	public GameItem(
			@NotNull UUID itemUniqueId,
			@NotNull NamespaceID blueprintID,
			@NotNull Material baseMaterial,
			@NotNull String name,
			@Nullable String description,
			@NotNull ItemAttributes attributes,
			@Nullable ItemRarity rarity
	) {
		this.itemUniqueId = itemUniqueId;
		this.blueprintID = blueprintID;
		this.baseMaterial = baseMaterial;
		this.name = name;
		this.description = description;
		this.attributes = attributes;
		this.rarity = rarity;
	}

	/**
	 * Create item stack from fields
	 * @param player - player to create item for
	 * @return ItemStack
	 */
	public ItemStack toItemStack(GamePlayer player) {
		PlaceholderManager placeholder = Server.getPlaceholderManager();
		Translation translation = TranslationManager.getTranslation(player);

		// Create lore
		List<Component> lore = new ArrayList<>();

		// Add description if present to lore
		if (this.description != null) {
			lore.add(placeholder.parse(this.description, player));
			lore.add(Component.empty());
		}

		// Add attribute list to lore
		EnumMap<ItemAttribute, ItemAttributeValue> attributeMap = this.attributes.getAttributeMap();
		if (!attributeMap.isEmpty()) {
			attributeMap.forEach((attribute, value) -> {
				Component namePart = Component.text(translation.get(attribute.translationKey()) + ": ")
						.color(ColorPallet.LIGHT_GRAY.getColor());

				boolean isPositive = value.value() >= 0;
				String sign = isPositive ? "+" : "";

				Component valuePart = getComponent(value, sign, isPositive);

				lore.add(
						namePart.append(valuePart)
								.decoration(TextDecoration.ITALIC, false)
				);

			});

			lore.add(Component.empty());
		}

		// Add rarity to lore
		if (this.rarity != null) {
			lore.add(
					Component.text(translation.get(rarity.translationKey()).toUpperCase())
							.color(TextColor.color(129, 21, 13))
							.decoration(TextDecoration.ITALIC, false)
							.decoration(TextDecoration.BOLD, true)
			);
		}

		// Add item unique id and blueprint id if player is in debug
		if (player.isInDebug()) {
			lore.add(Component.empty());
			lore.add(
					Component.text("Id: " + this.itemUniqueId)
							.color(ColorPallet.DARK_GRAY.getColor())
			);
			lore.add(
					Component.text("Blueprint Id: " + this.blueprintID.asString())
							.color(ColorPallet.DARK_GRAY.getColor())
			);
		}

		// Create and apply all values to item stack
		return ItemStack.of(this.baseMaterial)
				.withCustomName(
						placeholder.parse(this.name, player)
				)
				.withLore(
						lore
				)
				.withoutExtraTooltip()
				.withTag(idTag, this.itemUniqueId);
	}

	private @NotNull Component getComponent(ItemAttributeValue value, String sign, boolean isPositive) {
		AttributeOperation operation = value.operation();
		Component valuePart = switch (operation) {
			case ADD_VALUE -> Component.text(sign + (int) value.value());
			case MULTIPLY_BASE -> Component.text(sign + (int) (value.value() * 100) + "%");
			case MULTIPLY_TOTAL -> Component.text((int) (value.value() * 100) + "%");
		};

		if (isPositive) {
			valuePart = valuePart.color(ColorPallet.POSITIVE_GREEN.getColor());
		} else {
			valuePart = valuePart.color(ColorPallet.NEGATIVE_RED.getColor());
		}
		return valuePart;
	}
}
