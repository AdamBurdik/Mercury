package me.adamix.mercury.server.item.blueprint;

import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents item blueprint which containing all its generic data.
 * Typically used to define items loaded from config file
 */
public record MercuryItemBlueprint(@NotNull NamespaceID blueprintID, @NotNull Material baseMaterial,
                                   @NotNull String name, MercuryItemComponent[] components) {
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
}
