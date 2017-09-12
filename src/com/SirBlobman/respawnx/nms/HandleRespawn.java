package com.SirBlobman.respawnx.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HandleRespawn {
    public abstract void handleDeath(Player p);
    public void handleRespawn(Player p) {
        p.setCanPickupItems(true);
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