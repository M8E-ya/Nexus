package de.uscoutz.nexus.listeners.player;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.networking.packet.packets.player.PacketPlayerChat;
import eu.thesimplecloud.api.service.ICloudService;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class AsyncPlayerChatListener implements Listener {

    private final NexusPlugin plugin;

    public AsyncPlayerChatListener(NexusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncChatEvent event) {
        event.setCancelled(true);
        TextComponent messageComponent = (TextComponent) event.message();
        String message = messageComponent.content();
        String displayName = ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(event.getPlayer().displayName()));

        if(message.startsWith("@")) {
            Map<String, Integer> substringCommand = new HashMap<>();
            substringCommand.put("@t", 2);
            substringCommand.put("@c", 2);
            substringCommand.put("@team", 5);
            substringCommand.put("@coop", 5);
            int substring = 0;

            for(String command : substringCommand.keySet()) {
                if(message.startsWith(command)) {
                    if(substringCommand.get(command) > substring) {
                        substring = substringCommand.get(command);
                    }
                }
            }

            if(substring != 0) {
                message = message.substring(substring);
                if(message.startsWith(" ")) {
                    message = message.substring(1);
                }
                if(!message.equals("")) {
                    for(Player all : event.getPlayer().getWorld().getPlayers()) {
                        all.sendMessage("§7[§aCoop§7] " + displayName +"§7: §f" + message);
                    }
                }
                return;
            }
        }
        for(ICloudService iCloudService : plugin.getNexusServer().getNexusServers()) {
            new PacketPlayerChat("123", displayName, message).send(iCloudService);
        }
    }
}
