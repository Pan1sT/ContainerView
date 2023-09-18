package me.pan1st.containerview.command.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.util.Common;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

@CommandContainer
public class ReloadCommand {

    @CommandMethod(value = "cview reload")
    @CommandDescription(value = "Reloading configuration for ContainerView")
    @CommandPermission(value = "cview.command.reload")
    public final void reload(@NonNull CommandSender sender) {
        ContainerView plugin = ContainerView.getInstance();
        plugin.reload();
        sender.sendMessage(Common.deserialize(plugin.setting.reload));
    }
}
