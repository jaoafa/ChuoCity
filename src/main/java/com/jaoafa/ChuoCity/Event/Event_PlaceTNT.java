package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import com.jaoafa.ChuoCity.PermissionsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class Event_PlaceTNT implements Listener {
    @EventHandler
    public void onPlaceTNT(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (block.getType() != Material.TNT) {
            return;
        }

        if (!Main.isChuoCity(loc)) {
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

    @EventHandler
    public void onDispenseTNT(BlockDispenseEvent event){
        Location loc = event.getBlock().getLocation();
        ItemStack is = event.getItem();

        if (is.getType() != Material.TNT) {
            return;
        }

        if (!Main.isChuoCity(loc)) {
            return;
        }
        BlockFace face = ((Directional) event.getBlock().getBlockData()).getFacing();
        Block spawnBlock = event.getBlock().getRelative(face);

        event.setCancelled(true);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, spawnBlock.getLocation().add(0.5, 0.5, 0.5), 1);
        loc.getWorld().playSound(spawnBlock.getLocation().add(0.5, 0.5, 0.5), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.8f);
    }
}
