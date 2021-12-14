package com.minecraftdimensions.bungeechatfilter;


import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;

import java.io.File;

public class MainConfig {

    public MainConfig() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CONFIGPATH = File.separator + "plugins" + File.separator + "BungeeChatFilter" + File.separator + "config.yml";
    public static Config config = new Config(CONFIGPATH);

}
