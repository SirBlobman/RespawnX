package com.SirBlobman.respawnx.nms;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R1.EnumClientCommand;
import net.minecraft.server.v1_8_R1.PlayerConnection;

public class HandleRespawn1_8_R1 extends HandleRespawn {
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