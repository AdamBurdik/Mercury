package me.adamix.mercury.server.item.blueprint;

import lombok.Getter;
import me.adamix.mercury.server.common.SerializableEntity;
import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents item blueprint which containing all its generic data.
 * Typically used to define items loaded from config file
 */
@Getter
public class MercuryItemBlueprint implements SerializableEntity {
	private final @NotNull NamespaceID blueprintID;
	private final @NotNull Material baseMaterial;
	private final @NotNull String name;
	private final MercuryItemComponent[] components;

	public MercuryItemBlueprint(
			@NotNull NamespaceID blueprintID,
			@NotNull Material baseMaterial,
			@NotNull String name,
			@NotNull MercuryItemComponent[] components
	) {
		this.blueprintID = blueprintID;
		this.baseMaterial = baseMaterial;
		this.name = name;
		this.components = components;
	}

	public @NotNull MercuryItem build(UUID itemUniqueId) {
		return new MercuryItem(
				itemUniqueId,
				this.blueprintID,
				this.name,
				this.baseMaterial,
				this.components
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
		data.put("components", Arrays.toString(this.components));

		return data;
	}
}
