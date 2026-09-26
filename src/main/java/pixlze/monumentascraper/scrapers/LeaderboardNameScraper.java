package pixlze.monumentascraper.scrapers;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.Managers;
import pixlze.monumentascraper.scrapers.type.Scraper;
import pixlze.monumentascraper.scrapers.type.ScraperState;
import pixlze.monumentascraper.utils.McUtils;
import pixlze.monumentascraper.utils.text.TextUtils;
import pixlze.monumentascraper.utils.text.type.TextParseOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderboardNameScraper extends Scraper {
    private static final Pattern ROW_PATTERN = Pattern.compile(
            "^§. Leaderboard - §.(?<friendly>.*)$");
    private static final Pattern LEADERBOARD_END_PATTERN = Pattern.compile(
            "^§9§l--==-- +§.§l\\[ < ] *§e§l +Page: +§e§l(?<page>\\d+)/(?<maxPages>\\d+)§.§l +\\[ > ]§9§l *--==--$");

    private final int pages;
    private int currentPage;

    private final String command;

    private final JsonObject data;

    public LeaderboardNameScraper(String leaderboardId) {
        super("leaderboardNames");
        this.pages = 1;
        this.currentPage = 1;
        this.data = new JsonObject();
        this.data.addProperty("leaderboardId", leaderboardId);
        this.command = "leaderboard @s " + leaderboardId + " true ";
    }

    @Override
    public void fetchData() {
        this.setState(ScraperState.READY);
        fireCommand();
    }

    private void fireCommand() {
        if (this.state != ScraperState.READY) {
            MonumentaScraper.LOGGER.warn("scraper not ready");
            return;
        }
        try {
            McUtils.mc().getNetworkHandler().sendChatCommand(command + currentPage);
            this.setState(ScraperState.LISTENING);
        } catch (Exception e) {
            MonumentaScraper.LOGGER.warn("command fire error {} {}", e, e.getMessage());
        }
    }

    private void nextCommand() {
        ++this.currentPage;
        if (this.currentPage > this.pages) {
            postData(this.data);
        } else {
            this.setState(ScraperState.WAITING);
            Managers.Tick.scheduleLater(() -> {
                this.setState(ScraperState.READY);
                fireCommand();
            }, 7);
        }
    }

    @Override
    public synchronized void onChatMessageReceived(Text message) {
        if (this.state != ScraperState.LISTENING)
            return;

        String m = TextUtils.parseStyled(message, TextParseOptions.DEFAULT);
        Matcher rpMatcher = ROW_PATTERN.matcher(m);
        Matcher leaderboardEndMatcher = LEADERBOARD_END_PATTERN.matcher(m);
        if (rpMatcher.find()) {
            this.data.addProperty("leaderboardName", rpMatcher.group("friendly"));
            ;
        } else if (leaderboardEndMatcher.find()) {
            nextCommand();
        }
    }

    @Override
    public void onConnected() {
        if (this.currentPage > this.pages)
            return;
        Managers.Tick.scheduleLater(this::fireCommand, 10);
    }

    @Override
    public void onDisconnected() {
        this.setState(ScraperState.READY);
    }
}
