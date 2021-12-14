package com.minecraftdimensions.bungeechatfilter;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {

    @Getter
    private final String permission;

    @Getter
    private final PermissionType permissionType;

    @Getter
    private final boolean ignoreCommands;

    private final Pattern regex;
    private final Pattern ignore;
    private final Pattern server;
    private final Map<String, String[]> actions;
    private final Random rand = new Random();

    public Rule(String regex,
                Map<String, String[]> actions,
                String permission,
                String ignores,
                String server,
                PermissionType permissionType,
                boolean ignoreCommands) {
        this.regex = Pattern.compile(regex);
        this.ignore = ignores == null ? null : Pattern.compile(ignores);
        this.server = server == null ? null : Pattern.compile(server);
        this.actions = actions;
        this.permission = permission;
        this.permissionType = permissionType;
        this.ignoreCommands = ignoreCommands;
    }

    public Matcher getMatcher(String message) {
        return regex.matcher(message);
    }

    public boolean doesMessageContainRegex(String message) {
        return getMatcher(message).find();
    }

    public void performActions(ChatEvent event, ProxiedPlayer player) {
        String message = event.getMessage();
        if (checkIgnored(message)) return;
        if (checkServer(player)) return;
        for (Map.Entry<String, String[]> action : actions.entrySet()) {
            switch (action.getKey()) {
                case "deny":
                    event.setCancelled(true);
                    break;
                case "message":
                    player.sendMessage(color(action.getValue()[0]));
                    break;
                case "kick":
                    player.disconnect(color(action.getValue()[0]));
                    break;
                case "alert":
                    broadcast(player, message, action);
                    break;
                case "scommand":
                    player.chat(parseVariables(action.getValue()[0], event));
                    break;
                case "pcommand":
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, parseVariables(action.getValue()[0], event));
                    break;
                case "ccommand":
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), parseVariables(action.getValue()[0], event));
                    break;
                case "remove":
                    message = message.replaceAll(regex.pattern(), "");
                    break;
                case "replace":
                    message = replace(message, action);
                    break;
                case "lower":
                    message = lowercase(message);
                    break;
                default:
                    break;
            }
        }
        event.setMessage(message);
    }

    private void broadcast(ProxiedPlayer player, String message, Map.Entry<String, String[]> action) {
        String alert = action.getValue()[0].replace("{player}", player.getDisplayName());
        if (message.split(" ", 2).length > 1) {
            alert = alert.replace("{arguments}", message.split(" ", 2)[1]);
        }
        ProxyServer.getInstance().broadcast(color(alert));
    }

    private String replace(String message, Map.Entry<String, String[]> action) {
        Matcher m = getMatcher(message);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            int n = rand.nextInt(action.getValue().length);
            sb.append(message, last, m.start());
            sb.append(action.getValue()[n]);
            last = m.end();
        }
        sb.append(message.substring(last));
        message = sb.toString();
        return message;
    }

    private String lowercase(String message) {
        Matcher m = getMatcher(message);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sb.append(message, last, m.start());
            sb.append(m.group(0).toLowerCase());
            last = m.end();
        }
        sb.append(message.substring(last));
        message = sb.toString();
        return message;
    }

    private boolean checkIgnored(String message) {
        if (this.ignore != null) {
            Matcher ig = Pattern.compile(this.ignore.pattern()).matcher(message);
            return ig.find();
        }
        return false;
    }

    private boolean checkServer(ProxiedPlayer player) {
        if (this.server != null && player.getServer() != null && player.getServer().getInfo() != null) {
            Matcher ig = Pattern.compile(this.server.pattern()).matcher(player.getServer().getInfo().getName());
            return !ig.find();
        }
        return false;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * @Author zedwick (https://github.com/zedwick/BungeeChatFilter/blob/master/src/main/java/com/minecraftdimensions/bungeechatfilter/util.java)
     */
    private String parseVariables(String string, ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        String arguments = "";

        if (message.split(" ", 2).length > 1) {
            arguments = message.split(" ", 2)[1];
        }

        string = string
                .replace("{player}", player.getDisplayName())
                .replace("{message}", message)
                .replace("{arguments}", arguments);

        return string;
    }
}
