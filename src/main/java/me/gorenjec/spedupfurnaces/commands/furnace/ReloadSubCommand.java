package me.gorenjec.spedupfurnaces.commands.furnace;

import cloud.commandframework.Command;
import cloud.commandframework.paper.PaperCommandManager;
import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.AstronaCommand;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadSubCommand extends AstronaCommand {
    private final FurnacesFile furnacesFile;
    private final CustomizationFile customizationFile;
    private final InMemoryCache cache;

    public ReloadSubCommand(FurnacesFile furnacesFile, CustomizationFile customizationFile, InMemoryCache cache) {
        this.furnacesFile = furnacesFile;
        this.customizationFile = customizationFile;
        this.cache = cache;
    }

    @Override
    public Command<CommandSender> createCommand(PaperCommandManager<CommandSender> manager) {
        return manager.commandBuilder("spedupfurnaces", "spedupfurnace", "sf", "suf", "spedupf", "sufurnace", "sfurnace", "speedfurnace")
                .literal("reload")
                .permission("spedupfurnaces.use.reload")
                .handler(commandContext -> {
                    Player player = (Player) commandContext.getSender();
                    SpedupFurnaces instance = cache.getInstance();
                    instance.reloadConfig();
                    furnacesFile.cache();
                    customizationFile.cache();
                    player.sendMessage(HexUtils.colorify("&aReloaded configuration."));
                }).build();
    }
}
