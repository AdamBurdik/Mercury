package me.adamix.mercury.core.entity;

import lombok.Data;
import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.protocol.MercuryProtocol;
import me.adamix.mercury.core.protocol.data.EntityMetadata;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Data
public class MercuryEntity {
	private final @NotNull Entity bukkitEntity;
	private final @NotNull String name;
	private final boolean visibleDefault;

	private final Set<MercuryPlayer> viewers = new HashSet<>();

	public MercuryEntity(@NotNull Entity bukkitEntity, @NotNull String name, boolean visibleDefault) {
		this.bukkitEntity = bukkitEntity;
		this.name = name;
		this.visibleDefault = visibleDefault;
	}

	public void updateName(@NotNull MercuryPlayer player) {
		System.out.println("Updating name for: " + player.getName());
		EntityMetadata data = new EntityMetadata(((CraftEntity) bukkitEntity).getHandle())
				.customName(MercuryCore.placeholderManager().parse(this.name, player));

		MercuryProtocol.sendEntityMetadata(data, player);
	}

	public void addViewer(@NotNull MercuryPlayer player) {
		viewers.add(player);
		player.getBukkitPlayer().showEntity(MercuryCore.plugin(), bukkitEntity);
		updateName(player);
	}

	public void removeViewer(@NotNull MercuryPlayer player) {
		viewers.remove(player);
		player.getBukkitPlayer().hideEntity(MercuryCore.plugin(), bukkitEntity);
	}

	public boolean isViewer(@NotNull MercuryPlayer player) {
		return viewers.contains(player);
	}

	@ApiStatus.Internal
	public @NotNull Entity getBukkitEntity() {
		return bukkitEntity;
	}
}
