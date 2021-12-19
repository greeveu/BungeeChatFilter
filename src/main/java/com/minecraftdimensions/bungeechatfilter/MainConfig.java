package com.minecraftdimensions.bungeechatfilter;


import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class MainConfig {

    MainConfig() {
        throw new IllegalStateException("Utility class");
    }

    @Getter
    private static final String CONFIGPATH = File.separator + "plugins" + File.separator + "BungeeChatFilter" + File.separator + "config.yml";
    @Getter
    @Setter
    private static Config config = new Config(CONFIGPATH);

}
