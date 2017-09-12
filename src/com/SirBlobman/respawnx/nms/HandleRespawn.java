package com.SirBlobman.respawnx.nms;

import com.SirBlobman.respawnx.config.ConfigSettings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HandleRespawn {
    public abstract void handleDeath(Player p);
    
    public void handleRespawn(Player p, Location death) {
        p.setCanPickupItems(true);
        if(ConfigSettings.RESPAWN_NEAR_DEATH) {
            int radius = ConfigSettings.RESPAWN_RADIUS;
            if(radius < 0) radius = 0;
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomVar = random.nextInt(0, radius);
            randomVar = random.nextBoolean() ? (-1 * randomVar) : randomVar;
            int x = death.getBlockX() + randomVar;
            int y = death.getBlockY() + randomVar;
            int z = death.getBlockZ() + randomVar;
            Location l = new Location(death.getWorld(), x, y, z);
            p.teleport(l);
        }
    }
    
    public static String minecraftVersion() {
        String version = Bukkit.getVersion();
        Pattern pat = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))");
        Matcher mat = pat.matcher(version);
        if(mat.find()) return mat.group(2);
        return "";
    }
    
    public static HandleRespawn getRespawnHandler() {
        String version = minecraftVersion();
        switch(version) {
            case "1.7":
            case "1.7.1":
            case "1.7.2":
            case "1.7.3":
            case "1.7.4":
                return new HandleRespawn1_7_R1();
            case "1.7.5":
            case "1.7.6":
            case "1.7.7":
                return new HandleRespawn1_7_R2();
            case "1.7.8":
            case "1.7.9":
                return new HandleRespawn1_7_R3();
            case "1.8":
            case "1.8.1":
            case "1.8.2":
                return new HandleRespawn1_8_R1();
            case "1.8.3":
            case "1.8.4":
            case "1.8.5":
            case "1.8.6":
            case "1.8.7":
                return new HandleRespawn1_8_R2();
            case "1.8.8":
            case "1.8.9":
                return new HandleRespawn1_8_R3();
            default:
                return new HandleRespawnNew();
        }
    }
}