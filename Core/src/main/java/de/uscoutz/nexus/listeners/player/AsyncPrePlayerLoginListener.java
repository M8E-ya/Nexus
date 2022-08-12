package de.uscoutz.nexus.listeners.player;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.player.NexusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;

public class AsyncPrePlayerLoginListener implements Listener {

    private NexusPlugin plugin;

    public AsyncPrePlayerLoginListener(NexusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        NexusPlayer nexusPlayer = new NexusPlayer(playerUUID, plugin);
        boolean registered = nexusPlayer.registered();

        if(registered) {
            assert nexusPlayer.getCurrentProfile() != null;
            nexusPlayer.setActiveProfile(nexusPlayer.getCurrentProfileSlot(), true);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    nexusPlayer.setActiveProfile(nexusPlayer.getCurrentProfileSlot(), true);
                }
            }.runTaskLater(plugin, 10);
        }
    }
}
