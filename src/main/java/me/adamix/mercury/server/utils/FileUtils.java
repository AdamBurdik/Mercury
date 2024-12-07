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

	public static void forEachFile(String directory, Predicate<File> predicate, Consumer<File> consumer) {
		List<File> fileList = getAllFiles(new File(directory));

		for (File file : fileList) {
			if (predicate.test(file)) {
				consumer.accept(file);
			}
		}
	}

	public static @NonNull String getExtension(File file) {
		int dotIndex = file.getName().lastIndexOf('.');
		return (dotIndex == -1) ? "" : file.getName().substring(dotIndex + 1);
	}
}
