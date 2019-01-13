package io.github.wysohn.worldregenerator.api;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;

public class SomeAPI extends APISupport {

	public SomeAPI(PluginBase base) {
		super(base);
	}

	@Override
	public boolean init() throws Exception {
		return true;
	}

}
