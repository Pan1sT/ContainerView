package me.pan1st.containerview.command.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.profile.PlayerProfile;
import me.pan1st.containerview.util.Common;
import me.pan1st.containerview.util.Results;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

@CommandContainer
public class PageCommand {

    @CommandMethod("cview page <page>")
    @CommandDescription("switch new page")
    public final void page(@NonNull Player sender, @Argument("page") int page) {
        ContainerView plugin = ContainerView.getInstance();
        if (!plugin.playerProfiles.containsKey(sender.getUniqueId())){
            sender.sendMessage(Common.deserialize(plugin.setting.noCachedResult));
            return;
        }
        PlayerProfile playerProfile = plugin.playerProfiles.get(sender.getUniqueId());
        if (page - 1 == playerProfile.getPage()) return;
        if (page <= 0 || page > playerProfile.getContainerspPageSize()){
            sender.sendMessage(Common.deserialize(plugin.setting.invalidPage));
            return;
        }
        plugin.morePaperLib.scheduling().asyncScheduler().run(() -> {
            new Results(sender).unshow();
            playerProfile.setPage(page - 1);
            try {
                new Results(sender).show();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

}