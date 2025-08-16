package me.adamix.mercury.core.configuration.exception;

public class MissingPropertyException extends Exception {
	public MissingPropertyException(String property, String sourceName) {
		super("Missing required property '" + property + "' in: " + sourceName);
	}
}

