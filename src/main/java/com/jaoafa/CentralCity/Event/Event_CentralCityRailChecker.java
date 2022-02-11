package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Event_CentralCityRailChecker implements Listener {
    @EventHandler
    public void OnEvent_RailPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!event.canBuild()) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.RAIL && block.getType() != Material.ACTIVATOR_RAIL
            && block.getType() != Material.DETECTOR_RAIL && block.getType() != Material.POWERED_RAIL) {
            return;
        }

        if (!Main.isCentralCity(block.getLocation())) {
            return;
        }

        player.sendMessage("[CentralCity] " + ChatColor.GREEN + "中央市での鉄道の敷設は禁止しています。見つかった場合、処罰の対象となりますのでご注意ください。");
    }
}
