package me.adamix.mercury.server.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.core.GameInventory;
import me.adamix.mercury.server.mob.core.GameMob;
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
public class GamePlayer extends Player {
	private @NotNull PlayerState state = PlayerState.INIT;
	private @Nullable ProfileData profileData;
	private @Nullable PlayerData playerData;
	private final @NotNull Set<GameMob> viewedMobs = new HashSet<>();
	private @Nullable UUID dungeonUniqueId;
	@Setter
	private boolean inDebug = true;

	public GamePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
		super(playerConnection, gameProfile);
	}

	public @NotNull PlayerData getPlayerData() {
		if (state == PlayerState.INIT) {
			throw new RuntimeException("Cannot get player data in initialization state!");
		}
		if (playerData == null) {
			throw new RuntimeException("Player data is not loaded yet!");
		}

		return playerData;
	}

	public @NotNull ProfileData getProfileData() {
		if (state == PlayerState.INIT) {
			throw new RuntimeException("Cannot get profile data when player is in initialization state!");
		}
		if (state == PlayerState.LIMBO) {
			throw new RuntimeException("Cannot get profile data when player is in limbo state!");
		}
		if (profileData == null) {
			throw new RuntimeException("Profile data is not loaded yet!");
		}

		return profileData;
	}

	/**
	 * Retrieves player data from {@link PlayerDataManager} and save it to player instance
	 */
	public void loadPlayerData() {
		CompletableFuture<PlayerData> completableFuture = Server.getPlayerDataManager().getPlayerData(this.getUuid());
		if (completableFuture == null) {
			this.kick("Cannot get player data from database! Please notify admins about this message!");
			throw new RuntimeException("Cannot get player data of " + this.getUsername() + "!");
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
	 */
	@Override
	public float getHealth() {
		ProfileData profileData = getProfileData();

		return profileData.getHealth();
	}

	/**
	 * Retrieves player max health
	 * @return The player max health, or -1 if profile data is null
	 */
	public int getMaxHealth() {
		ProfileData profileData = getProfileData();

		return profileData.getMaxHealth();
	}

	/**
	 * Changes the player movement speed and edit attribute value
	 * @param movementSpeed new movement speed value
	 */
	public void setMovementSpeed(float movementSpeed) {
		ProfileData profileData = getProfileData();

		profileData.setMovementSpeed(movementSpeed);
		getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movementSpeed);
	}

	/**
	 * Adjusts the player movement speed by specific amount and operation
	 * @param amount - Amount to modify movement speed by
	 * @param operation - Attribute operation to apply (Add, Multiply Base or Multiply Total)
	 */
	public void modifyMovementSpeed(float amount, AttributeOperation operation) {
		ProfileData profileData = getProfileData();

		AttributeInstance attribute = getAttribute(Attribute.MOVEMENT_SPEED);
		attribute.addModifier(new AttributeModifier("movement_speed", amount, operation));
		profileData.setMovementSpeed((float) attribute.getValue());
	}

	/**
	 * Retrieves player current movement speed
	 * @return The player current movement speed, or -1 if profile data is null
	 */
	public float getMovementSpeed() {
		ProfileData profileData = getProfileData();

		return profileData.getMovementSpeed();
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if profile data is null
	 */
	public @NotNull String getTranslationId() {
		ProfileData profileData = getProfileData();

		return profileData.getTranslationId();
	}

	/**
	 * Retrieves custom player inventory
	 * @return game player inventory
	 */
	public @NotNull GamePlayerInventory getGameInventory() {
		ProfileData profileData = getProfileData();

		return profileData.getPlayerInventory();
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
	public void openGameInventory(@NotNull GameInventory inventory) {
		Server.getInventoryManager().open(inventory, this);
	}

	public static @NotNull GamePlayer of(@NotNull PlayerEvent event) {
		return (GamePlayer) event.getPlayer();
	}

	public static @NotNull GamePlayer of(@NotNull Player player) {
		return (GamePlayer) player;
	}
}
