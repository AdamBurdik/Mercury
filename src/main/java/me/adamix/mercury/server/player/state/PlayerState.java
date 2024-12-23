package me.adamix.mercury.server.player.state;

public enum PlayerState {
	INIT, // Player just joined server ( data from database is not loaded yet )
	LIMBO, // Player data is loaded from database. In this state player is choosing his profile
	PLAY; // All data is loaded and player chose his profile.

	public boolean isPlayable() {
		return this != INIT && this != LIMBO;
	}
}
