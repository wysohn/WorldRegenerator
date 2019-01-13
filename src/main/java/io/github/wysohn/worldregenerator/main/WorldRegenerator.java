package io.github.wysohn.worldregenerator.main;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.commands.SubCommand;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.worldregenerator.api.MultiworldAPI;
import io.github.wysohn.worldregenerator.api.WorldBorderAPI;
import io.github.wysohn.worldregenerator.api.WorldGuardAPI;
import io.github.wysohn.worldregenerator.manager.ManagerExample1;
import io.github.wysohn.worldregenerator.manager.ManagerPlayerConnection;
import io.github.wysohn.worldregenerator.manager.ManagerRegenProcess;
import io.github.wysohn.worldregenerator.manager.ManagerRegionCopy;

public class WorldRegenerator extends PluginBase {

	public WorldRegenerator() {
		super(new WorldRegeneratorConfig(), "worldregenerator", "worldregenerator.admin");
	}

	@Override
	protected void preEnable() {

	}

	@Override
	protected Stream<Language> initLangauges() {
		return Stream.of(WorldRegeneratorLanguage.values());
	}

	@Override
	protected Stream<SubCommand> initCommands() {
		List<SubCommand> list = Collections.emptyList();
		list.add(SubCommand.Builder.forCommand("command1", this, 1)
				.actOnConsole((sender, args) -> {
					sendMessage(sender, DefaultLanguages.Plugin_NotEnabled); 
					return true;
				})
				.actOnPlayer((sender, args)->{
					sendMessage(sender, DefaultLanguages.Plugin_NotEnabled); 
					return true;
				})
				.create());
		return list.stream();
	}

	@Override
	protected Stream<Entry<String, Class<? extends PluginAPISupport.APISupport>>> initAPIs() {
		Map<String, Class<? extends PluginAPISupport.APISupport>> map = Maps.newHashMap();
		map.put("MultiWorld", MultiworldAPI.class);
		map.put("WorldBorder", WorldBorderAPI.class);
		map.put("WorldGuard", WorldGuardAPI.class);
		return map.entrySet().stream();
	}

	@Override
	protected Stream<PluginManager<? extends PluginBase>> initManagers() {
		List<PluginManager<? extends PluginBase>> list = Collections.emptyList();
		list.add(new ManagerPlayerConnection(this, PluginManager.NORM_PRIORITY));
		list.add(new ManagerRegenProcess(this, PluginManager.SLOWEST_PRIORITY));
		list.add(new ManagerRegionCopy(this, PluginManager.NORM_PRIORITY));
		return list.stream();
	}

}
