package com.SirBlobman.respawnx.nms;

import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

public class HandleRespawnNew extends HandleRespawn {
    @Override
    public void handleDeath(Player p) {
        Spigot sp = p.spigot();
        sp.respawn();
    }
}