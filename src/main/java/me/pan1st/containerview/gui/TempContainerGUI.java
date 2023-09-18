package me.pan1st.containerview.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.DropperGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.util.Common;
import me.pan1st.containerview.util.Components;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TempContainerGUI {

    private ContainerView plugin;
    private DropperGui gui;
    private BlockState container;
    private ItemStack[] contents;

    public TempContainerGUI (BlockState container){
        this.plugin = ContainerView.getInstance();
        this.container = container;
        gui = init();
    }

    private DropperGui init(){
        if (container.getBlock().getType() == Material.AIR){
            return null;
        }
        if (container.getType() == Material.DROPPER){
            Component guiTitle = Common.withPlaceholder(plugin.setting.guiTitle,
                    Components.placeholder("container_type", Component.translatable("block.minecraft.dropper")));
            gui = new DropperGui(ComponentHolder.of(guiTitle));
            contents = ((Dropper) container.getLocation().getBlock().getState()).getInventory().getContents();
        } else if (container.getType() == Material.DISPENSER) {
            Component guiTitle = Common.withPlaceholder(plugin.setting.guiTitle,
                    Components.placeholder("container_type", Component.translatable("block.minecraft.dispenser")));
            gui = new DropperGui(ComponentHolder.of(guiTitle));
            contents = ((Dispenser) container.getLocation().getBlock().getState()).getInventory().getContents();
        }
        StaticPane pane = paneBuilder(contents);
        gui.getContentsComponent().addPane(pane);
        gui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
        gui.setOnGlobalDrag(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
        return gui;
    }

    public void show(Player player){
        if (!plugin.playerProfiles.containsKey(player.getUniqueId())) {
            return;
        }
        if (gui == null){
            player.sendMessage(Common.deserialize(plugin.setting.containerNotFound));
            return;
        }
        gui.show(player);
    }

    public StaticPane paneBuilder(ItemStack[] contents) {
        StaticPane pane = new StaticPane(0, 0, 3, 3);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int idx = y * 3 + x;
                ItemStack item = contents[idx];
                if (item == null) continue;
                pane.addItem(new GuiItem(item), x, y);
            }
        }
        return pane;
    }

}
