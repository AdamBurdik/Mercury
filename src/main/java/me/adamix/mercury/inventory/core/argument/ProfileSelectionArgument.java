package me.adamix.mercury.inventory.core.argument;

import me.adamix.mercury.player.profile.ProfileData;

import java.util.List;

public record ProfileSelectionArgument(List<ProfileData> profileDataList) implements InventoryArgument {
}
