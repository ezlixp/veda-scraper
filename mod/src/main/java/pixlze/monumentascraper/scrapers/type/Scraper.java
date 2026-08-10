package pixlze.monumentascraper.scrapers.type;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.scrapers.event.ScraperEvents;

public abstract class Scraper {
    private final String category;
    private boolean initialized = false;
    protected ScraperState state = ScraperState.WAITING;

    public Scraper(String category) {
        this.category = category;
    }

    public void run() {
        if (initialized) {
            MonumentaScraper.LOGGER.warn("Scraper is already completed!");
            return;
        }
        initialized = true;
        fetchData();
    }

    /**
     * Start fetching data. Eventually, postData must be called.
     */
    public abstract void fetchData();

    public void postData(JsonObject data) {
        if (this.state == ScraperState.DONE) return;
        setState(ScraperState.DONE);
        ScraperEvents.DONE.invoker().scraperDone(category, data);
    }

    public void setState(ScraperState state) {
        this.state = state;
    }

    public abstract void onConnected();

    public abstract void onDisconnected();

    public abstract void onChatMessageReceived(Text message);
}
