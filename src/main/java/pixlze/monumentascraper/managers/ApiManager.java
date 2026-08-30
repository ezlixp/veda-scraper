package pixlze.monumentascraper.managers;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.type.Manager;

public class ApiManager extends Manager {
    private static final File CONFIG_DIR = MonumentaScraper.getStorageDirectory("config");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final File apiFile;
    private String baseUrl;
    private final String extra = "v1/api/";
    private String validationKey;

    public ApiManager() {
        super(List.of());
        apiFile = new File(CONFIG_DIR, "api.json");
    }

    @Override
    public void init() {
        try {
            JsonObject config = Managers.Json.loadJsonFromFile(apiFile).getAsJsonObject();
            baseUrl = config.get("baseUrl").getAsString();
            validationKey = config.get("validationKey").getAsString();
        } catch (Exception e) {
            MonumentaScraper.LOGGER.warn("couldn't get api stuffs");
        }
    }

    public CompletableFuture<HttpResponse<String>> get(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + extra + path))
                .header("Authorization", "bearer " + validationKey)
                .GET();
        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> post(String path, JsonObject body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + extra + path))
                .header("Authorization", "bearer " + validationKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        if (MonumentaScraper.CONFIG.isDevelopmentEnvironment())
            builder.version(HttpClient.Version.HTTP_1_1);

        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
