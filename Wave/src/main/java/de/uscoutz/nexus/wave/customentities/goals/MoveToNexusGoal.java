package de.uscoutz.nexus.wave.customentities.goals;

import de.uscoutz.nexus.regions.Region;
import de.uscoutz.nexus.schematic.schematics.BuiltSchematic;
import de.uscoutz.nexus.schematic.schematics.Schematic;
import de.uscoutz.nexus.schematic.schematics.SchematicType;
import de.uscoutz.nexus.wave.NexusWavePlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class MoveToNexusGoal extends MoveToBlockGoal {

    private static final int GIVE_UP_TICKS = 100;
    private boolean shouldStop;
    private int tries;

    private Location nexusLocation, nearest;
    private double distance = Double.MAX_VALUE;

    public MoveToNexusGoal(PathfinderMob mob) {
        super(mob, 1.0, 100, 20);
        nexusLocation = NexusWavePlugin.getInstance().getSchematicPlugin().getSchematicManager().getSchematicProfileMap().get(
                NexusWavePlugin.getInstance().getSchematicPlugin().getNexusPlugin().getWorldManager().getWorldProfileMap().get(
                        mob.getBukkitEntity().getWorld()).getProfileId()).getSchematics().get(SchematicType.NEXUS).get(0).getBoundingBox().getCenter().toLocation(mob.getBukkitEntity().getWorld());
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        final Region[] region = new Region[1];
        Location location = new Location(mob.getBukkitEntity().getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());

        Map<Region, BuiltSchematic> schematics = NexusWavePlugin.getInstance().getSchematicPlugin().getSchematicManager().getSchematicProfileMap().get(
                NexusWavePlugin.getInstance().getSchematicPlugin().getNexusPlugin().getWorldManager().getWorldProfileMap().get(
                        mob.getBukkitEntity().getWorld()).getProfileId()).getSchematicsByRegion();
        region[0] = NexusWavePlugin.getInstance().getSchematicPlugin().getNexusPlugin().getRegionManager().getRegion(location);

        boolean valid;

        if(tries >= 1000) {
            valid = location.distance(nexusLocation) < mob.getBukkitEntity().getLocation().distance(nexusLocation);
            tries = 0;
        } else {
            valid = region[0] != null && schematics.containsKey(region[0]) && schematics.get(region[0]).getSchematic().getSchematicType() == SchematicType.NEXUS;
        }
        tries++;

        return valid;
    }

    /*@Override
    public boolean shouldRecalculatePath() {
        return this.tryTicks % 10 == 0;
    }*/

    @Override
    public void start() {
        super.start();
        shouldStop = false;
    }

    @Override
    public void stop() {
        this.shouldStop = true;
    }

    @Override
    protected boolean isReachedTarget() {
        return super.isReachedTarget() || NexusWavePlugin.getInstance().getSchematicPlugin().getNexusPlugin().getRegionManager().getRegion(mob.getBukkitEntity().getLocation()) != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !shouldStop && !this.isReachedTarget() && this.tryTicks <= GIVE_UP_TICKS && this.isValidTarget(this.mob.level, this.blockPos);
    }
}