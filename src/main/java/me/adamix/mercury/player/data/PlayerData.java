package me.adamix.mercury.player.data;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.player.inventory.GamePlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents data class to store player profile data which is stored in database
 *
 */
@Getter
public class PlayerData {
	private final @NotNull UUID profileUniqueId;
	@Setter private @NotNull String translationId;
	private int health;
	@Setter private int maxHealth;
	@Setter private float movementSpeed;
	@Setter private float attackSpeed;
	private GamePlayerInventory playerInventory;

	/**
	 * Constructs a new PlayerData instance all the required fields
	 *
	 * @param profileUniqueId the unique ID of the player profile
	 * @param health          the current health of the player
	 * @param maxHeath       the maximum health of the player
	 */
	public PlayerData(
			@NotNull UUID profileUniqueId,
			String translationId,
			int health,
			int maxHeath,
			float movementSpeed,
			float attackSpeed,
			GamePlayerInventory playerInventory
	) {
		this.profileUniqueId = profileUniqueId;
		this.health = health;
		this.maxHealth = maxHeath;
		this.movementSpeed = movementSpeed;
		this.attackSpeed = attackSpeed;
		this.playerInventory = playerInventory;
	}

	/**
	 * Sets the current health of player
	 * Health cannot be above maxHealth or below zero
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = Math.max(0, Math.min(health, maxHealth));
	}
}
