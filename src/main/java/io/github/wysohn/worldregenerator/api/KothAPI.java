package io.github.wysohn.worldregenerator.api;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.constants.Area;
import subside.plugins.koth.KothPlugin;

public class KothAPI extends APISupport implements IAreaInfoSource{
	private KothPlugin koth;
	
	public KothAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		koth = (KothPlugin) Bukkit.getPluginManager().getPlugin(getPluginName());
		return true;
	}

	public List<Area> getRegions(Predicate<String> world, Predicate<String> targets) {
		return Bukkit.getWorlds().stream().filter((targetWorld) -> world == null || world.test(targetWorld.getName()))
				.map((targetWorld) -> {
					return koth.getKothHandler().getAvailableKoths().stream()
							.filter((koth) -> targets == null || targets.test(koth.getName()))
							.map((koth) -> koth.getAreas())
							.flatMap(List::stream)
							.filter((area) -> world == null || world.test(area.getName())).map((area) -> {
								return Area.formAreaBetweenTwoPoints(area.getName(), area.getMin().getBlockX(),
										area.getMin().getBlockY(), area.getMin().getBlockZ(), area.getMax().getBlockX(),
										area.getMax().getBlockY(), area.getMax().getBlockZ());
							});
				})
				.flatMap(Function.identity())
				.collect(Collectors.toList());
	}
}
