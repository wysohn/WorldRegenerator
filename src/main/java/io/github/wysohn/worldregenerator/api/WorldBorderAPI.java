package io.github.wysohn.worldregenerator.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
	 * This method is blocking. Make sure that it's not scheduled on the server thread.
	 * @param world
	 * @param onFinish
	 * @return true if scheduled; false if already running
	 */
	public boolean preGenerate(World world, WorldFillTaksFuture.ProgressHandle onFinish) {
		String worldName = world.getName();
		if (task != null && task.valid())
			return false;

		task = new WorldFillTaksFuture(Bukkit.getServer(), null, worldName, 5, 1, 1, false, onFinish);
		Future<Void> future = Bukkit.getScheduler().callSyncMethod(base, ()->{task.run(); return null;});
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static class WorldFillTaksFuture extends WorldFillTask{
		private final ProgressHandle onFinish;
		private WorldFillTaksFuture(Server theServer, Player player, String worldName, int fillDistance,
				int chunksPerRun, int tickFrequency, boolean forceLoad, ProgressHandle onFinish) {
			super(theServer, player, worldName, fillDistance, chunksPerRun, tickFrequency, forceLoad);
			this.onFinish = onFinish;	
		}
		@Override
		public boolean moveToNext() {
			boolean next = super.moveToNext();
			if(!next) {
				onFinish.onProgress(getPercentageCompleted());
			}
			return next;
		}



		@Override
		public void finish() {
			super.finish();
			onFinish.onProgress(getPercentageCompleted());
		}
		
		@FunctionalInterface
		public interface ProgressHandle{
			void onProgress(double percent);
		}
	}
}
