package me.adamix.mercury.core.configuration.toml;

import me.adamix.mercury.core.configuration.MercuryConfiguration;
import me.adamix.mercury.core.exception.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class TomlConfiguration extends MercuryTomlTable implements MercuryConfiguration {
	private final @NotNull Path filePath;

	private TomlConfiguration(@NotNull TomlTable tomlTable, @NotNull Path filePath) {
		super(tomlTable, filePath.getFileName().toString());
		this.filePath = filePath;
	}

	@Override
	public @NotNull Path filePath() {
		return filePath;
	}

	public static @NotNull TomlConfiguration of(@NotNull Path path) throws IOException, ParsingException {
		TomlParseResult parseResult = Toml.parse(path);
		if (parseResult.hasErrors()) {
			if (parseResult.hasErrors()) {
				String errors = parseResult.errors().stream()
						.map(Object::toString)
						.collect(Collectors.joining("\n"));
				throw new ParsingException("Errors in file " + path.getFileName().toString() + ":\n" + errors);
			}
		}

		return new TomlConfiguration(parseResult, path);
	}
}
