package me.adamix.mercury.server.item.blueprint;

import me.adamix.mercury.server.attribute.AttributeContainer;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import me.adamix.mercury.server.item.component.ItemDescriptionComponent;
import me.adamix.mercury.server.item.component.MercuryItemComponent;
import me.adamix.mercury.server.item.rarity.ItemRarity;
import me.adamix.mercury.server.toml.MercuryConfiguration;
import me.adamix.mercury.server.utils.FileUtils;
import me.adamix.mercury.server.utils.TomlUtils;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlTable;

import java.io.File;
import java.util.*;

public class ItemBlueprintManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemBlueprintManager.class);
	private final Map<NamespaceID, MercuryItemBlueprint> itemBlueprintMap = new HashMap<>();

	public void registerAllItems() {
		itemBlueprintMap.clear();
		File itemDirectory = new File("resources/items/");
		List<File> fileList = FileUtils.getAllFiles(itemDirectory);

		for (File file : fileList) {
			String extension = FileUtils.getExtension(file);
			if (!extension.equals("toml")) {
				continue;
			}

			register(file);
		}
	}

	public void register(@NotNull File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register item! File does not exist");
		}

		MercuryConfiguration toml = new MercuryConfiguration(tomlFile);

		@NotNull NamespaceID namespaceID = toml.getNamespacedIDSafe("id");
		@NotNull String name = toml.getStringSafe("name");
		@NotNull Material material = toml.getMaterialSafe("material");

		List<MercuryItemComponent> componentList = new ArrayList<>();

		// Parse description to component
		String description = toml.getString("description");
		if (description != null) {
			String[] lines = description.split("\n");

			componentList.add(
					new ItemDescriptionComponent(lines)
			);
		}

		// Parse attributes to component
		TomlTable attributeTable = toml.getTomlTable("attributes");
		if (attributeTable != null) {
			AttributeContainer attributeContainer = new AttributeContainer();
			attributeContainer
					.set(MercuryAttribute.DAMAGE, TomlUtils.parseAttribute(attributeTable, "damage"))
					.set(MercuryAttribute.MOVEMENT_SPEED, TomlUtils.parseAttribute(attributeTable, "movement_speed"))
					.set(MercuryAttribute.ATTACK_SPEED, TomlUtils.parseAttribute(attributeTable, "attack_speed"))
					.set(MercuryAttribute.MAX_HEALTH, TomlUtils.parseAttribute(attributeTable, "max_health"));
			componentList.add(
					new ItemAttributeComponent(attributeContainer.getAttributeMap())
			);
		}

		// Parse rarity to component
		String rarity = toml.getString("rarity");
		if (rarity != null) {
			try {
				ItemRarity itemRarity = ItemRarity.valueOf(rarity.toUpperCase());
				componentList.add(itemRarity.toComponent());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(
						String.format("Unknown rarity %s in %s (%s) configuration! Please specify valid rarity. e.g. 'common'", rarity, namespaceID, tomlFile.getName())
				);
			}
		}


		LOGGER.info("Item '{}' ({}) has been registered", namespaceID, tomlFile.getName());
		MercuryItemBlueprint item = new MercuryItemBlueprint(
				namespaceID,
				material,
				name,
				componentList.toArray(new MercuryItemComponent[0])
		);

		this.register(item);

	}


	public void register(MercuryItemBlueprint item) {
		itemBlueprintMap.put(item.blueprintID(), item);
	}

	public @NotNull Optional<MercuryItemBlueprint> get(NamespaceID blueprintID) {
		return Optional.ofNullable(itemBlueprintMap.get(blueprintID));
	}

	public boolean contains(NamespaceID blueprintID) {
		return itemBlueprintMap.containsKey(blueprintID);
	}

	public @NotNull Set<NamespaceID> getItemIdCollection() {
		return itemBlueprintMap.keySet();
	}

	public @NotNull Collection<MercuryItemBlueprint> getAllItems() {
		return this.itemBlueprintMap.values();
	}
}
