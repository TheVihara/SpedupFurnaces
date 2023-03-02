package me.gorenjec.spedupfurnaces.commands.furnace;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import me.gorenjec.spedupfurnaces.models.AstronaCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends AstronaCommand {
    @Override
    public Command<CommandSender> createCommand(PaperCommandManager<CommandSender> manager) {
        return manager.commandBuilder("spedupfurnaces", "spedupfurnace", "sf", "suf", "spedupf", "sufurnace", "sfurnace", "speedfurnace")
                .permission("spedupfurnaces.use.help")
                .meta(CommandMeta.DESCRIPTION, "Shows you the help page for the SpedupFurnaces plugin.")
                .literal("help")
                .argument(StringArgument.<CommandSender>newBuilder("query").greedy().asOptional().build())
                .handler(commandContext -> {
                    MinecraftHelp minecraftHelp = new MinecraftHelp("/spedupfurnaces help", AudienceProvider.nativeAudience(), manager);
                    minecraftHelp.setMessage(MinecraftHelp.MESSAGE_HELP_TITLE, "Spedup Furnaces Help");
                    minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
                            TextColor.color(0x5467FF),
                            NamedTextColor.WHITE,
                            TextColor.color(0x19D3FF),
                            NamedTextColor.GRAY,
                            NamedTextColor.DARK_GRAY));
                    minecraftHelp.queryCommands(commandContext.<String>getOptional("query").orElse(""), commandContext.getSender());
                }).build();
    }
}
