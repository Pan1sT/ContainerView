package me.pan1st.containerview.task;

import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.profile.PlayerProfile;
import me.pan1st.containerview.util.Containers;
import me.pan1st.containerview.util.Results;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;

public class AsyncCacheTask extends BukkitRunnable {

    private ContainerView plugin;
    private Player player;

    public AsyncCacheTask(Player player){
        this.plugin = ContainerView.getInstance();
        this.player = player;
    }

    @Override
    public void run() {

        if (!plugin.playerProfiles.containsKey(player.getUniqueId())) cancel();
        PlayerProfile playerProfile = plugin.playerProfiles.get(player.getUniqueId());

        if (Duration.between(playerProfile.getQueryTime(), Instant.now()).getSeconds() > plugin.setting.cachedDuration){
            new Results(player).unshow();
            plugin.playerProfiles.remove(player.getUniqueId());
            cancel();
        }

        if (Containers.isWithinBoundingBox(player.getLocation(), playerProfile.getQueryLocation(), ContainerView.getInstance().setting.radius)) return;

        new Results(player).unshow();
        plugin.playerProfiles.remove(player.getUniqueId());
        cancel();
    }


}
