package io.github.wysohn.worldregenerator.api;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.World;

import io.github.wysohn.rapidframework.pluginbase.constants.Area;

public interface IAreaInfoSource {
	/**
	 * Get Areas stored in other plugins.
	 * @param worlds can be null, then it means all worlds loaded
	 * @param targets can be null, then it means all names
	 * @return 
	 */
	List<Area> getRegions(Predicate<String> worlds, Predicate<String> targets);
}
