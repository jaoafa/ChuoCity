package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class Event_Bamboo implements Listener {
    @EventHandler
    public void onBlockSpreadEvent(BlockSpreadEvent event) {
        if (!Main.isChuoCity(event.getBlock().getLocation())) {
            return;
        }
        if (event.getSource().getType() != Material.BAMBOO && event.getSource().getType() != Material.BAMBOO_SAPLING) {
            return;
        }
        event.setCancelled(true);
    }
}
