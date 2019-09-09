package com.SirBlobman.respawnx.command;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.respawnx.RespawnX;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandRespawnX implements CommandExecutor {
    private final RespawnX plugin;
    public CommandRespawnX(RespawnX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;

        String sub = args[0].toLowerCase();
        if(sub.equals("reload")) return reloadCommand(sender);

        return false;
    }

    private boolean reloadCommand(CommandSender sender) {
        this.plugin.reloadConfig();

        String message = Util.color("&c[&4RespawnX&c] &fSuccessfully reloaded 'config.yml'.");
        sender.sendMessage(message);
        return true;
    }
}