package me.adamix.mercury.player;

import lombok.Getter;
import me.adamix.mercury.Server;
import me.adamix.mercury.player.data.PlayerData;
import me.adamix.mercury.player.data.PlayerDataManager;
import me.adamix.mercury.player.inventory.GamePlayerInventory;
import me.adamix.mercury.player.state.PlayerState;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class GamePlayer extends Player {
	private @NotNull PlayerState state = PlayerState.LIMBO;
	private @Nullable PlayerData playerData;

	public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
		super(uuid, username, playerConnection);
	}

	/**
	 * Retrieves player data from {@link PlayerDataManager}
	 *
	 * @param profileUniqueId unique ID of player profile
	 */
	public void loadPlayerData(UUID profileUniqueId) {
		this.playerData = Server.getPlayerDataManager().getPlayerData(profileUniqueId);
	}

	/**
	 * Clear current player profile data
	 */
	public void clearPlayerData() {
		this.playerData = null;
	}

	@Override
	public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
		closeInventory();
		return super.teleport(position);
	}

	/**
	 * Teleport player to spawn location and change his state
	 */
	public void sendToSpawn() {
		this.state = PlayerState.SPAWN;
		teleport(Server.SPAWN_LOCATION);
	}

	/**
	 * Teleport player to limbo location and change his state
	 */
	public void sendToLimbo() {
		this.state = PlayerState.LIMBO;
		teleport(Server.LIMBO_LOCATION);
	}

	/**
	 * Changes the player health, kill it if {@code health} is &lt;= 0 and is not dead yet.
	 *
	 * @param health the new player health
	 */
	@Override
	public void setHealth(float health) {
		if (this.playerData == null) {
			return;
		}

		this.playerData.setHealth((int) health);
		if (this.playerData.getHealth() < 0 && !isDead) {
			kill();
		}
	}

	/**
	 * Retrieves player current health
	 * @return The player current health, or -1 if player data is null
	 */
	@Override
	public float getHealth() {
		if (this.playerData == null) {
			return -1;
		}
		return playerData.getHealth();
	}

	/**
	 * Retrieves player max health
	 * @return The player max health, or -1 if player data is null
	 */
	public int getMaxHealth() {
		if (this.playerData == null) {
			return -1;
		}
		return playerData.getMaxHealth();
	}

	/**
	 * Changes the player movement speed and edit attribute value
	 * @param movementSpeed new movement speed value
	 */
	public void setMovementSpeed(float movementSpeed) {
		if (this.playerData == null) {
			return;
		}
		this.playerData.setMovementSpeed(movementSpeed);
		getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
	}

	/**
	 * Adjusts the player movement speed by specific amount and operation
	 * @param amount - Amount to modify movement speed by
	 * @param operation - Attribute operation to apply (Add, Multiply Base or Multiply Total)
	 */
	public void modifyMovementSpeed(float amount, AttributeOperation operation) {
		if (this.playerData == null) {
			return;
		}
		AttributeInstance attribute = getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		attribute.addModifier(new AttributeModifier("movement_speed", amount, operation));
		this.playerData.setMovementSpeed((float) attribute.getValue());
	}

	/**
	 * Retrieves player current movement speed
	 * @return The player current movement speed, or -1 if player data is null
	 */
	public float getMovementSpeed() {
		if (this.playerData == null) {
			return -1;
		}
		return this.playerData.getMovementSpeed();
	}

	public static @NotNull GamePlayer of(@NotNull PlayerEvent event) {
		return (GamePlayer) event.getPlayer();
	}
	public static @NotNull GamePlayer of(@NotNull Player player) {
		return (GamePlayer) player;
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if player data is null
	 */
	public @Nullable String getTranslationId() {
		if (this.playerData == null) {
			return null;
		}
		return this.playerData.getTranslationId();
	}

	/**
	 * Retrieves custom player inventory
	 * @return game player inventory
	 */
	public @Nullable GamePlayerInventory getGameInventory() {
		if (this.playerData == null) {
			return null;
		}
		return this.playerData.getPlayerInventory();
	}

}
