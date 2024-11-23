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
			LOGGER.error("Unable to register item! File does not exists");
			return;
		}

		try {
			TomlParseResult result = Toml.parse(file.toPath());
			if (result.hasErrors())  {
				result.errors().forEach(error -> LOGGER.error("Error while parsing {} configuration !\n{}\n", file.getName(), error.toString()));
				return;
			}

			String stringId = result.getString("id");
			if (stringId == null) {
				LOGGER.error("Unable to find id property in {} configuration!", file.getName());
				return;
			}

			NamespaceID namespaceID = NamespaceID.from(stringId);

			String name = result.getString("name");
			if (name == null) {
				LOGGER.error("Unable to find name property in {} ({}) configuration!", namespaceID, file.getName());
				return;
			}

			String description = result.getString("description");

			String baseMaterial = result.getString("base_material");
			if (baseMaterial == null) {
				LOGGER.error("Unable to find base_material property in {} ({}) configuration!", namespaceID, file.getName());
				return;
			}

			Material material = Material.fromNamespaceId(baseMaterial);
			if (material == null) {
				LOGGER.error("Unknown material {} in {} ({}) configuration! Please specify valid material. e.g. 'minecraft:diamond_sword'", baseMaterial, namespaceID, file.getName());
				return;
			}

			String rarity = result.getString("rarity");
			ItemRarity itemRarity = null;
			if (rarity != null) {
				try {
					itemRarity = ItemRarity.valueOf(rarity.toUpperCase());
				} catch (IllegalArgumentException e) {
					LOGGER.error("Unknown rarity {} in {} ({}) configuration! Please specify valid rarity. e.g. 'common'", baseMaterial, namespaceID, file.getName());
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


			LOGGER.info("Registering item {} ({})", namespaceID, file.getName());
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
