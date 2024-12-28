package me.adamix.mercury.server.item;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.item.attribute.ItemAttributeValue;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.ItemRarityComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
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

import java.util.*;

public record MercuryItem (
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
		ItemDescriptionComponent itemDescriptionComponent = getComponent(ItemDescriptionComponent.class);
		if (itemDescriptionComponent != null) {
			for (String line : itemDescriptionComponent.lines()) {
				Component lineComponent = placeholderManager.parse(line, player);
				loreList.add(lineComponent);
			}
		}

		// Add attributes to lore if available
		ItemAttributeComponent itemAttributeComponent = getComponent(ItemAttributeComponent.class);
		if (itemAttributeComponent != null) {
			loreList.add(Component.empty());
			itemAttributeComponent.attributeMap().forEach((attribute, value) -> {
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
		ItemRarityComponent itemRarityComponent = getComponent(ItemRarityComponent.class);
		if (itemRarityComponent != null) {
			ItemRarity rarity = itemRarityComponent.rarity();
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

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		MercuryItem that = (MercuryItem) object;
		return Objects.equals(name, that.name)
				&& Objects.equals(uniqueId, that.uniqueId)
				&& Objects.equals(material, that.material)
				&& Objects.equals(blueprintID, that.blueprintID)
				&& Objects.deepEquals(components, that.components);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId, blueprintID, name, material, Arrays.hashCode(components));
	}

	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("uniqueId", this.uniqueId.toString());
		map.put("blueprintID", this.blueprintID.toString());
		map.put("name", this.name);
		map.put("material", this.material.namespace().asString());

		Map<String, Object> componentMap = new HashMap<>();
		for (@NotNull MercuryItemComponent component : components) {
			componentMap.put(component.name(), component.serialize());
		}

		map.put("components", componentMap);
		return map;

	}

	@SuppressWarnings("unchecked")
	public static MercuryItem deserialize(Map<String, Object> map) {
		UUID uniqueId = UUID.fromString((String) map.get("uniqueId"));
		NamespaceID blueprintID = NamespaceID.from((String) map.get("blueprintID"));
		String itemComponent = (String) map.get("name");
		Material material = Material.fromNamespaceId(NamespaceID.from((String) map.get("material")));
		Objects.requireNonNull(material);
		List<MercuryItemComponent> componentList = new ArrayList<>();
		Map<String, Object> componentMap = (Map<String, Object>) map.get("components");
		componentMap.forEach((name, value) -> {
			componentList.add(
					deserializeComponent(name, (Map<String, Object>) value)
			);
		});

		return new MercuryItem(
				uniqueId,
				blueprintID,
				itemComponent,
				material,
				componentList.toArray(new MercuryItemComponent[0])
		);
	}

	public static @NotNull MercuryItemComponent deserializeComponent(String name, Map<String, Object> map) {
		return switch (name) {
			case "itemDescriptionComponent" -> ItemDescriptionComponent.deserialize(map);
			case "itemAttributeComponent" -> ItemAttributeComponent.deserialize(map);
			case "itemRarityComponent" -> ItemRarityComponent.deserialize(map);
			default -> throw new RuntimeException("No component found with name " + name);
		};

	}
}
