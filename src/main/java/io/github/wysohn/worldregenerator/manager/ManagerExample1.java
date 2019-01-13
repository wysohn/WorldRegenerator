package io.github.wysohn.worldregenerator.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.worldregenerator.main.WorldRegenerator;

public class ManagerExample1 extends PluginManager<WorldRegenerator> {

	public ManagerExample1(WorldRegenerator base, int loadPriority) {
		super(base, loadPriority);
	}

	@Override
	protected void onEnable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDisable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReload() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
