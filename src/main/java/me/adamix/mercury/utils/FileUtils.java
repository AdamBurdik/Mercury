package me.adamix.mercury.utils;

import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
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

	public static @NonNull String getExtension(File file) {
		int dotIndex = file.getName().lastIndexOf('.');
		return (dotIndex == -1) ? "" : file.getName().substring(dotIndex + 1);
	}
}
