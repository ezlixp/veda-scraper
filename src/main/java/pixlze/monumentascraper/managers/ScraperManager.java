package pixlze.monumentascraper.managers;

import java.io.File;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.type.Manager;
import pixlze.monumentascraper.mc.event.MonumentaChatMessage;
import pixlze.monumentascraper.scrapers.LeaderboardScraper;
import pixlze.monumentascraper.scrapers.event.ScraperEvents;
import pixlze.monumentascraper.scrapers.type.Scraper;

public class ScraperManager extends Manager {
    private static final File CONFIG_DIR = MonumentaScraper.getStorageDirectory("config");
    private static final File DATA_DIR = MonumentaScraper.getStorageDirectory("data");

    private int uncompletedScrapers = 0;
    private final List<Scraper> scrapers = new ArrayList<>();
    private Scraper currentScraper;
    private boolean allDone = false;
    private boolean firstConnection = true;

    private final File dataFile;
    private final File configFile;
    private final JsonObject dataObject;
    private final JsonArray snapshots;

    public ScraperManager() {
        super(List.of());
        dataFile = new File(DATA_DIR, "data.json");
        configFile = new File(CONFIG_DIR, "config.json");
        dataObject = new JsonObject();
        snapshots = new JsonArray();
        dataObject.add("snapshots", snapshots);
    }

    @Override
    public void init() {
        ScraperEvents.DONE.register(this::writeData);
        ClientPlayConnectionEvents.JOIN.register(this::onConnected);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnected);
        MonumentaChatMessage.EVENT.register(this::onChatMessageReceived);

        JsonArray configObject;
        try {
            // configObject = Managers.Json.loadJsonFromFile(configFile).getAsJsonArray();
            HttpResponse<String> res = Managers.Api.get("leaderboards").get();
            JsonObject body = Managers.Json.toJsonObject(res.body());
            configObject = body.get("leaderboards").getAsJsonArray();
            MonumentaScraper.LOGGER.info("{}", configObject);
        } catch (Exception e) {
            configObject = new JsonArray();
            JsonObject base = new JsonObject();
            base.addProperty("leaderboardName", "Zenith Clears");
            base.addProperty("leaderboardId", "Zenith");
            base.addProperty("pages", 5);
            configObject.add(base);
        }
        Managers.Json.saveJsonAsFile(configFile, configObject);
        for (JsonElement scraper : configObject.asList()) {
            try {
                JsonObject scraperObject = scraper.getAsJsonObject();
                registerScraper(new LeaderboardScraper(scraperObject.get("leaderboardName")
                        .getAsString(), scraperObject.get("leaderboardId").getAsString(),
                        25));
            } catch (Exception e) {
                MonumentaScraper.LOGGER.warn("skipping malformed scraper {} for reason {}", scraper, e.getMessage());
            }
        }

    }

    private void registerScraper(Scraper scraper) {
        ++uncompletedScrapers;
        scrapers.add(scraper);
    }

    public void runScrapers() {
        runNext();
    }

    private void runNext() {
        if (scrapers.isEmpty()) {
            saveFile();
            return;
        }
        currentScraper = scrapers.remove(scrapers.size() - 1);
        initializeScraper(currentScraper);
    }

    private void initializeScraper(Scraper scraper) {
        scraper.run();
    }

    private void writeData(String title, JsonObject data) {
        --uncompletedScrapers;
        // title should be snapshots for things added to snapshots json array, which is
        // everything right now
        snapshots.add(data);
        if (uncompletedScrapers == 0) {
            saveFile();
        } else {
            runNext();
        }
    }

    private void saveFile() {
        if (allDone)
            return;
        allDone = true;
        Managers.Json.saveJsonAsFile(dataFile, dataObject);
        Managers.Api.post("leaderboards/snapshot", dataObject).whenComplete((res, exception) -> {
            MonumentaScraper.LOGGER.info("stuff: {} {}", res.body(), exception);
            ScraperEvents.ALL_DONE.invoker().allDone();
        });
    }

    public void onConnected(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!firstConnection && currentScraper != null) {
            currentScraper.onConnected();
        }
        firstConnection = false;
    }

    private void onDisconnected(ClientPlayNetworkHandler handler, MinecraftClient client) {
        if (currentScraper != null)
            currentScraper.onDisconnected();
    }

    private void onChatMessageReceived(Text message) {
        if (currentScraper != null)
            currentScraper.onChatMessageReceived(message);
    }

}
