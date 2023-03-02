package me.gorenjec.spedupfurnaces.commands.furnace;

import cloud.commandframework.Command;
import cloud.commandframework.paper.PaperCommandManager;
import me.gorenjec.spedupfurnaces.models.AstronaCommand;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpedupFurnaceCommand extends AstronaCommand {
    @Override
    public Command<CommandSender> createCommand(PaperCommandManager<CommandSender> manager) {
        return manager.commandBuilder("spedupfurnaces", "spedupfurnace", "sf", "suf", "spedupf", "sufurnace", "sfurnace", "speedfurnace")
                .permission("spedupfurnaces.use")
                .handler(commandContext -> {
                    Player player = (Player) commandContext.getSender();

                    player.sendMessage(HexUtils.colorify("&cUnknown subcommand, try /spedupfurnaces help for a list of commands!"));
                }).build();
    }
}
