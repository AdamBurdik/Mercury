package me.adamix.mercury.server.player.profile;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import me.adamix.mercury.server.player.stats.Statistics;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents data class to store player profile data which is stored in database
 *
 */
@Getter
public class ProfileData {
	private final @NotNull UUID playerUniqueId;
	private final @NotNull UUID profileUniqueId;
	@Setter private @NotNull String translationId;
	private int health;
	@Setter private int maxHealth;
	@Setter private float movementSpeed;
	@Setter private float attackSpeed;
	private final @NotNull GamePlayerInventory playerInventory;
	private final @NotNull Statistics statistics;

	/**
	 * Constructs a new ProfileData instance all the required fields
	 *
	 * @param profileUniqueId the unique ID of the player profile
	 * @param health          the current health of the player
	 * @param maxHeath       the maximum health of the player
	 */
	public ProfileData(
			@NotNull UUID playerUniqueId,
			@NotNull UUID profileUniqueId,
			@NotNull String translationId,
			int health,
			int maxHeath,
			float movementSpeed,
			float attackSpeed,
			@NotNull GamePlayerInventory playerInventory,
			@NotNull Statistics statistics
	) {
		this.playerUniqueId = playerUniqueId;
		this.profileUniqueId = profileUniqueId;
		this.translationId = translationId;
		this.health = health;
		this.maxHealth = maxHeath;
		this.movementSpeed = movementSpeed;
		this.attackSpeed = attackSpeed;
		this.playerInventory = playerInventory;
		this.statistics = statistics;
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
