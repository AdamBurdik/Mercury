package me.adamix.mercury.server.item.core.blueprint;

import me.adamix.mercury.server.item.core.attribute.ItemAttribute;
import me.adamix.mercury.server.item.core.attribute.ItemAttributes;
import me.adamix.mercury.server.item.core.rarity.ItemRarity;
import me.adamix.mercury.server.toml.TomlConfiguration;
import me.adamix.mercury.server.utils.FileUtils;
import me.adamix.mercury.server.utils.TomlUtils;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlTable;

import java.io.File;
import java.util.*;

public class ItemBlueprintManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemBlueprintManager.class);
	private final Map<NamespaceID, MercuryItemBlueprint> itemBlueprintMap = new HashMap<>();

	public void registerAllItems() {
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

		TomlConfiguration toml = new TomlConfiguration(tomlFile);

		@NotNull NamespaceID namespaceID = toml.getNamespacedIDSafe("id");
		@NotNull String name = toml.getStringSafe("name");
		@Nullable String description = toml.getString("description");
		@NotNull Material material = toml.getMaterialSafe("material");

		String rarity = toml.getString("rarity");
		ItemRarity itemRarity = null;
		if (rarity != null) {
			try {
				itemRarity = ItemRarity.valueOf(rarity.toUpperCase());
			} catch (IllegalArgumentException e) {
				LOGGER.error("Unknown rarity {} in {} ({}) configuration! Please specify valid rarity. e.g. 'common'", rarity, namespaceID, tomlFile.getName());
				return;
			}
		}

		TomlTable attributeTable = toml.getTable("attributes");
		ItemAttributes itemAttributes = new ItemAttributes();
		if (attributeTable != null) {
			itemAttributes
					.set(ItemAttribute.DAMAGE, TomlUtils.parseItemAttribute(attributeTable, "damage"))
					.set(ItemAttribute.MOVEMENT_SPEED, TomlUtils.parseItemAttribute(attributeTable, "movement_speed"))
					.set(ItemAttribute.ATTACK_SPEED, TomlUtils.parseItemAttribute(attributeTable, "attack_speed"))
					.set(ItemAttribute.MAX_HEALTH, TomlUtils.parseItemAttribute(attributeTable, "max_health"));
		}


		LOGGER.info("Item '{}' ({}) has been registered", namespaceID, tomlFile.getName());
		MercuryItemBlueprint item = new MercuryItemBlueprint(
				namespaceID,
				material,
				name,
				description,
				itemAttributes,
				itemRarity
		);

		this.register(item);

	}


	public void register(MercuryItemBlueprint item) {
		itemBlueprintMap.put(item.getBlueprintID(), item);
	}

	public @NotNull MercuryItemBlueprint get(NamespaceID blueprintID) {
		if (!itemBlueprintMap.containsKey(blueprintID)) {
			throw new RuntimeException("No blueprint item exists with id " + blueprintID.asString() + "!");
		}
		return itemBlueprintMap.get(blueprintID);
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
