package me.gorenjec.spedupfurnaces.models;

import cloud.commandframework.Command;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;

public abstract class AstronaCommand {
    public abstract Command<CommandSender> createCommand(PaperCommandManager<CommandSender> manager);
}
