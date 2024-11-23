package me.adamix.mercury.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.Server;
import me.adamix.mercury.inventory.core.GameInventory;
import me.adamix.mercury.mob.core.GameMob;
import me.adamix.mercury.player.data.PlayerData;
import me.adamix.mercury.player.data.PlayerDataManager;
import me.adamix.mercury.player.profile.ProfileData;
import me.adamix.mercury.player.profile.ProfileDataManager;
import me.adamix.mercury.player.inventory.GamePlayerInventory;
import me.adamix.mercury.player.state.PlayerState;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class GamePlayer extends Player {
	private @NotNull PlayerState state = PlayerState.LIMBO;
	private @Nullable PlayerData playerData;
	private @Nullable ProfileData profileData;
	private final @NotNull Set<GameMob> viewedMobs = new HashSet<>();
	@Setter
	private boolean inDebug = true;

	public GamePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
		super(playerConnection, gameProfile);
	}

	/**
	 * Retrieves player data from {@link PlayerDataManager} and save it to player instance
	 */
	public void loadPlayerData() {
		CompletableFuture<PlayerData> completableFuture = Server.getPlayerDataManager().getPlayerData(this.getUuid());
		if (completableFuture == null) {
			this.kick("Cannot get player data from database! Please notify admins about this message!");
			throw new RuntimeException("Cannot get player data!");
		}
		completableFuture.thenAccept(data -> {
			if (data == null) {
				data = new PlayerData(
						getUuid(),
						Duration.ZERO
				);
				Server.getPlayerDataManager().savePlayerData(data);
			}
			this.playerData = data;
		});
	}

	/**
	 * Retrieves profile data from {@link ProfileDataManager} and save it to player instance
	 *
	 * @param profileUniqueId unique ID of player profile
	 */
	public void loadProfileData(UUID profileUniqueId) {
		CompletableFuture<ProfileData> completableFuture = Server.getProfileDataManager().getProfileData(profileUniqueId);
		if (completableFuture == null) {
			throw new RuntimeException("Cannot get profile data!");
		}
		completableFuture.thenAccept(data -> {
			this.profileData = data;
		});
	}

	/**
	 * Clear current player profile data
	 */
	public void clearProfileData() {
		this.profileData = null;
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
		if (this.profileData == null) {
			return;
		}

		this.profileData.setHealth((int) health);
		if (this.profileData.getHealth() < 0 && !isDead) {
			kill();
		}
	}

	/**
	 * Retrieves player current health
	 * @return The player current health, or -1 if profile data is null
	 */
	@Override
	public float getHealth() {
		if (this.profileData == null) {
			return -1;
		}
		return profileData.getHealth();
	}

	/**
	 * Retrieves player max health
	 * @return The player max health, or -1 if profile data is null
	 */
	public int getMaxHealth() {
		if (this.profileData == null) {
			return -1;
		}
		return profileData.getMaxHealth();
	}

	/**
	 * Changes the player movement speed and edit attribute value
	 * @param movementSpeed new movement speed value
	 */
	public void setMovementSpeed(float movementSpeed) {
		if (this.profileData == null) {
			return;
		}
		this.profileData.setMovementSpeed(movementSpeed);
		getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movementSpeed);
	}

	/**
	 * Adjusts the player movement speed by specific amount and operation
	 * @param amount - Amount to modify movement speed by
	 * @param operation - Attribute operation to apply (Add, Multiply Base or Multiply Total)
	 */
	public void modifyMovementSpeed(float amount, AttributeOperation operation) {
		if (this.profileData == null) {
			return;
		}
		AttributeInstance attribute = getAttribute(Attribute.MOVEMENT_SPEED);
		attribute.addModifier(new AttributeModifier("movement_speed", amount, operation));
		this.profileData.setMovementSpeed((float) attribute.getValue());
	}

	/**
	 * Retrieves player current movement speed
	 * @return The player current movement speed, or -1 if profile data is null
	 */
	public float getMovementSpeed() {
		if (this.profileData == null) {
			return -1;
		}
		return this.profileData.getMovementSpeed();
	}

	public static @NotNull GamePlayer of(@NotNull PlayerEvent event) {
		return (GamePlayer) event.getPlayer();
	}
	public static @NotNull GamePlayer of(@NotNull Player player) {
		return (GamePlayer) player;
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if profile data is null
	 */
	public @Nullable String getTranslationId() {
		if (this.profileData == null) {
			return null;
		}
		return this.profileData.getTranslationId();
	}

	/**
	 * Retrieves custom player inventory
	 * @return game player inventory
	 */
	public @Nullable GamePlayerInventory getGameInventory() {
		if (this.profileData == null) {
			return null;
		}
		return this.profileData.getPlayerInventory();
	}

	/**
	 * Shows {@link GameMob game mob} to player and update its name
	 * @param mob mob to show
	 */
	public void show(GameMob mob) {
		mob.addViewer(this);
		this.viewedMobs.add(mob);

		mob.updateName(this);
	}

	/**
	 * Hides {@link GameMob game mob} from player
	 * @param mob mob to hide
	 */
	public void hide(GameMob mob) {
		mob.removeViewer(this);
		this.viewedMobs.remove(mob);
	}

	/**
	 * Opens the specified game inventory
	 * @param inventory inventory to open
	 */
	public void openGameInventory(GameInventory inventory) {
		Server.getInventoryManager().open(inventory, this);
	}

}
