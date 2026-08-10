package pixlze.monumentascraper.mc.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

public interface MonumentaChatMessage {
    Event<MonumentaChatMessage> EVENT = EventFactory.createArrayBacked(MonumentaChatMessage.class, (listeners) -> (message) -> {
        for (MonumentaChatMessage listener : listeners) {
            listener.interact(message);
        }
    });

    void interact(Text message);
}
