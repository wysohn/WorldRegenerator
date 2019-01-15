package io.github.wysohn.worldregenerator.api;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;

public class WorldGuardAPI extends APISupport implements IAreaInfoSource{
	private WorldGuardPlugin wg;
	public WorldGuardAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		wg = WGBukkit.getPlugin();

		return true;
	}

	public List<Area> getRegions(Predicate<String> world, Predicate<String> targets){
		return Bukkit.getWorlds().stream()
			.filter((targetWorld) -> world == null || world.test(targetWorld.getName()))
			.map((targetWorld) -> {
				RegionManager rm = wg.getRegionManager(targetWorld);
				return rm.getRegions().entrySet().stream()
						.filter((entry) -> targets == null || targets.test(entry.getKey()))
						.map((entry) -> {
							ProtectedRegion region = entry.getValue();
							BlockVector min = region.getMinimumPoint();
							BlockVector max = region.getMaximumPoint();
							return Area.formAreaBetweenTwoPoints(targetWorld.getName(),
									min.getBlockX(), min.getBlockY(), min.getBlockZ(),
									max.getBlockX(), max.getBlockY(), max.getBlockZ());
						});
						
			})
			.flatMap(Function.identity())
			.collect(Collectors.toList());
	}
}
