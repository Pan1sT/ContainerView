package me.pan1st.containerview.util;

import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.gui.TempContainerGUI;
import me.pan1st.containerview.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

public class Results {

    private Player player;
    private ContainerView plugin;

    public Results(Player player) {
        this.player = player;
        this.plugin = ContainerView.getInstance();
    }

    public void show() throws ReflectiveOperationException {
        PlayerProfile playerProfile = plugin.playerProfiles.get(player.getUniqueId());
        int currentPage = playerProfile.getPage();
        int totalPage = playerProfile.getContainerspPageSize();
        List<BlockState> containerList = playerProfile.getContainersByPage(currentPage);

        player.sendMessage(Common.withPlaceholder(plugin.setting.resultHeader,
                Components.placeholder("current_page", currentPage + 1),
                Components.placeholder("total_page", totalPage)
                ));

        for (int i = 0; i < containerList.size(); i++) {
            BlockState container = containerList.get(i);
            ChatColor color = DISTINCT_COLORS[i % DISTINCT_COLORS.length];
            TextColor textColor = DISTINCT_TEXT_COLORS[i % DISTINCT_COLORS.length];
            Component clickMessage = Common.deserialize(plugin.setting.resultClickMessage).clickEvent(ClickEvent.callback(audience -> {
                        new TempContainerGUI(container).show((Player) audience);
                    },
                    ClickCallback.Options.builder().uses(-1).lifetime(Duration.ofMinutes(10)).build()));

            player.sendMessage(Common.withPlaceholder(plugin.setting.resultContent,
                    Components.placeholder("container_x", container.getX()),
                    Components.placeholder("container_y", container.getY()),
                    Components.placeholder("container_z", container.getZ()),
                    Components.placeholder("click_message", clickMessage)
            ).colorIfAbsent(textColor));

            plugin.glowingBlocksManager.setGlowing(container.getLocation(), player, color);
        }

        Component resultPrevious = Common.deserialize(plugin.setting.resultPrevious).clickEvent(
                ClickEvent.runCommand("/cview page " + currentPage));
        Component resultNext = Common.deserialize(plugin.setting.resultNext).clickEvent(
                ClickEvent.runCommand("/cview page " + currentPage + 2));

        player.sendMessage(Common.withPlaceholder(plugin.setting.resultFooter,
                Components.placeholder("current_page", currentPage + 1),
                Components.placeholder("total_page", totalPage),
                Components.placeholder("result_previous", resultPrevious),
                Components.placeholder("result_next", resultNext)
        ));
    }

    public void unshow() {
        if (!plugin.playerProfiles.containsKey(player.getUniqueId())) return;
        PlayerProfile playerProfile = plugin.playerProfiles.get(player.getUniqueId());
        int currentPage = playerProfile.getPage();
        playerProfile.getContainersByPage(currentPage).forEach(container -> {
            try {
                plugin.glowingBlocksManager.unsetGlowing(container.getLocation(), player);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }



    private static final ChatColor[] DISTINCT_COLORS = {
            ChatColor.BLACK,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW,
            ChatColor.WHITE,
    };

    private static final TextColor[] DISTINCT_TEXT_COLORS = {
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW,
            NamedTextColor.WHITE,
    };

}
