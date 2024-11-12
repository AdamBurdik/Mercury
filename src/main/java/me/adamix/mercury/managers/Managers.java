package me.adamix.mercury.managers;

import lombok.Getter;
import me.adamix.mercury.inventory.core.InventoryManager;
import me.adamix.mercury.item.core.ItemManager;
import me.adamix.mercury.mob.core.MobManager;
import me.adamix.mercury.placeholder.PlaceholderManager;
import me.adamix.mercury.player.data.PlayerDataManager;
import me.adamix.mercury.translation.TranslationManager;

public class Managers {
	@Getter private static MobManager mobManager;
	@Getter private static ItemManager itemManager;
	@Getter private static PlayerDataManager playerDataManager;
	@Getter private static InventoryManager inventoryManager;
	@Getter private static TranslationManager translationManager;
	@Getter private static PlaceholderManager placeholderManager;

	private Managers() {}  // Prevent instantiation

	// Initialize all managers in one place
	public static void init() {
		mobManager = new MobManager();
		itemManager = new ItemManager();
		playerDataManager = new PlayerDataManager();
		inventoryManager = new InventoryManager();
		translationManager = new TranslationManager();
		placeholderManager = new PlaceholderManager();
	}
}
