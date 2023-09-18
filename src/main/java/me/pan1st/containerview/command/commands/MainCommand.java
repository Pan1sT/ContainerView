package me.pan1st.containerview.command.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.task.AsyncCacheTask;
import me.pan1st.containerview.util.Common;
import me.pan1st.containerview.util.Containers;
import me.pan1st.containerview.profile.PlayerProfile;
import me.pan1st.containerview.util.Results;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@CommandContainer
public class MainCommand {

    @CommandMethod("cview")
    @CommandDescription("Check containers in radius")
    @CommandPermission("cview.command.default")
    public final void cView(@NonNull Player sender) {
        try {
            ContainerView plugin = ContainerView.getInstance();
            Location playerLoc = sender.getLocation();
            int blockRadius = ContainerView.getInstance().setting.radius;

            CompletableFuture<List<BlockState>> futureTileEntities = Containers.getNearbyContainersAsync(playerLoc, blockRadius, Material.DROPPER, Material.DISPENSER);

            futureTileEntities.thenAccept(sortedTileEntities -> {
                if (plugin.playerProfiles.containsKey(sender.getUniqueId())) {
                    new Results(sender).unshow();
                    plugin.playerProfiles.remove(sender.getUniqueId());
                }
                if (sortedTileEntities.isEmpty()) {
                    sender.sendMessage(Common.deserialize(plugin.setting.queryNotFound));
                    return;
                }
                plugin.playerProfiles.put(sender.getUniqueId(), new PlayerProfile(sender.getUniqueId(), sortedTileEntities, playerLoc, Instant.now()));
                try {
                    new Results(sender).show();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
                new AsyncCacheTask(sender).runTaskTimerAsynchronously(plugin, 20, plugin.setting.checkPeriod);
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}