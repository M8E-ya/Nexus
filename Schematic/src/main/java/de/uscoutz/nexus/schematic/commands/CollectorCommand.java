package de.uscoutz.nexus.schematic.commands;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.schematic.NexusSchematicPlugin;
import de.uscoutz.nexus.schematic.collector.Collector;
import de.uscoutz.nexus.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CollectorCommand implements CommandExecutor {

    private NexusSchematicPlugin plugin;

    public CollectorCommand(NexusSchematicPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            new Collector(Arrays.asList(
                    ItemBuilder.create(Material.COAL)
                            .amount(16)
                            .build(),
                    ItemBuilder.create(Material.IRON_INGOT)
                            .amount(5)
                            .build(),
                    ItemBuilder.create(Material.NETHERITE_INGOT)
                            .amount(2)
                            .build()
            ), plugin).setFilledAction(player1 -> {
                player1.sendMessage("§6Wer das liest ist blöd");
            }).spawn(player.getLocation());
        }
        return false;
    }
}
