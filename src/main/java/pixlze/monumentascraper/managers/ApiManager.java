package pixlze.monumentascraper.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.type.Manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiManager extends Manager {
    private static final File CONFIG_DIR = MonumentaScraper.getModStorageDir("config");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final File apiFile;
    private String baseUrl;
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
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "bearer " + validationKey)
                .GET();
        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> post(String path, JsonObject body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "bearer " + validationKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
