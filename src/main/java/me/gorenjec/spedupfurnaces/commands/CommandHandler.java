package me.gorenjec.spedupfurnaces.commands;

import cloud.commandframework.CommandTree;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.models.AstronaCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

public class CommandHandler {
    private final SpedupFurnaces instance;
    public static PaperCommandManager<CommandSender> manager;

    public CommandHandler(SpedupFurnaces instance) {
        this.instance = instance;
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();

        try {
            manager = new PaperCommandManager<>(
                    instance,
                    executionCoordinatorFunction,
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            e.printStackTrace();
            instance.getLogger().severe("UNABLE TO INITIALIZE COMMAND MANAGER. SOMETHING IS REALLY WRONG! Disabling now...");
            Bukkit.getPluginManager().disablePlugin(instance);
            return;
        }

        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withArgumentParsingHandler()
                .withNoPermissionHandler()
                .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION,
                        (commandSender, e) -> {
                    e.printStackTrace();
                    return Component.text()
                                .content("An internal error occurred while attempting to perform this command.")
                                .color(NamedTextColor.RED)
                                .build();
                        })

                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX,
                        (commandSender, e) -> Component.text()
                                .content("Usage: ")
                                .style(Style.style(TextDecoration.ITALIC)
                                        .color(NamedTextColor.DARK_AQUA))
                                .append(Component.text()
                                        .content("/" + e.getMessage().replace("Invalid command syntax. Correct syntax is: ", ""))
                                        .style(Style.style(TextDecoration.ITALIC)
                                                .color(NamedTextColor.GRAY))
                                ).build())

                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION,
                        (commandSender, e) -> Component.text()
                                .content("Error: ")
                                .style(Style.style(TextDecoration.ITALIC)
                                        .color(NamedTextColor.RED))
                                .append(Component.text()
                                        .content("You don't have permission to use this command!")
                                        .style(Style.style(TextDecoration.ITALIC)
                                                .color(NamedTextColor.GRAY))
                                ).build())

                .apply(manager, commandSender -> (Audience) commandSender);
    }

    public static void register(AstronaCommand cmd) {
        manager.command(cmd.createCommand(manager));
    }

    public static void register(AstronaCommand... cmds) {
        for (AstronaCommand cmd : cmds) {
            register(cmd);
        }
    }
}
