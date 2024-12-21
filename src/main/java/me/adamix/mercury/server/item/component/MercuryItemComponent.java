package me.adamix.mercury.server.item.component;


import java.util.Map;

public interface MercuryItemComponent {
	String name();
	Map<String, Object> serialize();
}
