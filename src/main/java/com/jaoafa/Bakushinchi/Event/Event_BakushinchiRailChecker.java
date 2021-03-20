package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Event_BakushinchiRailChecker implements Listener {
    @EventHandler
    public void OnEvent_RailPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!event.canBuild()) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.RAILS && block.getType() != Material.ACTIVATOR_RAIL
                && block.getType() != Material.DETECTOR_RAIL && block.getType() != Material.POWERED_RAIL) {
            return;
        }

        if (!Main.isBakushinchi(block.getLocation())) {
            return;
        }

        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "爆新地での鉄道の敷設は禁止しています。見つかった場合、処罰の対象となりますのでご注意ください。");
    }
}
