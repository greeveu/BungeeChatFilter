package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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
}
