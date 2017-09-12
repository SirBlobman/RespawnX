package com.SirBlobman.respawnx.config;

import com.SirBlobman.respawnx.RespawnX;
import com.SirBlobman.respawnx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

class Config {
    protected static final File FOLDER = RespawnX.FOLDER;
    protected static YamlConfiguration load(String name) {
        File file = new File(FOLDER, name + ".yml");
        YamlConfiguration config = load(file);
        return config;
    }
    
    protected static YamlConfiguration load(File file) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            if(!file.exists()) save(config, file);
            config.load(file);
            return config;
        } catch(Throwable ex) {
            String error = "Failed to load config '" + file + "':";
            Util.print(error);
            ex.printStackTrace();
            return null;
        }
    }
    
    protected static void save(YamlConfiguration config, File file) {
        try {
            if(!file.exists()) {
                FOLDER.mkdirs();
                file.createNewFile();
            } config.save(file);
        } catch(Throwable ex) {
            String error = "Failed to save config '" + file + "':";
            Util.print(error);
            ex.printStackTrace();
        }
    }

    /**
     * Gets a config value of the same type as defaultValue {@link T}<br/>
     * @param config YamlConfiguration to use
     * @param path String path to the option
     * @param defaultValue If the value does not exist, it will become this
     * @return The value at {@code path}, if it is null or not the same type, {@code defaultValue} will be returned
     */
    @SuppressWarnings("unchecked")
    protected static <T extends Object> T get(YamlConfiguration config, String path, T defaultValue) {
        try {
            Object o = config.get(path);
            Class<?> clazz = defaultValue.getClass();
            if(o != null && clazz.isInstance(o)) {
                T t = (T) clazz.cast(o);
                return t;
            } else {
                config.set(path, defaultValue);
                return defaultValue;
            }
        } catch(Throwable ex) {
            Util.print("Config Parsing Error:");
            ex.printStackTrace();
            return defaultValue;
        }
    }
}