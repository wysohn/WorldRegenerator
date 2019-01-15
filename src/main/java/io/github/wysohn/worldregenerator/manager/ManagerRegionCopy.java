package io.github.wysohn.worldregenerator.manager;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;
import io.github.wysohn.rapidframework.pluginbase.constants.SimpleLocation;
import io.github.wysohn.rapidframework.utils.locations.LocationUtil;
import io.github.wysohn.worldregenerator.main.WorldRegenerator;

public class ManagerRegionCopy extends PluginManager<WorldRegenerator> {
	public ManagerRegionCopy(WorldRegenerator base, int loadPriority) {
		super(base, loadPriority);
	}

	@Override
	protected void onEnable() throws Exception {
		
	}

	@Override
	protected void onDisable() throws Exception {
		
	}

	@Override
	protected void onReload() throws Exception {
		
	}

	/**
	 * This method is blocking. Make sure that it's not scheduled on server thread.
	 * @param areas
	 * @param to
	 * @param handle
	 * @return
	 */
	public boolean copyRegions(List<Area> areas, World to, ProgressHandler handle) {
		int size = 0;
		for(Area area : areas)
			size += area.size();
		
		int blockProgressed = 0;
		int currentArea = 0;
		
		try {
			out:for(Area area : areas) {
				for(SimpleLocation sloc : area) {
					Location loc = LocationUtil.convertToBukkitLocation(sloc);
					Future<Void> future = Bukkit.getScheduler().callSyncMethod(base, ()->{
						Block origin = loc.getBlock();
						Block target = to.getBlockAt(sloc.getX(), sloc.getY(), sloc.getZ());
						target.setType(origin.getType());
						return null;
					});
					future.get();
					blockProgressed++;
					
					if(!handle.onProgress(currentArea, areas.size(), blockProgressed, size)) {
						break out;
					}
				}
				
				currentArea++;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} 
		return true;
	}
	
	@FunctionalInterface
	public interface ProgressHandler{
		/**
		 * 
		 * @param currentArea
		 * @param areaTotal
		 * @param blockProgress
		 * @param blockTotal This number equals the blockProgress indicates that the task is complete
		 * @return return false to stop the task in between the progression. true otherwise.
		 */
		boolean onProgress(int currentArea, int areaTotal, int blockProgress, int blockTotal);
	}
}
