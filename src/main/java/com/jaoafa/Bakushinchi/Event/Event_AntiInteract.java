package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.jaoafa.Bakushinchi.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Event_AntiInteract implements Listener {
    static Set<ItemInteract> itemInteracts = new HashSet<>();

    static {
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.EGG,
                "卵",
                "Egg",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT_ACACIA,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT_BIRCH,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT_DARK_OAK,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT_JUNGLE,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
        itemInteracts.add(new ItemInteract(
                new PermGroup[]{
                        PermGroup.DEFAULT,
                        PermGroup.VERIFIED
                },
                Material.BOAT_SPRUCE,
                "ボート",
                "Boat",
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
        ));
    }

    @EventHandler
    public void OnInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = event.getItem();
        if (hand == null) {
            return;
        }
        Action action = event.getAction();

        if (!Main.isBakushinchi(event.getClickedBlock().getLocation())) {
            return;
        }

        String group = PermissionsManager.getPermissionMainGroup(player);
        for (ItemInteract itemInteract : itemInteracts) {
            if (Arrays.stream(itemInteract.group).noneMatch(g -> group.equalsIgnoreCase(g.groupName))) {
                continue;
            }
            if (itemInteract.material != hand.getType()) {
                continue;
            }
            if (Arrays.stream(itemInteract.actions).noneMatch(a -> a == action)) {
                continue;
            }
            player.sendMessage(String.format("[BAKUSHINCHI] %s負荷対策の為に爆新地内での%sのアイテム使用を禁止しています。ご協力をお願いします。",
                    ChatColor.GREEN, itemInteract.itemName));
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                String _group = PermissionsManager.getPermissionMainGroup(p);
                if (_group.equalsIgnoreCase("Admin") || _group.equalsIgnoreCase("Moderator")) {
                    p.sendMessage(String.format("[%sNo%s%s] %s%sが%sを使用しましたが、規制しました。",
                            ChatColor.RED, itemInteract.itemNameEN, ChatColor.WHITE, ChatColor.GREEN, player.getName(), itemInteract.itemName));
                }
            }
            event.setCancelled(true);
        }
    }


    enum PermGroup {
        DEFAULT("Default"),
        VERIFIED("Verified"),
        REGULAR("Regular"),
        MODERATOR("Moderator"),
        ADMIN("Admin");

        String groupName;

        PermGroup(String groupName) {
            this.groupName = groupName;
        }
    }

    static class ItemInteract {
        PermGroup[] group;
        Material material;
        String itemName;
        String itemNameEN;
        Action[] actions;

        public ItemInteract(PermGroup[] group, Material material, String itemName, String itemNameEN, Action... actions) {
            this.group = group;
            this.material = material;
            this.itemName = itemName;
            this.itemNameEN = itemNameEN;
            this.actions = actions;
        }
    }
}
