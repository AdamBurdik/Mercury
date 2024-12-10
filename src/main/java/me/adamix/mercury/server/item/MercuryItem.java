package me.adamix.mercury.server.item;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.item.attribute.ItemAttributeValue;
import me.adamix.mercury.server.item.component.AttributeComponent;
import me.adamix.mercury.server.item.component.DescriptionComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.component.RarityComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import me.adamix.mercury.server.placeholder.PlaceholderManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.translation.Translation;
import me.adamix.mercury.server.translation.TranslationManager;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record MercuryItem(
		@NotNull UUID uniqueId,
		@NotNull NamespaceID blueprintID,
		@NotNull String name,
		@NotNull Material material,
		@NotNull MercuryItemComponent[] components
) {
	public MercuryItem(
			@NotNull UUID uniqueId,
			@NotNull NamespaceID blueprintID,
			@NotNull String name,
			@NotNull Material material,
			@NotNull MercuryItemComponent[] components
	) {
		this.uniqueId = uniqueId;
		this.blueprintID = blueprintID;
		this.name = name;
		this.material = material;
		this.components = components;
	}

	private static final Tag<UUID> uniqueIdTag = Tag.UUID("uniqueId");

	public boolean hasComponent(Class<? extends MercuryItemComponent> clazz) {
		return getComponent(clazz) != null;
	}

	public <T extends MercuryItemComponent> @Nullable T getComponent(Class<T> clazz) {
		for (@NotNull MercuryItemComponent itemComponent : components) {
			if (itemComponent.getClass().equals(clazz)) {
				if (clazz.isInstance(itemComponent)) {
					return clazz.cast(itemComponent);
				}
			}
		}
		return null;
	}

	public @NotNull ItemStack toItemStack(MercuryPlayer player) {
		PlaceholderManager placeholderManager = Server.getPlaceholderManager();
		Translation translation = TranslationManager.getTranslation(player);

		// Create lore list
		List<Component> loreList = new ArrayList<>();

		// Add description to lore if available
		DescriptionComponent descriptionComponent = getComponent(DescriptionComponent.class);
		if (descriptionComponent != null) {
			for (String line : descriptionComponent.lines()) {
				Component lineComponent = placeholderManager.parse(line, player);
				loreList.add(lineComponent);
			}
		}

		// Add attributes to lore if available
		AttributeComponent attributeComponent = getComponent(AttributeComponent.class);
		if (attributeComponent != null) {
			loreList.add(Component.empty());
			attributeComponent.attributeMap().forEach((attribute, value) -> {
				Component namePart = Component.text(translation.get(attribute.translationKey()) + ": ")
						.color(ColorPallet.LIGHT_GRAY.getColor());

				boolean isPositive = value.value() >= 0;
				String sign = isPositive ? "+" : "";

				Component valuePart = formatAttribute(value, sign, isPositive);

				loreList.add(
						namePart.append(valuePart)
								.decoration(TextDecoration.ITALIC, false)
				);
			});
		}

		// Add rarity to lore if available
		RarityComponent rarityComponent = getComponent(RarityComponent.class);
		if (rarityComponent != null) {
			ItemRarity rarity = rarityComponent.rarity();
			loreList.add(Component.empty());
			loreList.add(
					Component.text(translation.get(rarity.translationKey()).toUpperCase())
							.color(TextColor.color(129, 21, 13))
							.decoration(TextDecoration.ITALIC, false)
							.decoration(TextDecoration.BOLD, true)
			);
		}

		// Add debug to lore if player is in debug
		if (player.isInDebug()) {
			loreList.add(Component.empty());
			loreList.add(
					Component.text("uniqueId: " + this.uniqueId)
							.color(ColorPallet.DARK_GRAY.getColor())
			);
			loreList.add(
					Component.text("BlueprintID: " + this.blueprintID.asString())
							.color(ColorPallet.DARK_GRAY.getColor())
			);
			List<String> componentNameArray = new ArrayList<>();
			for (@NotNull MercuryItemComponent component : this.components) {
				componentNameArray.add(component.getClass().getSimpleName());
			}

			loreList.add(
					Component.text("Components: " + componentNameArray)
							.color(ColorPallet.DARK_GRAY.getColor())
			);
		}

		return ItemStack.of(this.material)
				.withCustomName(
						placeholderManager.parse(this.name, player)
				)
				.withLore(loreList)
				.withoutExtraTooltip()
				.withTag(uniqueIdTag, this.uniqueId);
	}

	private @NotNull Component formatAttribute(ItemAttributeValue value, String sign, boolean isPositive) {
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
