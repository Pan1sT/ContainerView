package me.pan1st.containerview.listener;

import java.util.HashMap;
import java.util.Map;

import me.pan1st.containerview.ContainerView;
import me.pan1st.containerview.listener.listeners.PlayerListener;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Listeners {
    private final ContainerView plugin;
    private final Map<Class<?>, Listener> listeners = new HashMap<>();

    private Listeners(ContainerView plugin) {
        this.plugin = plugin;
    }

    private <L extends Listener> void registerListener(final Class<L> listenerClass, final L listener) {
        this.listeners.put(listenerClass, listener);
        this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
    }

    private void register() {
        this.registerListener(PlayerListener.class, new PlayerListener());
    }

    private void unregister() {
        this.listeners.forEach((clazz, listener) -> HandlerList.unregisterAll(listener));
        this.listeners.clear();
    }

    public void reload() {
        this.unregister();
        this.register();
    }

    public static Listeners setup(final ContainerView plugin) {
        final Listeners instance = new Listeners(plugin);
        instance.register();
        return instance;
    }
}