package me.adamix.mercury.core.menu.context;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class InitialContext extends MenuContext {
	private @Nullable Component title;
	private int rows = 1;

	public InitialContext(@NotNull MercuryPlayer player) {
		super(player);
	}

	public void setTitle(@NotNull String title) {
		this.title = MercuryCore.placeholderManager().parse(title, getPlayer());
	}

	public void setTitle(@Nullable Component title) {
		this.title = title;
	}
}
