package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.Instant;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        if (player.hasPermission("bungeefilter.bypass")) {
            return;
        }

        if (Main.getInstance().isNorepeat()) {
            if (repeatCheck(player.getName(), e.getMessage())) {
                e.setCancelled(true);
                player.sendMessage(new TextComponent(ChatColor.RED + "Please do not spam"));
                return;
            } else {
                Main.getInstance().getAntirepeat().put(player.getName(), e.getMessage());
            }

        }

        if (Main.getInstance().isNospam()) {
            if (spamCheck(player)) {
                e.setCancelled(true);
                player.sendMessage(new TextComponent(ChatColor.RED + "Please do not spam"));
                return;
            } else {
                Main.getInstance().getAntispam().put(player.getName(), Instant.now());
            }
        }

        for (Rule rule : Main.getInstance().getRules()) {
            if (rule.getPermission() != null) {
                if (rule.getPermissionType() == PermissionType.BYPASS && player.hasPermission(rule.getPermission())) {
                    return;
                }
                if (rule.getPermissionType() == PermissionType.REQUIRED && !player.hasPermission(rule.getPermission())) {
                    return;
                }
            }
            if (rule.doesMessageContainRegex(e.getMessage())) {
                rule.performActions(e, player);
            }
        }
    }

    private boolean repeatCheck(String name, String message) {
        if (Main.getInstance().getAntirepeat().containsKey(name)) {
            return Main.getInstance().getAntirepeat().get(name).equals(message);
        }
        return false;
    }

    private boolean spamCheck(ProxiedPlayer player) {
        if (Main.getInstance().getAntispam().containsKey(player.getName())) {
            return Main.getInstance().getAntispam().get(player.getName())
                    .plusSeconds(Main.getInstance().getSpamtimer())
                    .isAfter(Instant.now());
        }
        return false;
    }

    @EventHandler
    public void playerLogOut(PlayerDisconnectEvent e) {
        Main.getInstance().getAntispam().remove(e.getPlayer().getName());
        Main.getInstance().getAntirepeat().remove(e.getPlayer().getName());
    }
}
