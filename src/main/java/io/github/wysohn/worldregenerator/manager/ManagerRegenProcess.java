package io.github.wysohn.worldregenerator.manager;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;

import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.manager.tasks.ManagerSequentialTask;
import io.github.wysohn.rapidframework.pluginbase.manager.tasks.ManagerSequentialTask.Tasks;
import io.github.wysohn.worldregenerator.main.WorldRegenerator;
import io.github.wysohn.worldregenerator.main.WorldRegeneratorConfig;
import net.minecraft.util.org.apache.commons.lang3.RandomStringUtils;
import io.github.wysohn.worldregenerator.api.*;

public class ManagerRegenProcess extends PluginManager<WorldRegenerator> {
	private static final ExecutorService pool = Executors.newSingleThreadExecutor();
	
	private final Map<State, Runnable> tasks = new EnumMap<State, Runnable>(State.class);
	/**
	 * the state must reflect the task 'that is already finished.'
	 */
	private State state = State.NONE;
	
	private WorldRegeneratorConfig config;
	
	private MultiworldAPI multiworldAPI;
	private WorldBorderAPI worldBorderAPI;
	private WorldGuardAPI worldGuardAPI;
	private KothAPI kothAPI;
	
	private ManagerSequentialTask managerSequentialTask;
	private ManagerPlayerConnection managerPlayerConnection;
	private ManagerRegionCopy managerRegionCopy;
	
	private String tempWorldName;
	private Location lastSpawnLocation;
	
	private Future<Void> runningTask = null;
	
	public ManagerRegenProcess(WorldRegenerator base, int loadPriority) {
		super(base, loadPriority);
	}

	@Override
	protected void onEnable() throws Exception {
		this.config = base.getPluginConfig();
		
		this.multiworldAPI = base.APISupport.getAPI("MultiWorld");
		this.worldBorderAPI = base.APISupport.getAPI("WorldBorder");
		this.worldGuardAPI = base.APISupport.getAPI("WorldGuard");
		this.kothAPI = base.APISupport.getAPI("KoTH");
		
		this.managerSequentialTask = base.getManager(ManagerSequentialTask.class);
		this.managerPlayerConnection = base.getManager(ManagerPlayerConnection.class);
		this.managerRegionCopy = base.getManager(ManagerRegionCopy.class);
	}

	private World validateAndGetTargetWorld(String targetWorldName) {
		World world = Bukkit.getWorld(targetWorldName);
		if(world == null)
			throw new RuntimeException(targetWorldName+" does not exist!");
		return world;
	}
	
	private Location getSafeSpawnLocation(World world, int x, int z) {
		ChunkSnapshot chunk = world.getChunkAt(x >> 4, z >> 4).getChunkSnapshot();
		return new Location(world, x, chunk.getHighestBlockYAt(x, z), z).add(0.5, 0.5, 0.5);
	}

	@Override
	protected void onDisable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReload() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public State getState() {
		return state;
	}

	// Start process
	public boolean start() {
		if(runningTask != null && !runningTask.isDone())
			return false;
		
		validate(this.config);
		validate(this.config.Settings_TargetWorld);
		
		validate(this.multiworldAPI);
		validate(this.worldBorderAPI);
		validate(this.worldGuardAPI);
		validate(this.kothAPI);
		
		validate(this.managerSequentialTask);
		validate(this.managerPlayerConnection);
		validate(this.managerRegionCopy);
		
		tempWorldName = config.Settings_TargetWorld+"_temp";
		
		runningTask = this.managerSequentialTask.schedule(Tasks.Builder
				.startWith(base, "Disallow Connections", ()->{
					managerPlayerConnection.setConnectionAllowed(false, true);
				})
				.then("Create World", ()->{
					World world = validateAndGetTargetWorld(config.Settings_TargetWorld);
					lastSpawnLocation = world.getSpawnLocation();
					
					multiworldAPI.createNewWorld(tempWorldName, world, new Random().nextLong());
				})
				.then("World Pregen", ()->{
					World world = validateAndGetTargetWorld(tempWorldName);
					
					worldBorderAPI.preGenerate(world, (percent) -> {
						base.getLogger().info(State.WORLDPREGEN.name()+" progress: "+percent+"%");
					});
				})
				.then("Copying Regions", () -> {
					World to = validateAndGetTargetWorld(tempWorldName);

					managerRegionCopy.copyRegions(
							worldGuardAPI.getRegions(worldName -> worldName.equals(config.Settings_TargetWorld),
									areaName -> config.Settings_WorldGuardAPI_TargetRegions.contains(areaName)),
							to,
							(area, areaTotal, block, blockTotal) -> {
								base.getLogger().info(State.REGIONCOPY.name() + " Areas: " + area + "/" + areaTotal + " "
										+ "Blocks: " + block + "/" + blockTotal);
								return true;
							});
				})
				.then("Rename worlds", () -> {
					World world = validateAndGetTargetWorld(config.Settings_TargetWorld);
					World newWorld = validateAndGetTargetWorld(tempWorldName);

					multiworldAPI.renameWorld(world, config.Settings_TargetWorld + "_bak");
					multiworldAPI.renameWorld(newWorld, config.Settings_TargetWorld);
				})
				.then("Set initial spawn", () -> {
					World world = validateAndGetTargetWorld(config.Settings_TargetWorld);
					world.setSpawnLocation(
							getSafeSpawnLocation(world, lastSpawnLocation.getBlockX(), lastSpawnLocation.getBlockZ()));
				})
				.then("Allow connections", ()->{
					managerPlayerConnection.setConnectionAllowed(true);
					
					tempWorldName = null;
					lastSpawnLocation = null;
				})
				.build());
		return true;
	}

	private void validate(Object obj) {
		if(obj == null)
			throw new RuntimeException("Cannot be null");
	}
	
	public enum State{
		NONE,
		DISALLOWCONNECTION,
		CREATEWORLD,
		WORLDPREGEN,
		REGIONCOPY,
		WORLDRENAME,
		RESETSPAWN,
		ALLOWCONNECTION,
	}
}
