package me.adamix.mercury.core.exception.placeholder;

public class MalformedPlaceholderArgumentException extends Exception {
	public MalformedPlaceholderArgumentException() {
	}

	public MalformedPlaceholderArgumentException(String message) {
		super(message);
	}

	public MalformedPlaceholderArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedPlaceholderArgumentException(Throwable cause) {
		super(cause);
	}

	public MalformedPlaceholderArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
