package io.github.wysohn.worldregenerator.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.wysohn.rapidframework.pluginbase.PluginConfig;

public class WorldRegeneratorConfig extends PluginConfig {
	public String Settings_TargetWorld = "world";
	public List<String> Settings_WorldGuardAPI_TargetRegions = new ArrayList<>();
	
	public String Some_Thing = "Something";
	public ConfigurationSection Some_That = new YamlConfiguration();
	{
		Some_That.set("test1", "val1");
		Some_That.set("test1", "val2");
	}
}
