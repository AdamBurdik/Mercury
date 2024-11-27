package me.adamix.mercury.item.core.blueprint;

import me.adamix.mercury.item.core.attribute.ItemAttribute;
import me.adamix.mercury.item.core.attribute.ItemAttributes;
import me.adamix.mercury.item.core.rarity.ItemRarity;
import me.adamix.mercury.utils.FileUtils;
import me.adamix.mercury.utils.TomlUtils;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemBlueprintManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemBlueprintManager.class);
	private final Map<NamespaceID, GameItemBlueprint> itemBlueprintMap = new HashMap<>();

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

	public void register(@NotNull File file) {
		if (!file.exists()) {
			throw new RuntimeException("Unable to register item! File does not exist");
		}

		try {
			TomlParseResult result = Toml.parse(file.toPath());
			TomlUtils.handleErrors(result, file.getName());

			NamespaceID namespaceID = TomlUtils.getNamespacedID(result, "id");
			String name = TomlUtils.getString(result, "name");
			String description = result.getString("description");
			Material material = TomlUtils.getMaterial(result, "material");

			String rarity = result.getString("rarity");
			ItemRarity itemRarity = null;
			if (rarity != null) {
				try {
					itemRarity = ItemRarity.valueOf(rarity.toUpperCase());
				} catch (IllegalArgumentException e) {
					LOGGER.error("Unknown rarity {} in {} ({}) configuration! Please specify valid rarity. e.g. 'common'", rarity, namespaceID, file.getName());
					return;
				}
			}

			TomlTable attributeTable = result.getTable("attributes");
			ItemAttributes itemAttributes = new ItemAttributes();
			if (attributeTable != null) {
				itemAttributes
						.set(ItemAttribute.DAMAGE, TomlUtils.parseItemAttribute(attributeTable, "damage"))
						.set(ItemAttribute.MOVEMENT_SPEED, TomlUtils.parseItemAttribute(attributeTable, "movement_speed"))
						.set(ItemAttribute.ATTACK_SPEED, TomlUtils.parseItemAttribute(attributeTable, "attack_speed"))
						.set(ItemAttribute.MAX_HEALTH, TomlUtils.parseItemAttribute(attributeTable, "max_health"));
			}


			LOGGER.info("Item '{}' ({}) has been registered", namespaceID, file.getName());
			GameItemBlueprint item = new GameItemBlueprint(
					namespaceID,
					material,
					name,
					description,
					itemAttributes,
					itemRarity
			);

			this.register(item);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public void register(GameItemBlueprint item) {
		itemBlueprintMap.put(item.getBlueprintID(), item);
	}

	public @NotNull GameItemBlueprint get(NamespaceID blueprintID) {
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

	public @NotNull Collection<GameItemBlueprint> getAllItems() {
		return this.itemBlueprintMap.values();
	}
}
