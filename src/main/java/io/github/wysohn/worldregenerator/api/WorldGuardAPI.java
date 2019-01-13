package io.github.wysohn.worldregenerator.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;

public class WorldGuardAPI extends APISupport {
	private WorldGuardPlugin wg;
	public WorldGuardAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		wg = WGBukkit.getPlugin();

		return true;
	}

	public List<Area> getRegions(World world, Set<String> targets){
		RegionManager rm = wg.getRegionManager(world);
		if(rm == null)
			return new ArrayList<>();
		
		return targets.stream()
				.map((name)->rm.getRegion(name))
				.map((region) -> {
					BlockVector min = region.getMinimumPoint();
					BlockVector max = region.getMaximumPoint();
					return Area.formAreaBetweenTwoPoints(world.getName(), 
							min.getBlockX(), min.getBlockY(),min.getBlockZ(), 
							max.getBlockX(), max.getBlockY(), max.getBlockZ());
				})
				.collect(Collectors.toList());
	}
}
