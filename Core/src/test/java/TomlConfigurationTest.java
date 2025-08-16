import me.adamix.mercury.core.configuration.MercuryArray;
import me.adamix.mercury.core.configuration.MercuryConfiguration;
import me.adamix.mercury.core.configuration.MercuryTable;
import me.adamix.mercury.core.configuration.toml.MercuryTomlTable;
import me.adamix.mercury.core.configuration.toml.TomlConfiguration;
import me.adamix.mercury.core.exception.ParsingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TomlConfigurationTest {
	@Test
	void parseTomlFile() throws URISyntaxException, ParsingException, IOException {
		var url = getClass().getResource("toml_file.toml");
		assertNotNull(url, "File not found in resources");

		Path path = Path.of(url.toURI());

		MercuryConfiguration configuration = TomlConfiguration.of(path);

		assertEquals(10, configuration.getInteger("number_value"));
		assertEquals("Hello, World!", configuration.getString("string_value"));
		assertEquals("Table Name", configuration.getTable("TABLE").getString("name"));
		assertEquals("second", configuration.getArray("ARRAY").getTable(1).getString("index"));

		assertEquals(
				"Really Complex Array",
				configuration.getArray("complex_array")
						.getArray(0)
						.getArray(0)
						.getArray(0)
						.getArray(0)
						.getArray(0)
						.getArray(0)
						.getArray(0)
						.getTable(0)
						.getString("name")
		);
	}
}
