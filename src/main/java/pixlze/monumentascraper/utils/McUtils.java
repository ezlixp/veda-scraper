package pixlze.monumentascraper.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class McUtils {
    public static String playerName() {
        return mc().getSession().getUsername();
    }

    public static String playerUUID() {
        return mc().getSession().getUuidOrNull().toString();
    }

    public static PlayerEntity player() {
        return mc().player;
    }

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
