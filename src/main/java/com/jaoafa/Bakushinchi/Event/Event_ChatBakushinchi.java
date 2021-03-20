package com.jaoafa.Bakushinchi.Event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Event_ChatBakushinchi implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (!message.contains("爆心地")) {
            return;
        }
        message = message.replaceAll("爆心地", "爆新地");
        event.setMessage(message);
    }
}
