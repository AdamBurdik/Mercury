package me.adamix.mercury.server.party;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
	private final @NotNull Map<UUID, MercuryParty> partyMap = new HashMap<>();
	private final @NotNull Map<UUID, UUID> pendingRequests = new HashMap<>();

	public void createParty(@NotNull MercuryPlayer player) {
		UUID partyUniqueId = UUID.randomUUID();
		MercuryParty party = new MercuryParty(
				partyUniqueId,
				player.getUuid(),
				player.getUsername() + " party"
		);
		partyMap.put(partyUniqueId, party);
		player.setPartyUniqueId(partyUniqueId);
	}

	public @Nullable MercuryParty getParty(@NotNull UUID partyUniqueId) {
		return partyMap.get(partyUniqueId);
	}

	public void acceptJoinRequest(@NotNull MercuryPlayer player, @NotNull MercuryPlayer requester) {
		if (!pendingRequests.containsKey(requester.getUuid())) {
			return;
		}

		UUID requestedPartyUniqueId = pendingRequests.get(requester.getUuid());
		if (!requestedPartyUniqueId.equals(player.getPartyUniqueId())) {
			return;
		}

		pendingRequests.remove(requester.getUuid());
		joinParty(requester, requestedPartyUniqueId);
	}

	public void requestJoinParty(@NotNull MercuryPlayer player, @NotNull UUID partyUniqueId) {
		pendingRequests.put(player.getUuid(), partyUniqueId);
	}

	public void joinParty(@NotNull MercuryPlayer player, @NotNull UUID partyUniqueId) {
		MercuryParty party = getParty(partyUniqueId);
		if (party == null) {
			return;
		}

		party.addPlayer(player);
		player.setPartyUniqueId(partyUniqueId);
	}

	public static @Nullable MercuryParty getParty(@NotNull MercuryPlayer player) {
		if (player.getPartyUniqueId() == null) {
			return null;
		}
		return Server.getPartyManager().getParty(player.getPartyUniqueId());
	}
}
