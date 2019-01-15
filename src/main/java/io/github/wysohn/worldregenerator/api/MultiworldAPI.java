package io.github.wysohn.worldregenerator.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.worldregenerator.main.WorldRegeneratorLanguage;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import multiworld.MultiWorldPlugin;
import multiworld.api.ConfigurationSaveException;
import multiworld.api.MultiWorldWorldData;
import multiworld.api.flag.FlagName;

public class MultiworldAPI extends APISupport {
	private MultiWorldPlugin mw;
	
	private Set<UUID> underProgress = new HashSet<>();
	
	public MultiworldAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		mw = (MultiWorldPlugin) Bukkit.getPluginManager().getPlugin(this.getPluginName());
		return true;
	}

	
	/**
	 * Create a new world based on given seed and copy world settings of oldWorld.
	 * @param worldName
	 * @param oldWorld
	 * @param seed
	 * @return true if created; false if worldName is already in use
	 */
	public boolean createNewWorld(String worldName, World oldWorld, long seed) {
		if(Bukkit.getWorld(worldName) != null)
			return false;

		Bukkit.createWorld(WorldCreator.name(worldName).copy(oldWorld).seed(seed));
		MultiWorldWorldData oldData = mw.getApi().getWorld(oldWorld.getName());
		MultiWorldWorldData worldData = mw.getApi().getWorld(worldName);
		Stream.of(FlagName.values()).forEach((flag)->{
			copyFlags(oldData, worldData, flag);
		});

		return true;
	}

	private void copyFlags(MultiWorldWorldData oldData, MultiWorldWorldData worldData, FlagName flag) {
		try {
			worldData.setOptionValue(flag, oldData.getOptionValue(flag));
		} catch (ConfigurationSaveException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Delete the world completely
	 * @param worldName
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteWorld(World world) {
		Bukkit.getOnlinePlayers().forEach((player)->{
			base.lang.addString(world.getName());
			String msg = base.lang.parseFirstString(player, WorldRegeneratorLanguage.API_Multiworld_KickedAsInDeletingWorld);
			player.kickPlayer(msg);
		});
		
		MultiWorldWorldData worldData = mw.getApi().getWorld(world.getName());
		try {
			worldData.unloadWorld();
		} catch (ConfigurationSaveException e) {
			e.printStackTrace();
			return false;
		}
		
		mw.getDataManager().getWorldManager().deleteWorld(world.getName());
		return true;
	}
	
	/**
	 * rename the world
	 * @param worldName
	 * @param worldNameNew
	 * @param onFinish
	 */
	public boolean renameWorld(World world, String worldNameNew) {
		MultiWorldWorldData worldData = mw.getApi().getWorld(world.getName());
		try {
			worldData.unloadWorld();
		} catch (ConfigurationSaveException e) {
			e.printStackTrace();
			return false;
		}
		
		File worldFolder = world.getWorldFolder();
		File toFolder = new File(worldFolder.getParentFile(), worldNameNew);
		try {
			Files.move(worldFolder.toPath(), toFolder.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				worldData.loadWorld();
			} catch (ConfigurationSaveException e) {
				e.printStackTrace();
			}
		}
		
		MultiWorldWorldData newWorldData = mw.getApi().getWorld(worldNameNew);
		Stream.of(FlagName.values()).forEach((flag)->{
			copyFlags(worldData, newWorldData, flag);
		});
		try {
			newWorldData.loadWorld();
		} catch (ConfigurationSaveException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
