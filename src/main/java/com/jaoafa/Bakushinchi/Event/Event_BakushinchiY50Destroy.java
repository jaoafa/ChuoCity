package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.jaoafa.Bakushinchi.PermissionsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Event_BakushinchiY50Destroy implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAntiBlockUnderDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        int y = loc.getBlockY();

        if (!Main.isBakushinchi(loc)) {
            return;
        }

        if (y > 50) {
            return;
        }

        String group = PermissionsManager.getPermissionMainGroup(player);
        if (!group.equalsIgnoreCase("Default") && !group.equalsIgnoreCase("Verified")) {
            return; // QD以外は特に規制設けない
        }
        player.sendMessage("[BlockDestroy] " + ChatColor.RED
                + "直下掘り制限を回避したりするプレイヤーの影響により、Default・Verified権限によるY50未満の採掘はできなくなりました。");
        event.setCancelled(true);
    }
}
