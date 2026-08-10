package pixlze.monumentascraper.managers;

public class Managers {
    public static final JsonManager Json = new JsonManager();
    public static final TickSchedulerManager Tick = new TickSchedulerManager();
    public static final ScraperManager Scraper = new ScraperManager();

    public static void init() {
        Json.init();
        Tick.init();
        Scraper.init();
    }
}
