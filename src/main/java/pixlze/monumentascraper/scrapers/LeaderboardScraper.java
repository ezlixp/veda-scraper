package pixlze.monumentascraper.scrapers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import pixlze.monumentascraper.MonumentaScraper;
import pixlze.monumentascraper.managers.Managers;
import pixlze.monumentascraper.scrapers.type.Scraper;
import pixlze.monumentascraper.scrapers.type.ScraperState;
import pixlze.monumentascraper.utils.McUtils;
import pixlze.monumentascraper.utils.text.TextUtils;
import pixlze.monumentascraper.utils.text.type.TextParseOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderboardScraper extends Scraper {
    private static final Pattern ROW_PATTERN = Pattern.compile("^(§[^9]§l)(?<rank>\\d+)§[^9](§l\\s+(§[^9])?)?\\s*((§[^9])?§l)(?<username>\\S+?)§[^9](§l\\s*§[^9])?\\s*(§[^9]§l)(?<value>\\d+)$");
    private static final Pattern LEADERBOARD_END_PATTERN = Pattern.compile("^§9§l--==-- +§.§l\\[ < ] *§e§l +Page: +§e§l(?<page>\\d+)/(?<maxPages>\\d+)§.§l +\\[ > ]§9§l *--==--$");

    private final int pages;
    private int currentPage;

    private final String command;

    private final JsonObject data;
    private final JsonArray rankings;
    private final List<JsonObject> pageRankings = new ArrayList<>();

    public LeaderboardScraper(String leaderboardName, String leaderboardId, int pages) {
        super("snapshots");
        this.pages = pages;
        this.currentPage = 1;
        this.data = new JsonObject();
        this.data.addProperty("leaderboardName", leaderboardId);
        this.rankings = new JsonArray();
        this.data.add("entries", this.rankings);
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
            pageRankings.clear();
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
        if (this.state != ScraperState.LISTENING) return;

        String m = TextUtils.parseStyled(message, TextParseOptions.DEFAULT);
        Matcher rpMatcher = ROW_PATTERN.matcher(m);
        Matcher leaderboardEndMatcher = LEADERBOARD_END_PATTERN.matcher(m);
        if (rpMatcher.find()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("rank", Integer.parseInt(rpMatcher.group("rank")));
            entry.addProperty("playerName", rpMatcher.group("username"));
            entry.addProperty("value", Integer.parseInt(rpMatcher.group("value")));
            pageRankings.add(entry);
        } else if (leaderboardEndMatcher.find()) {
            for (JsonObject ranking : pageRankings) {
                rankings.add(ranking);
            }
            pageRankings.clear();
            nextCommand();
        }
    }

    @Override
    public void onConnected() {
        if (this.currentPage > this.pages) return;
        Managers.Tick.scheduleLater(this::fireCommand, 10);
    }

    @Override
    public void onDisconnected() {
        pageRankings.clear(); // technically redundant
        this.setState(ScraperState.READY);
    }
}
