package com.jaoafa.Bakushinchi.Event;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Event_ChatBakushinchi implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncChatEvent event) {
        Component message = event.message();
        if (!message.contains(Component.text("爆心地"))) {
            return;
        }
        message = message.replaceText(TextReplacementConfig.builder().matchLiteral("爆心地").replacement("爆新地").build());
        event.message(message);
    }
}
