package com.jaoafa.Bakushinchi;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PermissionsManager implements Listener {
    /**
     * PermissionsExのステータス
     */
    private static boolean PermissionsExStatus;
    /**
     * LuckPermsのステータス
     */
    private static boolean LuckPermsStatus;

    private static PermissionsPlugin SelectPermissionsPlugin = null;

    public static void first() {
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    /**
     * 権限管理プラグインの動作状態を確認し、使用するプラグインを自動選択する
     *
     * @throws UnsupportedOperationException 権限管理プラグインが見つからない場合に発生
     */
    public static void checkPermissionsPluginAutoSelecter() throws UnsupportedOperationException {
        Bukkit.getLogger().info("権限管理プラグインの自動選択を行います。");

        Plugin PermissionsEx = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        Plugin LuckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");

        if (PermissionsEx != null && PermissionsEx.isEnabled()) {
            PermissionsExStatus = true;
            Bukkit.getLogger().info("PermissionsExが動作しています。");
        }
        if (LuckPerms != null && LuckPerms.isEnabled()) {
            LuckPermsStatus = true;
            Bukkit.getLogger().info("LuckPermsが動作しています。");
        }

        if (PermissionsExStatus) {
            // PermissionsExが動作している
            SelectPermissionsPlugin = PermissionsPlugin.PermissionsEx; // PermissionsExを使う。
            Bukkit.getLogger().info("PermissionsExを自動選択しました！");
        } else if (LuckPermsStatus) {
            // LuckPermsが動作している
            SelectPermissionsPlugin = PermissionsPlugin.LuckPerms; // PermissionsExを使う。
            Bukkit.getLogger().info("LuckPermsを自動選択しました！");
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが見つからないため、自動選択ができません！");
        }
    }

    /**
     * 権限管理プラグインの動作状態を確認する
     */
    public static boolean checkPermissionsPlugin() throws UnsupportedOperationException {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        Plugin PermissionsEx = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        Plugin LuckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            return (PermissionsEx != null && PermissionsEx.isEnabled());
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            return (LuckPerms != null && LuckPerms.isEnabled());
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
        }
    }

    /**
     * 指定されたプレイヤーのメイン権限グループを取得します。
     *
     * @param player プレイヤー
     * @return メイン権限グループ名
     * @throws UnsupportedOperationException UnsupportedOperationException
     * @throws IllegalArgumentException      IllegalArgumentException
     * @throws InternalError                 InternalError
     */
    public static String getPermissionMainGroup(Player player)
            throws UnsupportedOperationException, IllegalArgumentException, InternalError {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (player == null)
            throw new InternalError("オンラインプレイヤーがnullです。");

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            String groupname = LPplayer.getPrimaryGroup();
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null)
                throw new InternalError("Groupがnullです。");
            return group.getFriendlyName();
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが選択されていません！");
        }
    }

    public static boolean isSelectPermissionsPlugin(PermissionsPlugin plugin) {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        return plugin == SelectPermissionsPlugin;
    }

    @EventHandler
    public void PluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (!plugin.getName().equalsIgnoreCase("PermissionsEx") || !plugin.getName().equalsIgnoreCase("LuckPerms")) {
            return;
        }
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    @EventHandler
    public void PluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (!plugin.getName().equalsIgnoreCase("PermissionsEx") || !plugin.getName().equalsIgnoreCase("LuckPerms")) {
            return;
        }
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    public enum PermissionsPlugin {
        PermissionsEx(), LuckPerms();

        PermissionsPlugin() {
        }
    }
}
