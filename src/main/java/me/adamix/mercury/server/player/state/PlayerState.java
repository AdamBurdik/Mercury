package me.adamix.mercury.server.player.state;

public enum PlayerState {
	PRE_INIT, // Player data is null
	INIT, // Player data is loaded
	LIMBO, // Player is selecting profile
	PLAY; // All data is loaded and player chose his profile.

	public boolean isPlayable() {
		return this != INIT && this != LIMBO && this != PRE_INIT;
	}
}
