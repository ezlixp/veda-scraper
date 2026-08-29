package pixlze.monumentascraper;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import pixlze.monumentascraper.managers.Managers;
import pixlze.monumentascraper.scrapers.event.ScraperEvents;

public class MonumentaScraper implements ClientModInitializer {
    public static final String MOD_ID = "monumenta-scraper";
    public static final String MOD_STORAGE_ROOT = "monumenta-scraper";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static File getModStorageDir(String dirName) {
        return new File(MOD_STORAGE_ROOT, dirName);
    }

    @Override
    public void onInitializeClient() {
        Managers.init();
        Managers.Tick.scheduleLater(() -> {
            MonumentaScraper.LOGGER.info("Scrapers starting");
            Managers.Scraper.runScrapers();
        }, 10);
        ScraperEvents.ALL_DONE.register(this::close);

    }

    public static boolean isDevelopment() {
        return false;
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public void close() {
        System.exit(0);
    }
}
