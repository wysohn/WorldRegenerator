package io.github.wysohn.worldregenerator.api;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;
import subside.plugins.koth.KothPlugin;

public class KothAPI extends APISupport {
	private KothPlugin koth;
	
	public KothAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		koth = (KothPlugin) Bukkit.getPluginManager().getPlugin(getPluginName());
		return true;
	}

	public List<Area> getRegions() {
		return koth.getKothHandler().getAvailableKoths().stream()
				.map((koth) -> koth.getAreas())
				.flatMap(List::stream)
				.map((area) -> {
					return Area.formAreaBetweenTwoPoints(area.getName(), area.getMin().getBlockX(),
							area.getMin().getBlockY(), area.getMin().getBlockZ(), area.getMax().getBlockX(),
							area.getMax().getBlockY(), area.getMax().getBlockZ());
				})
				.collect(Collectors.toList());
	}
}
