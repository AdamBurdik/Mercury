package me.adamix.mercury.server.inventory.core.argument;

import me.adamix.mercury.server.player.profile.ProfileData;

import java.util.List;

public record ProfileSelectionArgument(List<ProfileData> profileDataList) implements InventoryArgument {
}
