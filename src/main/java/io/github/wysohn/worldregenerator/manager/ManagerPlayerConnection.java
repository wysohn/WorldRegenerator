package io.github.wysohn.worldregenerator.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.worldregenerator.main.WorldRegenerator;
import io.github.wysohn.worldregenerator.main.WorldRegeneratorConfig;
import io.github.wysohn.worldregenerator.main.WorldRegeneratorLanguage;

public class ManagerPlayerConnection extends PluginManager<WorldRegenerator> implements Listener {
	private boolean connectionAllowed = true;
	
	public ManagerPlayerConnection(WorldRegenerator base, int loadPriority) {
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

	public boolean isConnectionAllowed() {
		return connectionAllowed;
	}
	
	public void setConnectionAllowed(boolean connectionAllowed) {
		setConnectionAllowed(connectionAllowed, false);
	}

	public void setConnectionAllowed(boolean connectionAllowed, boolean kick) {
		this.connectionAllowed = connectionAllowed;
		
		if(!this.connectionAllowed && kick) {
			Bukkit.getOnlinePlayers().stream()
			.filter((player)->!player.hasPermission(base.commandExecutor.adminPermission))
			.forEach((player)->{
				String msg = base.lang.parseFirstString(player, WorldRegeneratorLanguage.Manager_PlayerConnection_KickMessage);
				player.kickPlayer(msg);
			});
		}
	}

	public boolean isConnectionAllowed(Player player) {
		if(player.hasPermission(base.commandExecutor.adminPermission))
			return true;
		
		return connectionAllowed;
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent ev) {
		if(isConnectionAllowed(ev.getPlayer()))
			return;
		
		ev.setResult(Result.KICK_WHITELIST);
	}
}
