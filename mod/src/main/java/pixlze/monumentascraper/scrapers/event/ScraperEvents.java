package pixlze.monumentascraper.scrapers.event;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ScraperEvents {
    public static final Event<Done> DONE = EventFactory.createArrayBacked(Done.class, (listeners) -> (title, data) -> {
        for (Done listener : listeners) listener.scraperDone(title, data);
    });
    public static final Event<AllDone> ALL_DONE = EventFactory.createArrayBacked(AllDone.class, (listeners) -> () -> {
        for (AllDone listener : listeners) listener.allDone();
    });

    public interface Done {
        void scraperDone(String title, JsonObject data);
    }

    public interface AllDone {
        void allDone();
    }
}
