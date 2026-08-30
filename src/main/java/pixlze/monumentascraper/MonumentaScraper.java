package pixlze.monumentascraper;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pixlze.monumentascraper.config.Configuration;
import pixlze.monumentascraper.managers.Managers;
import pixlze.monumentascraper.scrapers.event.ScraperEvents;

public class MonumentaScraper implements ClientModInitializer {
    public static final Configuration CONFIG = new Configuration();
    public static final Logger LOGGER = LoggerFactory.getLogger(CONFIG.getModId());

    public static File getStorageDirectory(String dirName) {
        return new File(CONFIG.getStorageDirectory(), dirName);
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

    public static Identifier id(String path) {
        return new Identifier(CONFIG.getModId(), path);
    }

    public void close() {
        System.exit(0);
    }
}
