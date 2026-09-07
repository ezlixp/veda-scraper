package pixlze.monumentascraper.managers;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.core.SafeExecutor;
import pixlze.monumentascraper.managers.type.Manager;

public class ApiManager implements Manager {
    private static final File CONFIG_DIR = MonumentaScraper.getStorageDirectory("config");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final File apiFile;
    private String baseUrl;
    private final String extra = "api/v1/";
    private String validationKey;

    public ApiManager() {
        apiFile = new File(CONFIG_DIR, "api.json");
    }

    public void init() {
        SafeExecutor.run(() -> {
            JsonObject config = Managers.Json.loadJsonFromFile(apiFile).getAsJsonObject();
            baseUrl = config.get("baseUrl").getAsString();
            validationKey = config.get("validationKey").getAsString();
        }, "Couldn't get API config");
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
