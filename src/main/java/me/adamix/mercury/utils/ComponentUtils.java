package me.adamix.mercury.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentUtils {
	public static String componentToAnsi(Component component) {
		StringBuilder ansiBuilder = new StringBuilder();
		appendComponentToAnsi(component, ansiBuilder);
		return ansiBuilder.toString();
	}

	private static void appendComponentToAnsi(Component component, StringBuilder ansiBuilder) {
		if (component instanceof TextComponent textComponent) {
			TextColor color = textComponent.color();
			if (color != null) {
				ansiBuilder.append(String.format("\u001B[38;2;%d;%d;%dm", color.red(), color.green(), color.blue()));
			}

			if (textComponent.hasDecoration(TextDecoration.BOLD)) ansiBuilder.append("\u001B[1m");
			if (textComponent.hasDecoration(TextDecoration.ITALIC)) ansiBuilder.append("\u001B[3m");
			if (textComponent.hasDecoration(TextDecoration.UNDERLINED)) ansiBuilder.append("\u001B[4m");
			if (textComponent.hasDecoration(TextDecoration.STRIKETHROUGH)) ansiBuilder.append("\u001B[9m");

			ansiBuilder.append(textComponent.content());

			ansiBuilder.append("\u001B[0m");

			if (textComponent.content().contains("\n")) {
				ansiBuilder.append(System.lineSeparator());
			}
		}

		for (Component child : component.children()) {
			appendComponentToAnsi(child, ansiBuilder);
		}
	}
}
