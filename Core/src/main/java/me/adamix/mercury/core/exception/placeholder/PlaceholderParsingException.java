package me.adamix.mercury.core.exception.placeholder;


public class PlaceholderParsingException extends Exception {
	public PlaceholderParsingException() {
	}

	public PlaceholderParsingException(String message) {
		super(message);
	}

	public PlaceholderParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlaceholderParsingException(Throwable cause) {
		super(cause);
	}

	public PlaceholderParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
