package me.adamix.mercury.server.utils;

import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FileUtils {
	public static Predicate<File> isTomlPredicate = file -> {
		String extension = FileUtils.getExtension(file);
		return extension.equals("toml");
	};

	/**
	 * Recursively retrieves list of all files in specific directory
	 * @param directory directory to get all files
	 * @return {@link List<File>} list of retrieved files
	 */
	public static List<File> getAllFiles(File directory) {
		List<File> fileList = new ArrayList<>();
		if (!directory.isDirectory()) {
			return fileList;
		}

		File[] content = directory.listFiles();
		if (content == null) {
			return fileList;
		}

		for (File file : content) {
			if (file.isDirectory()) {
				fileList.addAll(getAllFiles(file));
			}
			else {
				fileList.add(file);
			}
		}

		return fileList;
	}

	/**
	 * Recursively retrieves specific files from specified directory by predicate
	 * @param directory directory to get files
	 * @param predicate predicate to check for each file
	 * @param consumer consumer to run for each file
	 */
	public static void forEachFile(String directory, Predicate<File> predicate, Consumer<File> consumer) {
		List<File> fileList = getAllFiles(new File(directory));

		for (File file : fileList) {
			if (predicate.test(file)) {
				consumer.accept(file);
			}
		}
	}

	/**
	 * Retrieves extension of file
	 * @param file file to get extension of
	 * @return {@link String} extension excluding dot (e.g. toml )
	 */
	public static @NonNull String getExtension(File file) {
		int dotIndex = file.getName().lastIndexOf('.');
		return (dotIndex == -1) ? "" : file.getName().substring(dotIndex + 1);
	}
}
