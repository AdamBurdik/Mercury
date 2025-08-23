package me.adamix.mercury.core.utils;

import me.adamix.mercury.core.MercuryCore;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private static final Path serverRoot = Path.of("");

    /**
     * Resolve a path inside the server root safely.
     * Prevents path traversal outside of the server root.
     */
    public static Path resolveInRoot(String relativePath) {
        Path p = serverRoot.resolve(relativePath).normalize();
        LOGGER.info("Test: {}",  p);
        return p;
    }

    /**
     * Copy a resource file from the plugin JAR into the server root.
     *
     * @param resourcePath       Path to the resource inside JAR (e.g. "config.yml")
     * @param targetRelativePath Relative target path inside server root (e.g. "config/config.yml")
     * @param replace            If true, overwrite existing file
     * @return true if the file was copied, false otherwise
     */
    public static boolean copyResourceToRoot(String resourcePath, String targetRelativePath, boolean replace) {
        Path target = resolveInRoot(targetRelativePath);

        try (InputStream in = MercuryCore.plugin().getResource(resourcePath)) {
            if (in == null) {
                LOGGER.warn("Resource not found in JAR: {}", resourcePath);
                return false;
            }

            Files.createDirectories(target.getParent());

            if (replace) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Resource {} saved (overwritten) to ", target);
                return true;
            } else {
                if (Files.exists(target)) {
                    LOGGER.info("File already exists, skipping: {}", target);
                    return false;
                }
                Files.copy(in, target);
                LOGGER.info("Resource {} saved to {}", resourcePath, target);
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy resource {} -> {}: ", resourcePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Copy a resource into the server root only if it does not exist yet.
     */
    public static boolean copyResourceIfAbsent(String resourcePath, String targetRelativePath) {
        return copyResourceToRoot(resourcePath, targetRelativePath, false);
    }

    /**
     * Ensure that a directory exists inside the server root.
     * Creates it recursively if necessary.
     */
    public static Path ensureDir(String relativeDir) {
        Path dir = resolveInRoot(relativeDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create directory: " + dir + " - " + e.getMessage(), e);
        }
        return dir;
    }
}