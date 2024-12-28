package me.adamix.mercury.server.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.exceptions.ProfileDataNotAvailableException;
import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.player.attribute.PlayerAttribute;
import me.adamix.mercury.server.player.attribute.PlayerAttributes;
import me.adamix.mercury.server.player.data.PlayerData;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.sidebar.MercurySidebar;
import me.adamix.mercury.server.player.state.PlayerState;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class MercuryPlayer extends Player {
	private static final Logger LOGGER = LoggerFactory.getLogger(MercuryPlayer.class);
	private @NotNull PlayerState state = PlayerState.INIT;
	private @Nullable ProfileData profileData;
	private @Nullable PlayerData playerData;
	private final @NotNull Set<MercuryMob> viewedMobs = new HashSet<>();
	@Setter
	private @Nullable UUID dungeonUniqueId;
	@Setter
	private boolean inDebug = true;
	@Setter
	private @Nullable MercurySidebar sidebar;
	@Setter
	private @Nullable UUID partyUniqueId;

	public MercuryPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
		super(playerConnection, gameProfile);
	}

	/**
	 * Sets player data. <br>
	 * Called after player joins the server. <br>
	 * Sets player state to limbo
	 * @param playerData data to set
	 */
	public void setPlayerData(@NotNull PlayerData playerData) {
		this.playerData = playerData;
		this.state = PlayerState.LIMBO;
	}

	/**
	 * Sets profile data. <br>
	 * Called after player chooses profile. <br>
	 * Sets player state to play, if profile data is present
	 * @param profileData data to set, may be null
	 */
	public void setProfileData(@Nullable ProfileData profileData) {
		this.profileData = profileData;
		if (profileData != null) {
			this.state = PlayerState.PLAY;
		}
	}

	/**
	 * Updates player attributes based on the equipped items and currently held item.
	 * <br>
	 * <br>
	 * The process includes:
	 * <br>
	 * - Clearing existing attribute modifiers.
	 * <br>
	 * - Resetting default attributes (e.g., movement speed).
	 * <br>
	 * - Applying attribute modifiers from item currently held in specified slot.
	 *
	 * @param heldSlot the inventory slot index of the item the player is currently holding.
	 *                 If the slot contains no item, only default attributes are applied.
	 */
	public void updateAttributes(int heldSlot) {
		// Clear attributes
		for (AttributeInstance attribute : getAttributes()) {
			attribute.clearModifiers();
		}

		// Set default attributes
		getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(getProfileData().getAttributes().get(PlayerAttribute.MOVEMENT_SPEED));


		// Handle currently holding item
		Optional<MercuryItem> optionalHeldItem = getGameInventory().get(heldSlot);
		if (optionalHeldItem.isPresent()) {
			MercuryItem heldItem = optionalHeldItem.get();

			ItemAttributeComponent itemAttributeComponent = heldItem.getComponent(ItemAttributeComponent.class);
			if (itemAttributeComponent != null) {
				itemAttributeComponent.applyToPlayer(this);
			}
		}
	}

	/**
	 * Updates player attributes based on item they are currently holding.
	 * <br>
	 * This is a convenience method that delegates to {@link #updateAttributes(int)}
	 * using the slot index of item the player is currently holding.
	 */
	public void updateAttributes() {
		this.updateAttributes(getHeldSlot());
	}

	/**
	 * Updates player sidebar, if it is present
	 */
	public void updateSidebar() {
		if (this.sidebar != null) {
			this.sidebar.update(this);
		}
	}

	/**
	 * Clear current player profile data
	 */
	public void clearProfileData() {
		this.profileData = null;
	}

	/**
	 * Teleports player to specified position in same instance.
	 * <br>
	 * And closes current inventory
	 * @param position position where teleport player to
	 * @return {@link CompletableFuture} lambda that is called after teleportation
	 */
	@Override
	public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
		closeInventory();
		return super.teleport(position);
	}

	/**
	 * Teleports player to spawn location
	 */
	public void sendToSpawn() {
		teleport(Server.SPAWN_LOCATION);
	}

	/**
	 * Teleports player to limbo location
	 */
	public void sendToLimbo() {
		teleport(Server.LIMBO_LOCATION);
	}

	public @NotNull PlayerAttributes getPlayerAttributes() {
		return getProfileData().getAttributes();
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

		getPlayerAttributes().set(PlayerAttribute.HEALTH, (double) health);
		if (getPlayerAttributes().get(PlayerAttribute.HEALTH) < 0 && !isDead) {
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
		return getPlayerAttributes().get(PlayerAttribute.HEALTH).floatValue();
	}

	/**
	 * Retrieves player max health
	 * @return The player max health, or -1 if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public int getMaxHealth() {
		return getPlayerAttributes().get(PlayerAttribute.MAX_HEALTH).intValue();
	}

	/**
	 * Changes the player movement speed and edit attribute value
	 * @param movementSpeed new movement speed value
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 */
	public void setMovementSpeed(float movementSpeed) {
		getPlayerAttributes().set(PlayerAttribute.MOVEMENT_SPEED, (double) movementSpeed);
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
		getPlayerAttributes().set(PlayerAttribute.MOVEMENT_SPEED, attribute.getValue());
	}

	/**
	 * Retrieves player current movement speed
	 * @return The player current movement speed, or -1 if profile data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 *
	 */
	public float getMovementSpeed() {
		return getPlayerAttributes().get(PlayerAttribute.MOVEMENT_SPEED).floatValue();
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if player data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet

	 */
	public @Nullable String getTranslationId() {
		if (!hasProfileData()) {
			return null;
		}
		return getProfileData().getTranslationId();
	}

	/**
	 * Retrieves custom player inventory
	 * @return game player inventory
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet
	 *
	 */
	public @NotNull MercuryPlayerInventory getGameInventory() {
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
