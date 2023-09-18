package me.pan1st.containerview;

import fr.skytasul.glowingentities.GlowingBlocks;
import fr.skytasul.glowingentities.GlowingEntities;
import io.papermc.lib.PaperLib;
import me.pan1st.containerview.command.Commands;
import me.pan1st.containerview.config.Setting;
import me.pan1st.containerview.listener.Listeners;
import me.pan1st.containerview.profile.PlayerProfile;
import net.william278.annotaml.Annotaml;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ContainerView extends JavaPlugin {
    public static ContainerView instance;
    public MorePaperLib morePaperLib;
    public Setting setting;
    public Listeners listeners;
    public Commands commands;
    public GlowingEntities glowingEntitiesManager;
    public GlowingBlocks glowingBlocksManager;
    public HashMap<UUID, PlayerProfile> playerProfiles;

    public static ContainerView getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Initialize plugin
        final AtomicBoolean initialized = new AtomicBoolean(true);
        try {
            // Require Paper to work
            PaperLib.suggestPaper(this, Level.WARNING);
            morePaperLib = new MorePaperLib(this);
            if (!PaperLib.isPaper()) throw new IllegalStateException("This plugin requires Paper to work!");

            // Load settings and locales
            log(Level.INFO, "Loading plugin configuration settings...");
            initialized.set(reload().join());
            if (initialized.get()) {
                log(Level.INFO, "Successfully loaded plugin configuration settings");
            } else {
                throw new IllegalStateException("Failed to load plugin configuration settings and/or locales");
            }

            // Register events
            log(Level.INFO, "Registering events...");
            this.listeners = Listeners.setup(this);
            log(Level.INFO, "Successfully registered events listener");

            glowingEntitiesManager = new GlowingEntities(this);
            glowingBlocksManager = new GlowingBlocks(this);

            // Register commands
            this.commands = Commands.setup(this);
            log(Level.INFO, "Successfully registered permissions & commands");

            playerProfiles = new HashMap<>();

        } catch (IllegalStateException exception) {
            log(Level.SEVERE, """
                    ***************************************************
                               
                            Failed to initialize ContainerView!
                               
                    ***************************************************
                    The plugin was disabled due to an error. Please check
                    the logs below for details.
                    No user data will be synchronised.
                    ***************************************************
                    Caused by: %error_message%
                    """
                    .replaceAll("%error_message%", exception.getMessage()));
            initialized.set(false);
        } catch (Exception exception) {
            log(Level.SEVERE, "An unhandled exception occurred initializing ContainerView!", exception);
            initialized.set(false);
        } finally {
            // Validate initialization
            if (initialized.get()) {
                log(Level.INFO, "Successfully enabled ContainerView");
            } else {
                log(Level.SEVERE, "Failed to initialize ContainerView. The plugin will now be disabled");
                getServer().getPluginManager().disablePlugin(this);
            }
        }

    }

    @Override
    public void onDisable() {
        log(Level.INFO, "Successfully disabled ContainerView");
        glowingEntitiesManager.disable();
        glowingBlocksManager.disable();
        playerProfiles.clear();
    }

    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... throwable) {
        if (throwable.length > 0) {
            getLogger().log(level, message, throwable[0]);
        } else {
            getLogger().log(level, message);
        }
    }

    public CompletableFuture<Boolean> reload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Load plugin settings
                this.setting = Annotaml.create(new File(getDataFolder(), "config.yml"), new Setting()).get();

                return true;
            } catch (IOException | NullPointerException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                log(Level.SEVERE, "Failed to load data from the config", e);
                return false;
            }
        });
    }

}
