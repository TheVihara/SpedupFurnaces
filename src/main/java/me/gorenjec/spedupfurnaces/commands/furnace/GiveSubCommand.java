package me.gorenjec.spedupfurnaces.commands.furnace;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.paper.PaperCommandManager;
import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.AstronaCommand;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSubCommand extends AstronaCommand {
    private final FurnacesFile furnacesFile;
    private final InMemoryCache cache;
    private final NBTUtil nbtUtil;

    public GiveSubCommand(FurnacesFile furnacesFile, InMemoryCache cache, NBTUtil nbtUtil) {
        this.furnacesFile = furnacesFile;
        this.cache = cache;
        this.nbtUtil = nbtUtil;
    }
    @Override
    public Command<CommandSender> createCommand(PaperCommandManager<CommandSender> manager) {
        return manager.commandBuilder("spedupfurnaces", "spedupfurnace", "sf", "suf", "spedupf", "sufurnace", "sfurnace", "speedfurnace")
                .permission("spedupfurnaces.use.give")
                .literal("give")
                .argument(StringArgument.of("type"))
                .argument(IntegerArgument.of("amount"))
                .argument(IntegerArgument.optional("level"))
                .argument(SinglePlayerSelectorArgument.optional("player"))
                .handler(commandContext -> {
                    Player player = (Player) commandContext.getSender();
                    Player target = commandContext.getOrDefault("player", player);
                    Material material = Material.valueOf(commandContext.get("type").toString().toUpperCase());
                    int amount = commandContext.get("amount");
                    int level = commandContext.getOrDefault("level", 1);
                    FileConfiguration furnacesConfig = furnacesFile.getConfig();
                    ConfigurationSection furnacesSection = furnacesConfig.getConfigurationSection("furnaces");

                    if (furnacesSection == null) {
                        return;
                    }

                    if (!furnacesSection.contains(material.name().toLowerCase())) {
                        player.sendMessage(HexUtils.colorify("&cThat type of a furnace does not exist."));
                        return;
                    }

                    ConfigurationSection furnaceSection = furnacesSection.getConfigurationSection(material.name().toLowerCase());

                    if (furnaceSection == null) {
                        return;
                    }

                    ItemStack item = cache.createItem(material, level, amount, furnaceSection.getInt("speed." + String.valueOf(level)), nbtUtil);
                    target.getInventory().addItem(item);

                    player.sendMessage(HexUtils.colorify("&aGave " + target.getName() + " " + amount + "x " + material.name() + " level " + level));
                }).build();
    }
}
