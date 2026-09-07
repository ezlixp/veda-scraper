package pixlze.monumentascraper.managers;

import com.google.gson.*;
import pixlze.monumentascraper.core.SafeExecutor;
import pixlze.monumentascraper.managers.type.Manager;
import pixlze.monumentascraper.utils.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonManager implements Manager {
    public final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public JsonManager() {
    }

    public void init() {
    }

    public JsonElement loadJsonFromFile(File file) {
        return SafeExecutor.run(() -> {
            try (FileReader reader = new FileReader(file)) {
                return JsonParser.parseReader(reader);
            }
        }, String.format("Error loading JSON"));
    }

    public void saveJsonAsFile(File file, JsonElement json) {
        SafeExecutor.run(() -> {
            FileUtils.mkdir(file.getParentFile());
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(json, writer);
            }
        }, "Error writing JSON");
    }

    public JsonElement toJsonElement(String convert) {
        return GSON.fromJson(convert, JsonElement.class);
    }

    public JsonObject toJsonObject(String convert) {
        return GSON.fromJson(convert, JsonObject.class);
    }

    public String escapeUnsafeJsonChars(String input) {
        if (input == null)
            return null;

        String out = input.replace("\\", "\\\\");
        out = out.replace("\"", "\\\"");
        out = out.replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");

        return out;
    }
}
