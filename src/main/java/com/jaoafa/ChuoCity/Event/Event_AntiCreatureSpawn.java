package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import com.jaoafa.ChuoCity.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashSet;
import java.util.Set;

public class Event_AntiCreatureSpawn implements Listener {
    static final Set<SpawnEntityType> spawnEntityTypes = new HashSet<>();

    static {
        spawnEntityTypes.add(new SpawnEntityType(
            EntityType.IRON_GOLEM,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            "アイアンゴーレム",
            "IronGolem")
        );
        spawnEntityTypes.add(new SpawnEntityType(
            EntityType.SNOWMAN,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            "スノウゴーレム",
            "SnowMan")
        );
    }

    @EventHandler
    public void CreatureSpawn(CreatureSpawnEvent event) {
        if (!Main.isChuoCity(event.getLocation())) {
            return;
        }
        LivingEntity ent = event.getEntity();

        for (SpawnEntityType spawnEntityType : spawnEntityTypes) {
            if (ent.getType() != spawnEntityType.entityType) {
                continue;
            }
            if (spawnEntityType.spawnReason == null || event.getSpawnReason() != spawnEntityType.spawnReason) {
                continue;
            }
            event.setCancelled(true);

            Location location = event.getLocation();
            Player min_player = getNearestPlayer(location);
            if (min_player == null) {
                return;
            }
            min_player.sendMessage(String.format("[ChuoCity] %s負荷対策の為に中央市内での%sの召喚を禁止しています。ご協力をお願いします。",
                ChatColor.GREEN, spawnEntityType.entityName));
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                String group = PermissionsManager.getPermissionMainGroup(p);
                if (group.equalsIgnoreCase("Admin") || group.equalsIgnoreCase("Moderator")) {
                    p.sendMessage(String.format("[%sNo%s%s] %s%sの近くで%sが発生しましたが、発生を規制されました。(%s %d %d %d)",
                        ChatColor.RED,
                        spawnEntityType.entityNameEN,
                        ChatColor.WHITE,
                        ChatColor.GREEN,
                        min_player.getName(),
                        spawnEntityType.entityName,
                        location.getWorld().getName(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ()));
                }
            }
        }
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

    static class SpawnEntityType {
        final EntityType entityType;
        final CreatureSpawnEvent.SpawnReason spawnReason;
        final String entityName;
        final String entityNameEN;

        public SpawnEntityType(EntityType entityType, CreatureSpawnEvent.SpawnReason spawnReason, String entityName, String entityNameEN) {
            this.entityType = entityType;
            this.spawnReason = spawnReason;
            this.entityName = entityName;
            this.entityNameEN = entityNameEN;
        }
    }
}
