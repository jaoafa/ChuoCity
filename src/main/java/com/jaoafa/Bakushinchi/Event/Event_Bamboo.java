package com.jaoafa.Bakushinchi.Event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class Event_Bamboo implements Listener {
    @EventHandler
    public void onBlockSpreadEvent(BlockSpreadEvent event){
        if(event.getBlock().getType() != Material.BAMBOO){
            return;
        }
        event.setCancelled(true);
    }
}
