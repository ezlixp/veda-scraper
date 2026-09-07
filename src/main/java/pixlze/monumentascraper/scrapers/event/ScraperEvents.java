package pixlze.monumentascraper.scrapers.event;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ScraperEvents {
    public interface Done {
        void scraperDone(String title, JsonObject data);
    }

    public interface AllDone {
        void allDone();
    }

    public static final Event<Done> DONE = EventFactory.createArrayBacked(
            Done.class,
            ScraperEvents::onDone);

    public static final Event<AllDone> ALL_DONE = EventFactory.createArrayBacked(
            AllDone.class,
            ScraperEvents::onAllDone);

    private static Done onDone(Done[] listeners) {
        return (title, data) -> {
            for (var listener : listeners) {
                listener.scraperDone(title, data);
            }
        };
    }

    private static AllDone onAllDone(AllDone[] listeners) {
        return () -> {
            for (var listener : listeners) {
                listener.allDone();
            }
        };
    }
}
