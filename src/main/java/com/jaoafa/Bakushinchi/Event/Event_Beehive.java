package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Event_Beehive implements Listener {
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (!Main.isBakushinchi(event.getLocation())) {
            return;
        }
        if (event.getEntity().getType() != EntityType.BEE) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BEEHIVE) {
            return;
        }
        event.setCancelled(true);
    }
}
