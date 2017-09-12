package com.SirBlobman.respawnx.nms;

import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R3.EnumClientCommand;
import net.minecraft.server.v1_7_R3.PlayerConnection;

public class HandleRespawn1_7_R3 extends HandleRespawn {
    @Override
    public void handleDeath(Player p) {
        EnumClientCommand respawn = EnumClientCommand.PERFORM_RESPAWN;
        PacketPlayInClientCommand in = new PacketPlayInClientCommand(respawn);
        
        CraftPlayer cp = (CraftPlayer) p;
        EntityPlayer ep = cp.getHandle();
        PlayerConnection pc = ep.playerConnection;
        pc.a(in);
    }
}