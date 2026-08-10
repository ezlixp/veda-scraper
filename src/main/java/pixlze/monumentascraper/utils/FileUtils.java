package pixlze.monumentascraper.utils;

import pixlze.monumentascraper.MonumentaScraper;

import java.io.File;

public class FileUtils {
    public static void mkdir(File dir) {
        if (dir.isDirectory()) return;
        if (!dir.mkdirs()) {
            MonumentaScraper.LOGGER.error("couldn't make directory {}", dir);
        }
    }
}