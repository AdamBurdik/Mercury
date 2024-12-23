package me.adamix.mercury.server.party;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.player.MercuryPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class MercuryParty {
	private final @NotNull UUID partyUniqueId;
	private final @NotNull UUID leaderUniqueId;
	private final @NotNull Set<UUID> adminSet = new HashSet<>();
	private final @NotNull Set<UUID> memberSet = new HashSet<>();
	@Setter
	private @NotNull String name;
	@Setter
	private @Nullable String description;

	public MercuryParty(
			@NotNull UUID partyUniqueId,
			@NotNull UUID leaderUniqueId,
			@NotNull String name
	) {
		this.partyUniqueId = partyUniqueId;
		this.leaderUniqueId = leaderUniqueId;
		this.name = name;
	}

	public void addPlayer(@NotNull MercuryPlayer player) {
		memberSet.add(player.getUuid());
	}

	public void setAdmin(@NotNull MercuryPlayer player) {
		memberSet.remove(player.getUuid());
		adminSet.add(player.getUuid());
	}

	public void setMember(@NotNull MercuryPlayer player) {
		adminSet.remove(player.getUuid());
		memberSet.add(player.getUuid());
	}

	public Set<UUID> getAllMembers() {
		Set<UUID> allMemberSet = new HashSet<>();
		allMemberSet.addAll(memberSet);
		allMemberSet.addAll(adminSet);
		allMemberSet.add(this.leaderUniqueId);
		return allMemberSet;
	}
}
