package me.adamix.mercury.inventory.core.argument;

import me.adamix.mercury.player.data.PlayerData;

import java.util.List;

public record ProfileSelectionArgument(List<PlayerData> playerDataList) implements InventoryArgument {
}
