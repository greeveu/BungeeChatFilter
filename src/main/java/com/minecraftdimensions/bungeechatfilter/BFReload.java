package com.minecraftdimensions.bungeechatfilter;

import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * User: Bloodsplat
 * Date: 21/10/13
 */
public class BFReload extends Command {

    public BFReload(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MainConfig.setConfig(new Config(MainConfig.getCONFIGPATH()));
        Main.getInstance().getRules().clear();
        Main.getInstance().setConfig(MainConfig.getConfig());
        Main.getInstance().loadRules();
        sender.sendMessage(new TextComponent("BungeeFilter Reloaded"));
    }
}
