package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.jaoafa.Bakushinchi.PermissionsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Event_PlaceTNT implements Listener {
    @EventHandler
    public void onPlaceTNT(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (block.getType() != Material.TNT) {
            return;
        }

        if (!Main.isBakushinchi(loc)) {
            return;
        }

        String group = PermissionsManager.getPermissionMainGroup(player);
        if (!group.equalsIgnoreCase("Default") && !group.equalsIgnoreCase("Verified")
            && !group.equalsIgnoreCase("Regular")) {
            return;
        }
        event.setCancelled(true);
        block.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation().add(0.5, 0.5, 0.5), 1);
        block.getWorld().playSound(block.getLocation().add(0.5, 0.5, 0.5), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.8f);
        //block.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4F, false, false);
    }
}
