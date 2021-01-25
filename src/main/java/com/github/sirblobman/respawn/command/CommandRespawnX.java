package com.github.sirblobman.respawn.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.respawn.RespawnPlugin;

public class CommandRespawnX extends Command {
    private final RespawnPlugin plugin;
    public CommandRespawnX(RespawnPlugin plugin) {
        super(plugin, "respawnx");
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return (args.length == 1 ? Collections.singletonList("reload") : Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        String sub = args[0].toLowerCase();
        if(!sub.equals("reload")) return false;

        this.plugin.reloadConfig();
        String message = MessageUtility.color("&2[RespawnX]&a Successfully reloaded the configuration file.");
        sender.sendMessage(message);
        return true;
    }
}