package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import com.jaoafa.ChuoCity.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Event_AntiBlockUnderDestroy implements Listener {
    public static final Map<UUID, Location> destroy = new HashMap<>();
    public static final Map<UUID, Integer> destroycount = new HashMap<>();
    public static final Map<UUID, Boolean> destroyAlerted = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAntiBlockUnderDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        if (!Main.isChuoCity(loc)) {
            return;
        }

        if (y > 67) {
            return;
        }

        String group = PermissionsManager.getPermissionMainGroup(player);
        int destroyOK;
        if (group.equalsIgnoreCase("Default")) {
            destroyOK = 3;
        } else if (group.equalsIgnoreCase("Verified")) {
            destroyOK = 5;
        } else {
            return; // QD以外は特に規制設けない
        }

        if (!destroy.containsKey(uuid)) {
            destroy.put(uuid, loc);
            destroycount.put(uuid, 1);
            destroyAlerted.put(uuid, false);
            return;
        }

        int oldx = destroy.get(uuid).getBlockX();
        int oldy = destroy.get(uuid).getBlockY();
        int oldz = destroy.get(uuid).getBlockZ();

        if (x != oldx || z != oldz) {
            destroy.put(uuid, loc);
            destroycount.put(uuid, 0);
            destroyAlerted.put(uuid, false);
            return;
        }

        if (oldy <= y) {
            // 前回掘ったときよりも上(その高さを含む)
            destroy.put(uuid, loc);
            destroycount.put(uuid, 0);
            destroyAlerted.put(uuid, false);
            return;
        }

        if ((oldy - y) <= 1 && (oldy - y) >= 3) {
            // oldYからYを引いた数(どれだけ下か)は、1ブロックから3ブロック以内ではないか
            destroy.put(uuid, loc);
            destroycount.put(uuid, 0);
            destroyAlerted.put(uuid, false);
            return; // じゃないなら判定しない
        }

        if (!destroycount.containsKey(uuid)) {
            destroy.put(uuid, loc);
            destroycount.put(uuid, 1);
            destroyAlerted.put(uuid, false);
            return;
        }

        int nowdestroyCount = destroycount.get(uuid);
        if (nowdestroyCount <= destroyOK) {
            // 以内だったらカウントアップしておわり
            destroy.put(uuid, loc);
            destroycount.put(uuid, nowdestroyCount + 1);
            destroyAlerted.put(uuid, false);
            return;
        }

        // アウト？
        // カウント処理とか座標処理とかあえてしない。
        player.sendMessage("[BlockDestroy] " + ChatColor.RED + "荒らし対策のため、ブロックの直下掘りは禁止されています。");
        player.sendMessage("[BlockDestroy] " + ChatColor.RED + "修復が見られない場合、荒らしとして処罰される場合があります。");
        if (destroyAlerted.containsKey(uuid) && !destroyAlerted.get(uuid)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String _group = PermissionsManager.getPermissionMainGroup(p);
                if (_group.equalsIgnoreCase("Regular") || _group.equalsIgnoreCase("Moderator")
                    || _group.equalsIgnoreCase("Admin")) {
                    p.sendMessage("[AntiBlockUnderDestroy] " + ChatColor.RED + "プレイヤー「" + player.getName() + "」が直下掘りを"
                        + world.getName() + "の" + x + " " + y + " " + z + "で行いました。");
                }
                Main.getMain().getLogger().info("[AntiBlockUnderDestroy] プレイヤー「" + player.getName() + "」が直下掘りを"
                    + world.getName() + "の" + x + " " + y + " " + z + "で行いました。");
            }
        }
        destroyAlerted.put(uuid, true);
        event.setCancelled(true);
    }
}