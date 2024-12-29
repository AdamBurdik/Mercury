package me.adamix.mercury.server.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.exceptions.PlayerDataNotAvailableException;
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
@Setter
public class MercuryPlayer extends Player {
	private static final Logger LOGGER = LoggerFactory.getLogger(MercuryPlayer.class);
	private PlayerState state = PlayerState.PRE_INIT;
	private @Nullable PlayerData playerData;
	private @Nullable ProfileData profileData;

	private final @NotNull Set<MercuryMob> viewedMobs = new HashSet<>();
	private @Nullable UUID dungeonUniqueId;
	private boolean inDebug = true;
	private @Nullable MercurySidebar sidebar;
	private @Nullable UUID partyUniqueId;

	public MercuryPlayer(
			@NotNull PlayerConnection playerConnection,
			@NotNull GameProfile gameProfile
	) {
		super(playerConnection, gameProfile);
	}

	/**
	 * Retrieves player data.
	 *
	 * @return the {@link PlayerData} associated with the player.
	 * @throws PlayerDataNotAvailableException if the player is in the initialization state
	 *                                         or the player data has not been loaded yet.
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
	 * Retrieves player data.
	 * @return the {@link PlayerData} associated with player currently selected profile.
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet.
	 */
	public @NotNull ProfileData getProfileData() {
		if (state == PlayerState.INIT) {
			LOGGER.error("Cannot get profile data when player is in initialization state! ({}={})", this.getUsername(), this.getUuid());
			throw new ProfileDataNotAvailableException("Cannot get profile data when player is in initialization state!");
		}
		if (state == PlayerState.LIMBO) {
			LOGGER.error("Cannot get profile data when player is in limbo state! ({}={})", this.getUsername(), this.getUuid());
			throw new ProfileDataNotAvailableException("Cannot get profile data when player is in limbo state!");
		}
		if (profileData == null) {
			LOGGER.error("Profile data is not loaded yet! ({}={})", this.getUsername(), this.getUuid());
			throw new ProfileDataNotAvailableException("Profile data is not loaded yet!");
		}

		return profileData;
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

	/**
	 * Retrieves player attributes from profile data manager
	 * @return {@link PlayerAttributes}, may be null if profile data is not available
	 */
	public @NotNull PlayerAttributes getPlayerAttributes() {
		return getProfileData().getAttributes();
	}

	/**
	 * Retrieves player current translation id
	 * @return The player current translation id, or null if player data is null
	 * @throws ProfileDataNotAvailableException if the player is in initialization or limbo state or the profile data has not been loaded yet

	 */
	public @Nullable String getTranslationId() {
		return getPlayerData().getTranslationId();
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
	 * Opens the specified game inventory
	 * @param inventory inventory to open
	 */
	public void openGameInventory(@NotNull MercuryInventory inventory) {
		Server.getInventoryManager().open(inventory, this);
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
		for (AttributeInstance attribute : this.getAttributes()) {
			attribute.clearModifiers();
		}

		// Set default attributes
		this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(this.getProfileData().getAttributes().get(PlayerAttribute.MOVEMENT_SPEED));


		// Handle currently holding item
		Optional<MercuryItem> optionalHeldItem = this.getGameInventory().get(heldSlot);
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
		updateAttributes(this.getHeldSlot());
	}

	public static @NotNull MercuryPlayer of(@NotNull PlayerEvent event) {
		return of(event.getPlayer());
	}

	public static @NotNull MercuryPlayer of(@NotNull Player player) {
		return (MercuryPlayer) player;
	}
}
