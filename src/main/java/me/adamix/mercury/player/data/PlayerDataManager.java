package me.adamix.mercury.player.data;

import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerDataManager {

	/**
	 * Retrieves PlayerData from database using profile ID
	 *
	 * @param profileUniqueId unique ID of player profile
	 * @return the PlayerData containing player profile data
	 */
	public @Nullable PlayerData getPlayerData(UUID profileUniqueId) {
		// ToDo Retrieve data from actual database
		// Currently it will be populated with random data
		Random random = new Random();
		int health = random.nextInt(50, 150);

		return new PlayerData(
				UUID.randomUUID(),
				"en",
				health,
				health,
				(float) Attribute.GENERIC_MOVEMENT_SPEED.defaultValue(),
				(float) Attribute.GENERIC_ATTACK_SPEED.defaultValue()
		);
	}

	/**
	 * Save player data to database
	 *
	 * @param playerData player data
	 */
	public void savePlayerData(PlayerData playerData) {
		// ToDo Save data to database
	}

	/**
	 * Retrieves PlayerData list containing all player profile data from database
	 *
	 * @param playerUniqueId - unique ID of player profile
	 * @return list of PlayerData
	 */
	public List<PlayerData> getPlayerDataList(UUID playerUniqueId) {
		// ToDO Retrieve list from actual database
		// Currently it will be populated with random data
		Random random = new Random();

		List<PlayerData> list = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			int health = random.nextInt(50, 150);
			list.add(
					new PlayerData(
							UUID.randomUUID(),
							"en",
							health,
							health,
							(float) Attribute.GENERIC_MOVEMENT_SPEED.defaultValue(),
							(float) Attribute.GENERIC_ATTACK_SPEED.defaultValue()
					)
			);
		}

		return list;
	}

}
