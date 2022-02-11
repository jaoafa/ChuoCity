package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class Event_AntiDiffusionWaterLava implements Listener {
    @EventHandler
    public void OnDiffusionWaterLava(BlockFromToEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Block newBlock = event.getToBlock();
        Location newLoc = newBlock.getLocation();

        if (!Main.isCentralCity(loc)) {
            return;
        }

        if (loc.equals(newLoc)) {
            return;
        }

        newLoc.getWorld().spawnParticle(Particle.SPELL_WITCH, newLoc.add(0.5, 0, 0.5), 100, 0.5, 0, 0.5);
        event.setCancelled(true);
    }
}