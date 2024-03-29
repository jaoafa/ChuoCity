package com.jaoafa.ChuoCity.Tasks;

import com.jaoafa.ChuoCity.Main;
import com.jaoafa.jaosuperachievement2.api.Achievementjao;
import com.jaoafa.jaosuperachievement2.lib.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Task_NewStep extends BukkitRunnable {
    final Set<UUID> got = new HashSet<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (got.contains(player.getUniqueId())) {
                continue;
            }
            Location loc = player.getLocation();
            if (Main.isChuoCity(loc)) {
                continue;
            }
            Achievementjao.getAchievementAsync(player, Achievement.NEWSTEP);
            got.add(player.getUniqueId());
        }
    }
}
