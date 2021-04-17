package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.Arrays;
import java.util.List;

public class Event_DisableHalfBlockWater implements Listener {
    List<Material> materials = Arrays.asList(
        Material.OAK_SLAB,
        Material.SPRUCE_SLAB,
        Material.BIRCH_SLAB,
        Material.JUNGLE_SLAB,
        Material.ACACIA_SLAB,
        Material.DARK_OAK_SLAB,
        Material.CRIMSON_SLAB,
        Material.WARPED_SLAB,
        Material.STONE_SLAB,
        Material.SMOOTH_STONE_SLAB,
        Material.SANDSTONE_SLAB,
        Material.CUT_SANDSTONE_SLAB,
        Material.PETRIFIED_OAK_SLAB,
        Material.COBBLESTONE_SLAB,
        Material.BRICK_SLAB,
        Material.STONE_BRICK_SLAB,
        Material.NETHER_BRICK_SLAB,
        Material.QUARTZ_SLAB,
        Material.RED_SANDSTONE_SLAB,
        Material.CUT_RED_SANDSTONE_SLAB,
        Material.PURPUR_SLAB,
        Material.PRISMARINE_SLAB,
        Material.PRISMARINE_BRICK_SLAB,
        Material.DARK_PRISMARINE_SLAB,
        Material.POLISHED_GRANITE_SLAB,
        Material.SMOOTH_RED_SANDSTONE_SLAB,
        Material.MOSSY_STONE_BRICK_SLAB,
        Material.POLISHED_DIORITE_SLAB,
        Material.MOSSY_COBBLESTONE_SLAB,
        Material.END_STONE_BRICK_SLAB,
        Material.SMOOTH_SANDSTONE_SLAB,
        Material.SMOOTH_QUARTZ_SLAB,
        Material.GRANITE_SLAB,
        Material.ANDESITE_SLAB,
        Material.RED_NETHER_BRICK_SLAB,
        Material.POLISHED_ANDESITE_SLAB,
        Material.DIORITE_SLAB
    );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        if (!Main.isBakushinchi(player.getLocation())) {
            return;
        }

        if (!materials.contains(block.getType())) {
            return;
        }

        event.setCancelled(true);
    }
}
