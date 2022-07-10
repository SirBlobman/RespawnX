package com.github.sirblobman.respawn.command;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.respawn.RespawnPlugin;

public final class CommandRespawnX extends Command {
    private final RespawnPlugin plugin;

    public CommandRespawnX(RespawnPlugin plugin) {
        super(plugin, "respawnx");
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            return Collections.singletonList("reload");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 1) {
            return false;
        }

        String sub = args[0].toLowerCase(Locale.US);
        if(!sub.equals("reload")) {
            return false;
        }

        this.plugin.reloadConfig();
        String message = MessageUtility.color("&2[RespawnX]&a Successfully reloaded the configuration file.");
        sender.sendMessage(message);
        return true;
    }
}
