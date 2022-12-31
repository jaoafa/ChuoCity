package com.jaoafa.ChuoCity.Event;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Event_Inspect implements Listener {
    public static Set<UUID> inspects = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!inspects.contains(player.getUniqueId())) {
            return;
        }

        ItemStack is = player.getInventory().getItemInMainHand();
        if (is.getType() != Material.STICK) {
            return;
        }


        event.setCancelled(true);

        CoreProtectAPI cp = getCoreProtect();
        if (cp == null) {
            player.sendMessage("[Inspect] CoreProtectが見つかりませんでした。");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        // 12か月
        List<String[]> lookup = cp.blockLookup(block, 12 * 30 * 24 * 60 * 60);
        if (lookup != null && lookup.size() > 0) {
            for (String[] result : lookup.subList(0, Math.min(lookup.size(), 5))) {
                CoreProtectAPI.ParseResult parseResult = cp.parseResult(result);
                if(parseResult.isRolledBack()){
                   continue;
                }
                Date date = new Date(parseResult.getTimestamp());
                LocalDateTime actionDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                Duration duration = Duration.between(actionDateTime, now);
                player.sendMessage(Component.join(
                        JoinConfiguration.noSeparators(),
                        Component.text("[Inspect] ", NamedTextColor.GREEN),
                        Component.text(sdf.format(date))
                                .color(NamedTextColor.DARK_AQUA),
                        Component.space(),
                        Component.text("(%d日前)".formatted(duration.toDays()), NamedTextColor.GRAY)
                                .color(NamedTextColor.GRAY),
                        Component.space(),
                        Component.text(parseResult.getActionString()),
                        Component.space(),
                        Component.text("by"),
                        Component.space(),
                        Component.text(parseResult.getPlayer())
                                .color(NamedTextColor.DARK_AQUA),
                        Component.space(),
                        Component.text("(%d日前最終ログイン)".formatted(getDaysSinceLastLogin(parseResult.getPlayer())), NamedTextColor.GRAY)
                                .color(NamedTextColor.GRAY)
                ));
            }
        }else{
            player.sendMessage(Component.join(
                    JoinConfiguration.noSeparators(),
                    Component.text("[Inspect] ", NamedTextColor.GREEN),
                    Component.text("該当するログは見つかりませんでした。").color(NamedTextColor.GRAY))
            );
        }
    }

    private long getDaysSinceLastLogin(String playerName) {
        //noinspection deprecation
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        if (player.getLastLogin() == 0) {
            return 0;
        }
        return Duration.between(
                LocalDateTime.ofEpochSecond(player.getLastLogin() / 1000, 0, ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())),
                LocalDateTime.now()
        ).toDays();
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        if (CoreProtect.APIVersion() < 9) {
            return null;
        }

        return CoreProtect;
    }
}
