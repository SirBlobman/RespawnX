package com.SirBlobman.respawnx.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigSettings extends Config {
    private static final File FILE = new File(FOLDER, "config.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    
    public static void save() {save(config, FILE);}
    public static YamlConfiguration load() {
        config = load(FILE);
        defaults();
        return config;
    }
    
    public static long DELAY_IN_TICKS = 1;
    public static boolean REQUIRE_PERMISSION = true;
    public static String PERMISSION = "respawnx.respawn";
    public static boolean RESPAWN_NEAR_DEATH = true;
    public static int RESPAWN_RADIUS = 1;
    private static void defaults() {
        DELAY_IN_TICKS = get(config, "delay in ticks", 1L);
        REQUIRE_PERMISSION = get(config, "require permission", true);
        PERMISSION = get(config, "permission", "respawnx.respawn");
        RESPAWN_NEAR_DEATH = get(config, "respawn near death.enabled", true);
        RESPAWN_RADIUS = get(config, "respawn near death.radius", 1);
        save();
    }
}