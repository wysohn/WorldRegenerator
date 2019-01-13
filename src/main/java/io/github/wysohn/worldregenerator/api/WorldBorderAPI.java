package io.github.wysohn.worldregenerator.api;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.wimbli.WorldBorder.WorldBorder;
import com.wimbli.WorldBorder.WorldFillTask;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;

public class WorldBorderAPI extends APISupport {
	private WorldBorder wb;
	private WorldFillTaksFuture task;
	
	public WorldBorderAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		wb = (WorldBorder) Bukkit.getPluginManager().getPlugin(getPluginName());
		
		return true;
	}
	
	/**
	 * 
	 * @param world
	 * @param onFinish
	 * @return true if scheduled; false if already running
	 */
	public boolean preGenerate(World world, Runnable onFinish) {
		String worldName = world.getName();
		if (task != null && task.valid())
			return false;

		task = new WorldFillTaksFuture(Bukkit.getServer(), null, worldName, 5, 1, 1, false, onFinish);
		return true;
	}
	
	private static class WorldFillTaksFuture extends WorldFillTask{
		private final Runnable onFinish;
		public WorldFillTaksFuture(Server theServer, Player player, String worldName, int fillDistance,
				int chunksPerRun, int tickFrequency, boolean forceLoad, Runnable onFinish) {
			super(theServer, player, worldName, fillDistance, chunksPerRun, tickFrequency, forceLoad);
			this.onFinish = onFinish;	
		}
		@Override
		public void finish() {
			super.finish();
			onFinish.run();
		}
		
	}
}
