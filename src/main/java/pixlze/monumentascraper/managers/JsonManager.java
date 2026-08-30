package pixlze.monumentascraper.managers;

import com.google.gson.*;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.type.Manager;
import pixlze.monumentascraper.utils.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonManager extends Manager {
    public final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().serializeNulls()
            .create();

    public JsonManager() {
        super(List.of());
    }

    @Override
    public void init() {
    }

    public JsonElement loadJsonFromFile(File file) {
        JsonElement element;
        try (FileReader reader = new FileReader(file)) {
            element = JsonParser.parseReader(reader);

            return element;
        } catch (IOException e) {
            MonumentaScraper.LOGGER.error("json load error: {} {}", e, e.getMessage());
        }

        return null;
    }

    public boolean saveJsonAsFile(File file, JsonElement json) {
        FileUtils.mkdir(file.getParentFile());
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            MonumentaScraper.LOGGER.error("json write error: {} {}", e, e.getMessage());

            return false;
        }

        return true;
    }

    public JsonElement toJsonElement(String convert) {
        return GSON.fromJson(convert, JsonElement.class);
    }

    public JsonObject toJsonObject(String convert) {
        return GSON.fromJson(convert, JsonObject.class);
    }

    public String escapeUnsafeJsonChars(String input) {
        if (input == null) {
            return null;
        }
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
