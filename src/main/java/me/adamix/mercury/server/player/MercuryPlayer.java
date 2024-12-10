package me.adamix.mercury.server.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.exceptions.PlayerDataNotAvailableException;
import me.adamix.mercury.server.exceptions.ProfileDataNotAvailableException;
import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.player.data.PlayerData;
import me.adamix.mercury.server.player.data.PlayerDataManager;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import me.adamix.mercury.server.player.state.PlayerState;
import me.adamix.mercury.server.player.stats.Statistics;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class MercuryPlayer extends Player {
	private @NotNull PlayerState state = PlayerState.INIT;
	private @Nullable ProfileData profileData;
	private @Nullable PlayerData playerData;
	private final @NotNull Set<MercuryMob> viewedMobs = new HashSet<>();
	private @Nullable UUID dungeonUniqueId;
	@Setter
	private boolean inDebug = true;

	public MercuryPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
		super(playerConnection, gameProfile);
	}

	/**
	 * Retrieves player data.
	 *
	 * @return the {@link PlayerData} associated with the player
	 * @throws PlayerDataNotAvailableException if the player is in the initialization state
	 *                                         or the player data has not been loaded yet
	 */
	public @NotNull PlayerData getPlayerData() {
		if (state == PlayerState.INIT) {
			throw new PlayerDataNotAvailableException("Cannot get player data in initialization state!");
		}
		if (playerData == null) {
			throw new PlayerDataNotAvailableException("Player data is not loaded yet!");
		}

		return playerData;
	}

	/**
	 * Retrieves player dat
	 * @return the {@link PlayerData} associated with player currently selected profile
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public @NotNull ProfileData getProfileData() {
		if (state == PlayerState.INIT) {
			throw new ProfileDataNotAvailableException("Cannot get profile data when player is in initialization state!");
		}
		if (state == PlayerState.LIMBO) {
			throw new ProfileDataNotAvailableException("Cannot get profile data when player is in limbo state!");
		}
		if (profileData == null) {
			throw new ProfileDataNotAvailableException("Profile data is not loaded yet!");
		}

		return profileData;
	}

	/**
	 * Retrieves player data from {@link PlayerDataManager} and save it to player instance
	 * @throws PlayerDataNotAvailableException if player data is not available in database
	 */
	public void loadPlayerData() {
		CompletableFuture<PlayerData> completableFuture = Server.getPlayerDataManager().getPlayerData(this.getUuid());
		if (completableFuture == null) {
			this.kick("Cannot get player data from database! Please notify admins about this message!");
			throw new PlayerDataNotAvailableException("Cannot get player data of " + this.getUsername() + "!");
		}
		completableFuture.thenAccept(data -> {
			if (data == null) {
				data = new PlayerData(
						getUuid(),
						new Statistics()
				);
				Server.getPlayerDataManager().savePlayerData(data);
			}
			this.playerData = data;
			this.state = PlayerState.LIMBO;
		});
	}

	/**
	 * Retrieves profile data from {@link ProfileDataManager} and save it to player instance
	 *
	 * @param profileUniqueId unique ID of player profile
	 * @throws ProfileDataNotAvailableException if profile data is not available in database
	 */
	public void loadProfileData(UUID profileUniqueId) {
		CompletableFuture<ProfileData> completableFuture = Server.getProfileDataManager().getProfileData(profileUniqueId);
		if (completableFuture == null) {
			throw new ProfileDataNotAvailableException("Cannot get profile data!");
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
		this.state = PlayerState.PLAY;
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
	 * @throws ProfileDataNotAvailableException if the player is in limbo state or the profile data has not been loaded yet
	 */
	@Override
	public void setHealth(float health) {
		if (state == PlayerState.INIT) {
			return;
		}

		ProfileData profileData = getProfileData();

		profileData.setHealth((int) health);
		if (profileData.getHealth() < 0 && !isDead) {
			kill();
		}
	}

	/**
	 * Retrieves player current health
	 * @return The player current health, or -1 if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	@Override
	public float getHealth() {
		return getProfileData().getHealth();
	}

	/**
	 * Retrieves player max health
	 * @return The player max health, or -1 if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public int getMaxHealth() {
		return getProfileData().getMaxHealth();
	}

	/**
	 * Changes the player movement speed and edit attribute value
	 * @param movementSpeed new movement speed value
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public void setMovementSpeed(float movementSpeed) {
		getProfileData().setMovementSpeed(movementSpeed);
		getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movementSpeed);
	}

	/**
	 * Adjusts the player movement speed by specific amount and operation
	 * @param amount - Amount to modify movement speed by
	 * @param operation - Attribute operation to apply (Add, Multiply Base or Multiply Total)
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public void modifyMovementSpeed(float amount, AttributeOperation operation) {
		AttributeInstance attribute = getAttribute(Attribute.MOVEMENT_SPEED);
		attribute.addModifier(new AttributeModifier("movement_speed", amount, operation));
		getProfileData().setMovementSpeed((float) attribute.getValue());
	}

	/**
	 * Retrieves player current movement speed
	 * @return The player current movement speed, or -1 if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 *
	 */
	public float getMovementSpeed() {
		return getProfileData().getMovementSpeed();
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet

	 */
	public @NotNull String getTranslationId() {
		return getProfileData().getTranslationId();
	}

	/**
	 * Retrieves custom player inventory
	 * @return game player inventory
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 *
	 */
	public @NotNull GamePlayerInventory getGameInventory() {
		return getProfileData().getPlayerInventory();
	}

	/**
	 * Shows {@link MercuryMob game mob} to player and update its name
	 * @param mob mob to show
	 */
	public void show(MercuryMob mob) {
		mob.addViewer(this);
		this.viewedMobs.add(mob);

		mob.updateName(this);
	}

	/**
	 * Hides {@link MercuryMob game mob} from player
	 * @param mob mob to hide
	 */
	public void hide(MercuryMob mob) {
		mob.removeViewer(this);
		this.viewedMobs.remove(mob);
	}

	/**
	 * Opens the specified game inventory
	 * @param inventory inventory to open
	 */
	public void openGameInventory(@NotNull MercuryInventory inventory) {
		Server.getInventoryManager().open(inventory, this);
	}

	public boolean hasPlayerData() {
		return this.playerData != null;
	}

	public boolean hasProfileData() {
		return this.profileData != null;
	}

	public static @NotNull MercuryPlayer of(@NotNull PlayerEvent event) {
		return (MercuryPlayer) event.getPlayer();
	}

	public static @NotNull MercuryPlayer of(@NotNull Player player) {
		return (MercuryPlayer) player;
	}
}
