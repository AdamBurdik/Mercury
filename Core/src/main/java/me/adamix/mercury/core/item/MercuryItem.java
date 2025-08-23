package me.adamix.mercury.core.item;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
public class MercuryItem {
	private final @NotNull Material material;
	private @Nullable String name;

	public MercuryItem(@NotNull Material material) {
		this.material = material;
	}

	public @NotNull ItemStack toItemStack(@NotNull MercuryPlayer player) {
		ItemStack itemStack = new ItemStack(material);
		itemStack.editMeta(meta -> {
			if (name != null) {
				Component component = MercuryCore.placeholderManager().parse(name, player);
				meta.customName(component);
			}
		});
		return itemStack;
	}

	public @NotNull MercuryItem setName(@Nullable String name) {
		this.name = name;
		return this;
	}

	public static @NotNull MercuryItem of(@NotNull Material material) {
		return new MercuryItem(material);
	}
}
