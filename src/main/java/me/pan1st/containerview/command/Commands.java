package me.pan1st.containerview.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import me.pan1st.containerview.ContainerView;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

@DefaultQualifier(NonNull.class)
public final class Commands {

    private final ContainerView plugin;
    private PaperCommandManager<CommandSender> manager;
    private CommandConfirmationManager<CommandSender> confirmationManager;
    private AnnotationParser<CommandSender> annotationParser;

    private Commands(ContainerView plugin) {
        this.plugin = plugin;
    }

    private void register() throws Exception {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();

        try {
            this.manager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin,
                    /* Coordinator function */ executionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunction
            );
        } catch (final Exception e) {
            plugin.log(Level.SEVERE, "Failed to initialize the command this.manager");
            /* Disable the plugin */
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
        ));

        if (this.manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.manager.registerBrigadier();
        }

        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }

        this.confirmationManager = new CommandConfirmationManager<>(
                /* Timeout */ 30L,
                /* Timeout unit */ TimeUnit.SECONDS,
                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
                ChatColor.RED + "Confirmation required. Confirm using /example confirm."),
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
                ChatColor.RED + "You don't have any pending commands.")
        );

        this.confirmationManager.registerConfirmationProcessor(this.manager);

        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        // This will allow you to decorate commands with descriptions
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        this.annotationParser = new AnnotationParser<>(
                /* Manager */ this.manager,
                /* Command sender type */ CommandSender.class,
                /* Mapper for command meta instances */ commandMetaFunction
        );

        new ExceptionHandler(plugin, this.manager).register();

        this.annotationParser.parseContainers();
    }

    public static Commands setup(final ContainerView plugin) throws Exception {
        final Commands instance = new Commands(plugin);
        instance.register();
        return instance;
    }


}
