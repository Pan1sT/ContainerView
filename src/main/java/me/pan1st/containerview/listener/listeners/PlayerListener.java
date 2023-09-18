package me.pan1st.containerview.listener.listeners;

import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if (e.getBlock().getType() != Material.DISPENSER && e.getBlock().getType() != Material.DROPPER) return;
        ContainerView plugin = ContainerView.getInstance();
        if (!plugin.playerProfiles.containsKey(e.getPlayer().getUniqueId())) return;
        PlayerProfile playerProfile = plugin.playerProfiles.get(e.getPlayer().getUniqueId());
        playerProfile.removeSpecificBlockState(e.getBlock().getState());
        plugin.morePaperLib.scheduling().asyncScheduler().run(() -> {
            try {
                plugin.glowingBlocksManager.unsetGlowing(e.getBlock().getLocation(), e.getPlayer());
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        ContainerView plugin = ContainerView.getInstance();
        if (plugin.playerProfiles.containsKey(e.getPlayer().getUniqueId())){
            plugin.playerProfiles.remove(e.getPlayer().getUniqueId());
        }
    }

}
