package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.jaoafa.Bakushinchi.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class Event_AntiSnowMan implements Listener {
    @EventHandler
    public void CreatureSpawn(CreatureSpawnEvent event) {
        if (!Main.isBakushinchi(event.getLocation())) {
            return;
        }

        LivingEntity ent = event.getEntity();
        if ((ent.getType() == EntityType.SNOWMAN) &&
                (event.getSpawnReason() == SpawnReason.BUILD_SNOWMAN)) {
            Location location = event.getLocation();
            double min = 1.79769313486231570E+308;
            Player min_player = null;
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                org.bukkit.Location location_p = player.getLocation();
                if (location.getWorld().getName().equals(location_p.getWorld().getName())) {
                    double distance = location.distance(location_p);
                    if (distance < min) {
                        min = distance;
                        min_player = player;
                    }
                }
            }
            if (min_player == null) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            min_player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "負荷対策の為に爆新地内でのスノウゴーレムの召喚を禁止しています。ご協力をお願いします。");
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                String group = PermissionsManager.getPermissionMainGroup(p);
                if (group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator")) {
                    p.sendMessage("[" + ChatColor.RED + "NoSnowMan" + ChatColor.WHITE + "] " + ChatColor.GREEN
                            + min_player.getName() + "の近くでスノウゴーレムが発生しましたが、発生を規制されました。");
                }
            }
        }
    }
}
