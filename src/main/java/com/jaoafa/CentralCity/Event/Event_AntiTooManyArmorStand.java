package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.List;

public class Event_AntiTooManyArmorStand implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand)) {
            return;
        }

        if (!Main.isCentralCity(event.getLocation())) {
            return;
        }

        List<Entity> entitys = entity.getNearbyEntities(5, 5, 5);
        if (entitys.stream().filter(e -> e.getType() == EntityType.ARMOR_STAND).count() <= 5) {
            return;
        }
        event.setCancelled(true);
        Player min_player = getNearestPlayer(event.getLocation());
        if (min_player == null) {
            return;
        }

        min_player.sendMessage("[CentralCity] " + ChatColor.RED + "負荷対策の為に中央市内での防具立ての数を制限しています。ご協力をお願いします。");
    }

    Player getNearestPlayer(Location location) {
        double min = Double.MAX_VALUE;
        Player min_player = null;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Location location_p = player.getLocation();
            if (location.getWorld().getName().equals(location_p.getWorld().getName())) {
                double distance = location.distance(location_p);
                if (distance < min) {
                    min = distance;
                    min_player = player;
                }
            }
        }
        return min_player;
    }
}
