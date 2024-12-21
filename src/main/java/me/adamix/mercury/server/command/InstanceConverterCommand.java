package me.adamix.mercury.server.command;

import net.hollowcube.polar.AnvilPolar;
import net.hollowcube.polar.PolarWorld;
import net.hollowcube.polar.PolarWriter;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class InstanceConverterCommand extends Command {
	public InstanceConverterCommand() {
		super("convert");

		var inputArgument = ArgumentType.String("input");
		var outputArgument = ArgumentType.String("output");

		addSyntax((sender, ctx) -> {

			String input = ctx.get(inputArgument);
			String output = ctx.get(outputArgument);

			try {
				PolarWorld polarWorld = AnvilPolar.anvilToPolar(Path.of("worlds/" + input));

				try (FileOutputStream fos = new FileOutputStream("worlds/dungeons/" + output + ".polar")) {
					fos.write(PolarWriter.write(polarWorld));
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}, inputArgument, outputArgument);
	}
}
