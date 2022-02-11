package com.jaoafa.CentralCity.Tasks;

import com.jaoafa.CentralCity.Main;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Task_CentralCityFlat extends BukkitRunnable {
    final Player player;
    final Region region;
    final int caseNum;

    public Task_CentralCityFlat(Player player, Region region, int caseNum) {
        this.player = player;
        this.region = region;
        this.caseNum = caseNum;
    }

    @Override
    public void run() {
        switch (caseNum) {
            case 0 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "y-64～y319を選択: //expand vert");
                boolean expand_vert = player.performCommand("/expand vert");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (expand_vert ? "成功" : "失敗"));
            }
            case 1 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "空気に変更: //set 0");
                boolean set_0 = player.performCommand("/set 0");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_0 ? "成功" : "失敗"));
            }
            case 2 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "岩盤範囲であるy-64～y-60を選択: //pos1,2 x,y,z");
                boolean bedrock_pos1 = player.performCommand(String.format("/pos1 %d,%d,%d", region.getMinimumPoint().getBlockX(), -64, region.getMinimumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos1 -> " + (bedrock_pos1 ? "成功" : "失敗"));
                boolean bedrock_pos2 = player.performCommand(String.format("/pos2 %d,%d,%d", region.getMaximumPoint().getBlockX(), -60, region.getMaximumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos2 -> " + (bedrock_pos2 ? "成功" : "失敗"));
            }
            case 3 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "岩盤に変更: //set bedrock");
                boolean set_bedrock = player.performCommand("/set bedrock");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_bedrock ? "成功" : "失敗"));
            }
            case 4 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "深層岩範囲であるy-59～y-1を選択: //pos1,2 x,y,z");
                boolean stone_pos1 = player.performCommand(String.format("/pos1 %d,%d,%d", region.getMinimumPoint().getBlockX(), -59, region.getMinimumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos1 -> " + (stone_pos1 ? "成功" : "失敗"));
                boolean stone_pos2 = player.performCommand(String.format("/pos2 %d,%d,%d", region.getMaximumPoint().getBlockX(), -1, region.getMaximumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos2 -> " + (stone_pos2 ? "成功" : "失敗"));
            }
            case 5 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "深層岩に変更: //set deepslate");
                boolean set_deepslate = player.performCommand("/set deepslate");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_deepslate ? "成功" : "失敗"));
            }
            case 6 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "石範囲であるy0～y59を選択: //pos1,2 x,y,z");
                boolean dirt_pos1 = player.performCommand(String.format("/pos1 %d,%d,%d", region.getMinimumPoint().getBlockX(), 0, region.getMinimumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos1 -> " + (dirt_pos1 ? "成功" : "失敗"));
                boolean dirt_pos2 = player.performCommand(String.format("/pos2 %d,%d,%d", region.getMaximumPoint().getBlockX(), 59, region.getMaximumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos2 -> " + (dirt_pos2 ? "成功" : "失敗"));
            }
            case 7 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "石に変更: //set stone");
                boolean set_stone = player.performCommand("/set stone");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_stone ? "成功" : "失敗"));
            }
            case 8 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "土範囲であるy63～y67を選択: //pos1,2 x,y,z");
                boolean dirt_pos1 = player.performCommand(String.format("/pos1 %d,%d,%d", region.getMinimumPoint().getBlockX(), 60, region.getMinimumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos1 -> " + (dirt_pos1 ? "成功" : "失敗"));
                boolean dirt_pos2 = player.performCommand(String.format("/pos2 %d,%d,%d", region.getMaximumPoint().getBlockX(), 66, region.getMaximumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos2 -> " + (dirt_pos2 ? "成功" : "失敗"));
            }
            case 9 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "土に変更: //set dirt");
                boolean set_dirt = player.performCommand("/set dirt");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_dirt ? "成功" : "失敗"));
            }
            case 10 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "草ブロック範囲であるy67を選択: //pos1,2 x,y,z");
                boolean dirt_pos1 = player.performCommand(String.format("/pos1 %d,%d,%d", region.getMinimumPoint().getBlockX(), 67, region.getMinimumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos1 -> " + (dirt_pos1 ? "成功" : "失敗"));
                boolean dirt_pos2 = player.performCommand(String.format("/pos2 %d,%d,%d", region.getMaximumPoint().getBlockX(), 67, region.getMaximumPoint().getBlockZ()));
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "pos2 -> " + (dirt_pos2 ? "成功" : "失敗"));
            }
            case 11 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "草ブロックに変更: //set grass_block");
                boolean set_dirt = player.performCommand("/set grass_block");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (set_dirt ? "成功" : "失敗"));
            }
            case 12 -> {
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "選択範囲の解除: //sel");
                boolean sel = player.performCommand("/sel");
                player.sendMessage("[CentralCity] " + ChatColor.GREEN + "-> " + (sel ? "成功" : "失敗"));
                return;
            }
        }
        new Task_CentralCityFlat(player, region, caseNum + 1).runTaskLater(Main.getJavaPlugin(), 20L);
    }
}

