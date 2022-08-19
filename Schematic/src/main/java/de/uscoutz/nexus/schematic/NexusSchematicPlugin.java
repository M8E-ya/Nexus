package de.uscoutz.nexus.schematic;

import de.uscoutz.nexus.NexusPlugin;
import de.uscoutz.nexus.schematic.listener.block.BlockPlaceListener;
import de.uscoutz.nexus.schematic.listener.player.PlayerDropListener;
import de.uscoutz.nexus.schematic.listener.player.PlayerInteractListener;
import de.uscoutz.nexus.schematic.listener.player.PlayerItemHeldListener;
import de.uscoutz.nexus.schematic.listener.player.PlayerJoinListener;
import de.uscoutz.nexus.schematic.listener.profile.ProfileLoadListener;
import de.uscoutz.nexus.schematic.schematicitems.SchematicItemManager;
import de.uscoutz.nexus.schematic.collector.CollectorManager;
import de.uscoutz.nexus.schematic.commands.CreateSchematicCommand;
import de.uscoutz.nexus.schematic.commands.LoadSchematicCommand;
import de.uscoutz.nexus.schematic.commands.SchematicWand;
import de.uscoutz.nexus.schematic.files.FileManager;
import de.uscoutz.nexus.schematic.player.SchematicPlayerManager;
import de.uscoutz.nexus.schematic.schematics.SchematicManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NexusSchematicPlugin extends JavaPlugin {

    @Getter
    private static NexusSchematicPlugin instance;

    @Getter
    private SchematicPlayerManager playerManager;
    @Getter
    private FileManager fileManager;
    @Getter
    private SchematicManager schematicManager;
    @Getter
    private CollectorManager collectorManager;
    @Getter
    private SchematicItemManager schematicItemManager;

    @Getter
    private final String NO_PERMISSION = "§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.";

    @Override
    public void onEnable() {
        instance = this;
        playerManager = new SchematicPlayerManager(this);
        fileManager = new FileManager(this);
        fileManager.loadSchematicFiles();
        schematicManager = new SchematicManager(this);
        schematicManager.loadSchematics();
        collectorManager = new CollectorManager(new File("/home/networksync/nexus/schematiccollectors.yml"), this);
        collectorManager.loadCollectors();
        Bukkit.getConsoleSender().sendMessage(NexusPlugin.getInstance() +": " + Bukkit.getPluginManager().isPluginEnabled("NexusCore"));
        schematicItemManager = new SchematicItemManager(this, NexusPlugin.getInstance(),
                new File("/home/networksync/nexus/schematicitems.yml"));
        schematicItemManager.loadItems();

        getCommand("schematicwand").setExecutor(new SchematicWand(this));
        getCommand("createschematic").setExecutor(new CreateSchematicCommand(this));
        getCommand("loadschematic").setExecutor(new LoadSchematicCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ProfileLoadListener(this), this);
        Bukkit.getConsoleSender().sendMessage("[NexusSchematic] Enabled");
    }
}
