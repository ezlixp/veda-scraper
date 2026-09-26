package pixlze.monumentascraper.config;

import net.fabricmc.loader.api.FabricLoader;
import pixlze.monumentascraper.config.type.ConfigLoad;

public final class Configuration {
    private static final String MOD_ID = "monumenta-scraper";
    private static final String MOD_STORAGE_DIR = "monumenta-scraper";
    private static final ConfigLoad CONFIG_LOAD = ConfigLoad.FILE;

    private final boolean isDevelopmentEnvironment;

    public Configuration() {
        isDevelopmentEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public String getModId() {
        return MOD_ID;
    }

    public String getStorageDirectory() {
        return MOD_STORAGE_DIR;
    }

    public boolean isDevelopmentEnvironment() {
        return isDevelopmentEnvironment;
    }

    public ConfigLoad getConfigLoad() {
        return CONFIG_LOAD;
    }

}
