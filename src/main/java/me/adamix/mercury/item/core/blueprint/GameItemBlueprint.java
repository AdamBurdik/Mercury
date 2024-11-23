package me.adamix.mercury.item.core.blueprint;

import lombok.Getter;
import me.adamix.mercury.common.SerializableEntity;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.item.core.attribute.ItemAttributes;
import me.adamix.mercury.item.core.rarity.ItemRarity;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents item blueprint which containing all its generic data.
 * Typically used to define items loaded from config file
 */
@Getter
public class GameItemBlueprint implements SerializableEntity {
	private final @NotNull NamespaceID blueprintID;
	private final @NotNull Material baseMaterial;
	private final @NotNull String name;
	private final @Nullable String description;
	private final @NotNull ItemAttributes attributes;
	private final @Nullable ItemRarity rarity;

	public GameItemBlueprint(
			@NotNull NamespaceID blueprintID,
			@NotNull Material baseMaterial,
			@NotNull String name,
			@Nullable String description,
			@NotNull ItemAttributes attributes,
			@Nullable ItemRarity rarity
	) {
		this.blueprintID = blueprintID;
		this.baseMaterial = baseMaterial;
		this.name = name;
		this.description = description;
		this.attributes = attributes;
		this.rarity = rarity;
	}

	public GameItem build(UUID itemUniqueId) {
		return new GameItem(
				itemUniqueId,
				this.blueprintID,
				this.baseMaterial,
				this.name,
				this.description,
				this.attributes,
				this.rarity
		);
	}

	@Override
	public String toString() {
		return String.valueOf(this.serialize());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<>();

		data.put("id", this.blueprintID);
		data.put("baseMaterial", this.baseMaterial.name());
		data.put("name", this.name);
		data.put("description", this.description);
		data.put("attributes", this.attributes.serialize());
		data.put("rarity", this.rarity.name());

		return data;
	}
}
