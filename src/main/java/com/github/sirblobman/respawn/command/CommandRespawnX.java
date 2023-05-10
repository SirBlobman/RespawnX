package com.github.sirblobman.respawn.command;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.respawn.RespawnPlugin;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextComponent;
import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;

public final class CommandRespawnX extends Command {
    public CommandRespawnX(@NotNull RespawnPlugin plugin) {
        super(plugin, "respawnx");
        setPermissionName("respawnx.reload");
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        String sub = args[0].toLowerCase(Locale.US);
        if (!sub.equals("reload")) {
            return false;
        }

        JavaPlugin plugin = getPlugin();
        plugin.reloadConfig();

        LanguageManager languageManager = getLanguageManager();
        if (languageManager != null) {
            sendReloadSuccess(sender, languageManager);
        }

        return true;
    }

    private void sendReloadSuccess(@NotNull CommandSender sender, @NotNull LanguageManager languageManager) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("[RespawnX]", NamedTextColor.DARK_GREEN));
        builder.append(Component.space());
        builder.append(Component.text("Successfully reload the configuration file.", NamedTextColor.GREEN));

        Component message = builder.build();
        languageManager.sendMessage(sender, message);
    }
}
