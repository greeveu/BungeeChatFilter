package com.minecraftdimensions.bungeechatfilter;

import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Plugin {

    @Getter
    private static Main instance;

    @Getter
    private ArrayList<Rule> rules;

    @Getter
    @Setter
    private Config config;

    public void onEnable() {
        instance = this;
        initialiseConfig();
        this.getProxy().getPluginManager().registerListener(this, new PlayerChatListener());
        this.getProxy().getPluginManager().registerCommand(this, new BFReload("bungeefilterreload", "bungeefilter.reload", "bfreload", "reloadbf"));
    }

    private void initialiseConfig() {
        File file = new File(this.getDataFolder().getAbsoluteFile() + File.separator + "config.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");

            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = MainConfig.config;
        loadRules();
    }

    public void loadRules() {
        rules = new ArrayList<>();
        List<String> nodes = config.getSubNodes("rules");
        for (String node : nodes) {
            String regex = config.getString("rules." + node + ".regex");
            String perm = config.getString("rules." + node + ".permission");
            String ignore = config.getString("rules." + node + ".ignores");
            String server = config.getString("rules." + node + ".server");
            PermissionType permissionType = PermissionType.valueOf(config.getString("rules." + node + ".permissiontype", "BYPASS"));
            boolean ignoreCommands = config.getBoolean("rules." + node + ".ignoreCommands", false);
            Map<String, String[]> actions = extractActions(node);

            rules.add(new Rule(regex, actions, perm, ignore, server, permissionType, ignoreCommands));
        }
        getProxy().getLogger().log(Level.INFO, rules.size() + " filter rules loaded!");
    }

    private Map<String, String[]> extractActions(String node) {
        Map<String, String[]> actions = new HashMap<>();
        for (String action : config.getSubNodes("rules." + node + ".actions")) {
            if (action.equals("replace")) {
                List<String> strlist = config.getListString("rules." + node + ".actions.replace");
                actions.put(action, strlist.toArray(new String[0]));
            } else {
                actions.put(action, new String[]{config.getString("rules." + node + ".actions." + action)});
            }
        }
        return actions;
    }
}
