package io.github.wysohn.worldregenerator.main;

import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;

public enum WorldRegeneratorLanguage implements Language {
	Manager_PlayerConnection_KickMessage("&cYou are kicked as world regeneration will start soon."), 
	
	API_Multiworld_KickedAsInDeletingWorld("&cCan't stay in the ${string} as it will be deleted."),
	;
	
	private String[] def;
	
	private WorldRegeneratorLanguage(String... def) {
		this.def = def;
	}

	@Override
	public String[] getEngDefault() {
		return def;
	}

}
