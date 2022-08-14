package de.uscoutz.nexus.networking.packet.packets.profiles;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.networking.packet.Packet;
import lombok.Getter;
import org.bukkit.Bukkit;

public class PacketSendProfilesCount extends Packet {

    @Getter
    private String serverName;
    @Getter
    private int profileCount;

    public PacketSendProfilesCount(String password, int profileCount, String serverName) {
        super(password);
        this.profileCount = profileCount;
        this.serverName = serverName;
    }

    @Override
    public Object execute() {
        NexusPlugin.getInstance().getNexusServer().getProfileCountByServer().put(serverName, profileCount);
        Bukkit.broadcastMessage(serverName + " hat " + profileCount + " Profile geladen");

        return this;
    }
}