package de.uscoutz.nexus.listeners.player;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.player.NexusPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private NexusPlugin plugin;

    public PlayerJoinListener(NexusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


    }
}