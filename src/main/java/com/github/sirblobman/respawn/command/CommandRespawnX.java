package com.github.sirblobman.respawn.command;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.respawn.RespawnPlugin;

public final class CommandRespawnX extends Command {
    public CommandRespawnX(RespawnPlugin plugin) {
        super(plugin, "respawnx");
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

        JavaPlugin plugin = getPlugin();
        plugin.reloadConfig();

        LanguageManager languageManager = getLanguageManager();
        if (languageManager != null) {
            Component message = Component.text("[RespawnX] ", NamedTextColor.DARK_GREEN)
                    .append(Component.text("Successfully reloaded the configuration file.",
                            NamedTextColor.GREEN));
            languageManager.sendMessage(sender, message);
        }

        return true;
    }
}
