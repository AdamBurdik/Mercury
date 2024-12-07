package me.adamix.mercury.server.common;

import java.util.Map;

public interface SerializableEntity {
	Map<String, Object> serialize();
}
