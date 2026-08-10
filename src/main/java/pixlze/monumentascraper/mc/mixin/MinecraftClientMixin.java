package pixlze.monumentascraper.mc.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public abstract @Nullable Overlay getOverlay();

    @Unique
    private boolean autoJoin$hasTriggeredJoin = false;
    @Unique
    private boolean autoJoin$reconnecting = false;
    @Unique
    private int autoJoin$reconnectingTicks = 0;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public ClientWorld world;
    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow
    private volatile boolean running;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (getOverlay() != null) {
//            MonumentaScraper.LOGGER.info("Waiting for splash overlay to disappear...");
            return;
        }

        if (!autoJoin$hasTriggeredJoin) {
            if (currentScreen instanceof TitleScreen || currentScreen == null) {
                autoJoin$hasTriggeredJoin = true;

                ServerAddress serverAddress = ServerAddress.parse("server.playmonumenta.com");
                ConnectScreen.connect(new TitleScreen(), MinecraftClient.class.cast(this), serverAddress, new ServerInfo("monumenta", serverAddress.getAddress(), ServerInfo.ServerType.OTHER), false);
            }
        } else {
            if (currentScreen instanceof DisconnectedScreen && !autoJoin$reconnecting) {
                if (autoJoin$reconnectingTicks < 40) {
                    ++autoJoin$reconnectingTicks;
                } else {
                    autoJoin$reconnectingTicks = -40;

                    ServerAddress serverAddress = ServerAddress.parse("server.playmonumenta.com");
                    ConnectScreen.connect(new TitleScreen(), MinecraftClient.class.cast(this), serverAddress, new ServerInfo("monumenta", serverAddress.getAddress(), ServerInfo.ServerType.OTHER), false);
                }
            } else if (player != null) {
                autoJoin$reconnectingTicks = 0;
            }
        }
    }
}
